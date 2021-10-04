# 환율 계산 애플리케이션 - 김동인

<a href="https://github.com/wirebarley/apply/blob/master/coding_test.md">문제 소스</a>

클라이언트<br/>
http://3.36.42.9/currency

API<br/>
http://3.36.42.9/api/currency/current

## 1. 개요

[1차 개발]

Client에서 API를 호출할 때마다 Currency Layer API를 호출하는 방식.

1. Client 진입 시 환율 데이터를 가져오는 서버 API 호출.
2. 환율 계산 시 통화 코드와 통화량을 파라미터로 서버 API에 송신하여 결괏값 반환.

이슈
* API를 호출할 때마다 외부 API를 호출하므로 성능 이슈 발생(응답 시간 700ms~1500ms)
* 페이지 진입 시 확인했던 환율 값과 계산에 들어가는 환율 값이 요청 시간에 따라 싱크가 맞지 않는 이슈 발생

[2차 개발]

서버에서 특정 주기로 Currency Layer API를 호출하여 데이터를 DB에 저장하는 방식. Client에서 서버 API를 호출할 때 Redis에서 실시간 데이터를 반환하여 성능 개선. 데이터를 업데이트하는 주기를 조정할 수 있어 초 단위의 실시간 데이터 처리를 위한 확장성에 용이하다고 판단.

* Currency Layer API 업데이트 주기에 따라 API를 호출해서 Redis에 저장
* Redis Sorted Set 자료구조를 사용하여 가장 최신의 데이터를 반환(응답 시간 20ms~100ms)
* Client에서 polling 방식으로 서버 API를 호출하여 화면에 있는 값을 서버와 동기화
* Client에서 최신화되어있는 데이터를 저장하며 환율 계산하여 성능 개선


DB
* Redis

백엔드
* Java 11
* Spring Boot 2.x
* Spring Data Redis
* Retrofit2
* Scheduler
* Validation
* Currency Layer API

프론트엔드
* Thymeleaf
* Vanilla JS


## 2. 아키텍처 및 비즈니스 로직

### 멀티 모듈 설계

백엔드와 프론트엔드의 상호 간 의존성을 제거하기 위해 멀티 모듈의 형식으로 구성했습니다.
도메인이나 공통으로 사용되는 클래스를 모아놓은 core, REST API 형식으로 비즈니스 로직을 구현한 API,
UI를 보여주는 front로 나누었습니다.


### 백엔드 구성

* 외부 API 호출 Retrofit2 & Scheduler

통신 성능이 뛰어나고 편의성이 좋은 Retrofit2를 사용하여 Currency Layer API와 통신합니다. 1차 개발 시 서버 API를 호출되면 그때그때
외부 API와 통신하도록 비즈니스 로직을 구성했습니다. 하지만 Currency Layer는 [Subscription Plan](https://currencylayer.com/product)에 따라 매일, 매분, 매시 각각 다른 정보를
주고 있었습니다. Free 서비스는 Daily로 값을 달리하여서 일 1회만 값을 저장하면 되었지만, 확장성을 고려하기 위해 매분, 매초에도 정보를
가져올 수 있도록 유연하게 구성하고 싶었습니다. 그래서 Scheduler를 활용하여 Polling 방식으로 일정 주기에 따라 API를 요청할 수 있도록
구성했습니다.

[Retrofit2 (currency_converter/core/src/main/java/com/wirebarley/core/component/currency_layer)](https://github.com/eastperson/currency_converter/tree/master/core/src/main/java/com/wirebarley/core/component/currency_layer) 

[Scheduler (currency_converter/API/src/main/java/com/wirebarley/API/scheduler/CurrencyScheduler.java)](https://github.com/eastperson/currency_converter/blob/master/API/src/main/java/com/wirebarley/API/scheduler/CurrencyScheduler.java) 


* 실시간 데이터 처리

초 단위로 값이 변경될 수 있는 데이터를 RDBMS에 저장하기는 무겁다는 생각이 들었습니다. 또한 주기에 따라 클라이언트 쪽에서의 요청이 잦아질
수 있어 퍼포먼스를 가장 높일 수 있는 DB를 고민했고 실제 웹소켓과 함께 실시간 데이터 처리를 사용하는 데 유용하게 쓰이는 Redis를 사용했습니다.


* 유효성 검사

코딩 테스트 요구사항에서 송금 금액의 유효성 검사 내용을 Validator로 구현했습니다. Dispatcher Servlet에서 Controller로
요청 파라미터를 바인딩할 때, @InitBinder 어노테이션을 사용하여 유효성 검사를 선행합니다. 유효성 검사는 요청 들어온 값이 정상적인
타입(enum,Integer)으로 바인딩이 되었는지와 값의 크기를 확인하였습니다. 검사를 탈락한 데이터는 Exception을 발생 시켜 404 에러 코드를 반환합니다.

[Validator (currency_converter/api/src/main/java/com/wirebarley/api/validation/CurrencyConvertRequestValid.java)](https://github.com/eastperson/currency_converter/blob/master/api/src/main/java/com/wirebarley/api/validation/CurrencyConvertRequestValid.java) 

[RestController (currency_converter/api/src/main/java/com/wirebarley/api/web/rest/CurrencyConvertRest.java)](https://github.com/eastperson/currency_converter/blob/master/api/src/main/java/com/wirebarley/api/web/rest/CurrencyConvertRest.java)


* 예외 처리

예외처리는 하나의 Exception을 생성하여 RestControllerAdvice로 대응하였습니다. 프론트엔드와 약속된
response code와 message를 반환하여 응답 코드에 따라 로직을 달리할 수 있도록 구성했습니다.


[RestControllerAdvice (currency_converter/api/src/main/java/com/wirebarley/api/web/advice/CurrencyApiRestAdvice.java )](https://github.com/eastperson/currency_converter/blob/master/api/src/main/java/com/wirebarley/api/web/advice/CurrencyApiRestAdvice.java)

[Exception (currency_converter/api/src/main/java/com/wirebarley/api/exception/CurrencyConvertException.java )](https://github.com/eastperson/currency_converter/blob/master/api/src/main/java/com/wirebarley/api/exception/CurrencyConvertException.java)

### 테스트

테스트는 MockMVC를 활용하여 각 API의 엔드포인트가 확실한 값을 반환하는지를 테스트했습니다.
또한 오류의 상황에서도 적절한 에러 코드를 반환하는지를 같이 테스트하였습니다.

성능은 Postman을 활용해서 요청/응답 시간으로 확인했습니다.

[MockMVC 테스트 (currency_converter/api/src/test/java/com/wirebarley/api/rest/CurrencyEntityConvertRestTests.java )](https://github.com/eastperson/currency_converter/blob/master/api/src/test/java/com/wirebarley/api/rest/CurrencyEntityConvertRestTests.java)<br/>

## 3. 배포

<img src="https://user-images.githubusercontent.com/66561524/135833407-69804c6b-f9ad-48eb-ab20-dde06cff4e5c.png"/>

* AWS EC2 배포

1개의 인스턴스에서 멀티 모듈인 client와 server를 구동했습니다.

* Docker & Redis 설치

Redis의 설치와 실행을 편리하게 하기 위해 Docker를 활용했습니다.

* Nginx 구축

client와 server의 각각의 포트를 1개의 도메인으로 요청을 받기 위해 Ngnix를 활용하여 포트 포워딩을 했습니다.
