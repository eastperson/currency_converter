# 환율 계산 애플리케이션 - 김동인

<a href="https://github.com/wirebarley/apply/blob/master/coding_test.md">문제 소스</a>

##1. 개요

DB
* Redis

백엔드
* Currency Layer API
* Retrofit2
* Scheduler & Polling
* Validation

프론트엔드
* Thymeleaf
* Vanilla JS

##2. 아키텍쳐
<h4>멀티 모듈 설계</h4>
<p>
백엔드와 프론트엔드의 상호 간의 의존성을 제거하기 위해 멀티 모듈의 형식으로 구성했습니다. 
도메인이나 공통적으로 사용되는 클래스를 모아놓은 core, REST API 형식으로 비즈니스 로직을 구현한 api, ui를 보여주는 front로 나누었습니다.
</p>

<h4>백엔드 구성</h4>

* API 호출 Retrofit2 & Polling & Scheduler

통신 성능이 뛰어나고 편의성이 좋은 Retrofit2를 사용하여 Currency Layer API와 통신합니다. 기존에는 서버 api를 호출하면 그때 그때
  외부 API와 통신하도록 비즈니스 로직을 구성했습니다. 하지만 Currency Layer는 회원 등급에 따라 매일, 매분, 매시 각각 다른 정보를
  주고 있었습니다. 무료 서비스는 Daily로 값을 달리하여 일 1회만 값을 저장하면 되었지만, 확장성을 고려하기 위해 매분, 매초에도 정보를
  가져올 수 있도록 유연하게 구성하고 싶었습니다. 그래서 Scheduler를 활용하여 Polling 방식으로 일정 주기에 따라 api를 요청할 수 있도록
  구성했습니다.

* 실시간 데이터 처리
  
초단위로 값이 변경되는 데이터는 RDBMS를 사용하기에 다소 무겁다는 생각이 들었습니다. 또한 주기에 따라 클라이언트 쪽에서의 요청이 잦아질
수 있어 퍼포먼스를 가장 높일 수 있는 DB를 고민했고 실제 웹소켓과 함께 실시간 데이터 처리를 사용하는데 유용한 Redis를 사용했습니다.
Redis는 속도가 빠르고 데이터의 생명주기를 부여할 수 있어서 메모리 관리가 유용합니다.

외부 API 호출은 실패의 가능성이 있어서 요청한 시간의 해당하는 데이터가 반드시 저장된다는 보장이 없습니다. 
따라서 Redis의 Sorted Set을 사용하여 응답 데이터에 포함되어있는 timestamp를 score로 등록하고 
성공적으로 응답된 가장 최신의  데이터를 불러올 수 있도록 설계했습니다. 추후에 websocket을 연동한 사용을 고려했습니다.

* 유효성 검사

테스트 요구사항에서 송금 금액의 유효성 검사를 체크하는 내용을 Validator로 구현했습니다. Dispatcher Servlet에서 Controller로
요청 파라미터를 바인딩 할 때, @InitBinder 어노테이션을 사용하여 유효성 검사를 선행합니다. 유효성 검사는 요청들어온 값이 정상적인
타입(enum,Integer)으로 바인딩이 되었는지와 값의 크기를 확인하였습니다.

* 예외 처리
예외처리는 하나의 Exception을 생성하여 RestControllerAdvice로 대응하였습니다. 프론트엔드와 약속된 
  response code와 message를 반환하여 응답 코드에 따라 로직을 달리할 수 있도록 response body를 반환하고 있습니다.


<h4>프론트엔드 구성</h4>
* polling
* Async/Await

<h4>배포</h4>
* AWS 배포

##3. 레퍼런스
