package com.wirebarley.api.web.rest;

import com.wirebarley.api.component.currency_layer.client.CurrencyLayerClient;
import com.wirebarley.api.component.currency_layer.domain.CurrencyLayerResponse;
import com.wirebarley.api.component.currency_layer.domain.CurrencyType;
import com.wirebarley.api.model.CurrencyConvertRequest;
import com.wirebarley.api.service.CurrencyConvertService;
import com.wirebarley.core.properties.CurrencyLayerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Call;

import java.io.IOException;


@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyConvertRest {

    private final CurrencyConvertService currencyConvertService;

    @GetMapping("/current")
    public ResponseEntity<?> currentCurrency(){
        return ResponseEntity.ok(currencyConvertService.currentCurrency());
    }

    @GetMapping("/convert")
    public ResponseEntity<?> currencyConvert(CurrencyConvertRequest currencyConvertRequest){
        return ResponseEntity.ok(currencyConvertService.currencyConvert(currencyConvertRequest));
    }


}
