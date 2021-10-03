package com.wirebarley.core.component.currency_layer.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wirebarley.core.component.currency_layer.service.CurrencyLayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
@RequiredArgsConstructor
public class CurrencyLayerClient {

    public static String CURRENCY_LAYER_URL = "http://api.currencylayer.com";
    private static Retrofit getInstance(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(CURRENCY_LAYER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static CurrencyLayerService getApiService(){
        return getInstance().create(CurrencyLayerService.class);
    }

}