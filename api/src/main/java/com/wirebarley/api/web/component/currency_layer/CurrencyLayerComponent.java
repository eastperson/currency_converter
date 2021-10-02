package com.wirebarley.api.web.component.currency_layer;


import com.wirebarley.core.properties.CurrencyLayerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyLayerComponent {

    private final CurrencyLayerProperties currencyLayerProperties;
    private static String CURRENCY_URL = "http://api.currencylayer.com/";

    private void getCurrency(){

    }

}
