package com.wirebarley.api.web.rest;

import com.wirebarley.api.model.CurrencyConvertRequest;
import com.wirebarley.api.service.CurrencyConvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyConvertRest {

    private final CurrencyConvertService currencyConvertService;

    @CrossOrigin(origins="*")
    @GetMapping("/current")
    public ResponseEntity<?> currentCurrency(){
        return ResponseEntity.ok(currencyConvertService.currentCurrency());
    }

    @CrossOrigin(origins="*")
    @GetMapping("/convert")
    public ResponseEntity<?> currencyConvert(CurrencyConvertRequest currencyConvertRequest){
        return ResponseEntity.ok(currencyConvertService.currencyConvert(currencyConvertRequest));
    }


}
