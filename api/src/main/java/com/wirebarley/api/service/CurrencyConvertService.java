package com.wirebarley.api.service;

import com.google.gson.Gson;
import com.wirebarley.api.exception.CurrencyConvertException;
import com.wirebarley.api.model.ConvertedResultView;
import com.wirebarley.api.model.CurrencyConvertRequest;
import com.wirebarley.core.component.currency_layer.client.CurrencyLayerClient;
import com.wirebarley.core.component.currency_layer.domain.CurrencyLayerResponse;
import com.wirebarley.core.component.currency_layer.domain.CurrencyType;
import com.wirebarley.core.constant.ResponseCode;
import com.wirebarley.core.entity.CurrencyEntity;
import com.wirebarley.core.properties.CurrencyLayerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.wirebarley.core.entity.CurrencyEntity.convertToCurrency;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConvertService {

    private final CurrencyLayerProperties currencyLayerProperties;
    private final StringRedisTemplate redisTemplate;
    private final Gson gson;

    private static String REDIS_CURRENCY_KEY = "currency_set";

    /**
     * Currency Layer API를 호출하여
     * 현재 currency 정보를 가져온다.
     * @return
     */
    public CurrencyLayerResponse currentCurrency(){
        Call<CurrencyLayerResponse> currencyLayerResponseCall = CurrencyLayerClient.getApiService().getCurrency(
                currencyLayerProperties.getAccessKey(),
                CurrencyType.USD.name(),
                "1",
                parseCurrencies(CurrencyType.KRW.name(),CurrencyType.JPY.name(),CurrencyType.PHP.name())
        );
        try {
            return currencyLayerResponseCall.execute().body();
        } catch (IOException e) {
            throw new CurrencyConvertException(ResponseCode.INTERNAL_ERROR,"API 호출 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Currency Layer API를 호출하여 가져온 currency 데이터를
     * Redis에 sorted set으로 저장한다. score는 timestamp로 저장한다.
     *
     */
    public void saveCurrentCurrency(){
        CurrencyLayerResponse currencyLayerResponse = currentCurrency();
        ZSetOperations<String, String> stringStringZSetOperations = redisTemplate.opsForZSet();
        if(currencyLayerResponse.getSuccess().equals("false")){
            throw new CurrencyConvertException(ResponseCode.INTERNAL_ERROR,"API 호출 오류가 발생했습니다.",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CurrencyEntity currencyEntity = convertToCurrency(currencyLayerResponse);
        log.info("[{}] currency entity : {}", ZonedDateTime.now(ZoneId.of("Asia/Seoul")),currencyEntity);
        String currencyEntityJson = gson.toJson(currencyEntity);

        double score = Double.parseDouble(currencyEntity.getTimestamp());
        stringStringZSetOperations.add(REDIS_CURRENCY_KEY, currencyEntityJson, score);
    }

    /**
     * Redis에 저장되어 있는 CurrencyLayerResponse 중
     * 가장 스코어가 높은(가장 최신의) 데이터 로드
     * @return
     */
    public CurrencyLayerResponse getCurrentCurrency(){
        ZSetOperations<String, String> stringStringZSetOperations = redisTemplate.opsForZSet();

        Set<String> reverseRange = stringStringZSetOperations.reverseRange(REDIS_CURRENCY_KEY,0,0);
        String str = (String) reverseRange.toArray()[0];
        CurrencyLayerResponse currencyLayerResponse = gson.fromJson(str, CurrencyLayerResponse.class);
        return currencyLayerResponse;
    }

    /**
     * amount와 type을 매개변수로 주어졌을 때,
     * 환율에 맞게 변환
     * @param currencyConvertRequest
     * @return
     */
    public ConvertedResultView currencyConvert(CurrencyConvertRequest currencyConvertRequest) {
        CurrencyLayerResponse currencyLayerResponse = getCurrentCurrency();
        if(currencyLayerResponse == null || currencyLayerResponse.getSuccess().equals("false")){
            throw new CurrencyConvertException(ResponseCode.INTERNAL_ERROR,"API 호출 오류가 발생했습니다.",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Double rate = Double.parseDouble(currencyLayerResponse.getQuotes().get(CurrencyType.USD.name().concat(currencyConvertRequest.getType().name())));
        Double convertedAmount = rate * currencyConvertRequest.getAmount();
        DecimalFormat decimalFormat = new DecimalFormat("###,###.##");
        return ConvertedResultView.builder()
                .convertedAmount(convertedAmount)
                .formattedConvertedAmount(decimalFormat.format(convertedAmount))
                .build();
    }

    /**
     * currency를 가변인자로 받아 하나의 문자열로 파싱
     * @param currencies
     * @return
     */
    private String parseCurrencies(String... currencies) {
        StringBuilder paredCurrencies = new StringBuilder();
        for(String str : currencies){
            paredCurrencies.append(",").append(str);
        }
        return paredCurrencies.toString();
    }
}
