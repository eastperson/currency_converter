package com.wirebarley.core.entity;

import com.wirebarley.core.component.currency_layer.domain.CurrencyLayerResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.HashMap;

@Getter
@RedisHash(value = "currency", timeToLive = 2592000)
@Builder @ToString @Setter
public class CurrencyEntity {

    @Id
    private String id;
    private String success;
    private String terms;
    private String privacy;
    private String timestamp;
    private String source;
    private HashMap<String,String> quotes;

    public static CurrencyEntity convertToCurrency(CurrencyLayerResponse response){
        return CurrencyEntity.builder()
                .success(response.getSuccess())
                .terms(response.getTerms())
                .privacy(response.getPrivacy())
                .timestamp(response.getTimestamp())
                .source(response.getSource())
                .quotes(response.getQuotes())
                .build();
    }
}
