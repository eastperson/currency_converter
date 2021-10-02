package com.wirebarley.api.service;

import com.wirebarley.core.component.currency_layer.client.CurrencyLayerClient;
import com.wirebarley.core.component.currency_layer.domain.CurrencyLayerResponse;
import com.wirebarley.core.component.currency_layer.domain.CurrencyType;
import com.wirebarley.api.exception.CurrencyLayerClientException;
import com.wirebarley.api.model.ConvertedResultView;
import com.wirebarley.api.model.CurrencyConvertRequest;
import com.wirebarley.core.constant.ResponseCode;
import com.wirebarley.core.properties.CurrencyLayerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class CurrencyConvertService {

    private final CurrencyLayerProperties currencyLayerProperties;

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
            throw new CurrencyLayerClientException(ResponseCode.INTERNAL_ERROR,"Currency Layer를 호출하는데 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public CompletableFuture<CurrencyLayerResponse> currentCurrencyAsync(){
        Call<CurrencyLayerResponse> currencyLayerResponseCall = CurrencyLayerClient.getApiService().getCurrency(
                currencyLayerProperties.getAccessKey(),
                CurrencyType.USD.name(),
                "1",
                parseCurrencies(CurrencyType.KRW.name(),CurrencyType.JPY.name(),CurrencyType.PHP.name())
        );
        try {
            return CompletableFuture.completedFuture(currencyLayerResponseCall.execute().body());
        } catch (IOException e) {
            throw new CurrencyLayerClientException(ResponseCode.INTERNAL_ERROR,"Currency Layer를 호출하는데 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ConvertedResultView currencyConvert(CurrencyConvertRequest currencyConvertRequest) {
        CompletableFuture<CurrencyLayerResponse> future = currentCurrencyAsync();
        while(!future.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            CurrencyLayerResponse currencyLayerResponse = future.get();

            Double rate = Double.parseDouble(currencyLayerResponse.getQuotes().get(CurrencyType.USD.name().concat(currencyConvertRequest.getType().name())));
            Double convertedAmount = rate * currencyConvertRequest.getAmount();
            DecimalFormat decimalFormat = new DecimalFormat("###,###.##");
            return ConvertedResultView.builder()
                    .convertedAmount(convertedAmount)
                    .formattedConvertedAmount(decimalFormat.format(convertedAmount))
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new CurrencyLayerClientException(ResponseCode.INTERNAL_ERROR,"",HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
