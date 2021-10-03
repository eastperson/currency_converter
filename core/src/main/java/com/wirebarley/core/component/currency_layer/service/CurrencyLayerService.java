package com.wirebarley.core.component.currency_layer.service;

import com.wirebarley.core.component.currency_layer.domain.CurrencyLayerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyLayerService {

    @GET("/live")
    Call<CurrencyLayerResponse> getCurrency(
            @Query("access_key") String accessKey,
            @Query("source") String source,
            @Query("format") String format,
            @Query("currencies") String currencies
    );


}