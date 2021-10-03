package com.wirebarley.core.entity;

import com.wirebarley.core.component.currency_layer.domain.CurrencyLayerResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.HashMap;

@Getter
@RedisHash(value = "currency", timeToLive = 30)
@Builder
public class Currency {

    @Id
    private String id;
    private String success;
    private String terms;
    private String privacy;
    private String timestamp;
    private String source;
    private HashMap<String,String> quotes;

    public static Currency convertToCurrency(CurrencyLayerResponse response){
        return Currency.builder()
                .success(response.getSuccess())
                .terms(response.getTerms())
                .privacy(response.getPrivacy())
                .timestamp(response.getTimestamp())
                .source(response.getSource())
                .quotes(response.getQuotes())
                .build();
    }

}
