package com.wirebarley.api.web.rest;

import com.wirebarley.api.model.CurrencyConvertRequest;
import com.wirebarley.api.service.CurrencyConvertService;
import com.wirebarley.api.validation.CurrencyConvertRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyConvertRest {

    private final CurrencyConvertService currencyConvertService;
    private final CurrencyConvertRequestValidator currencyConvertRequestValidator;

    @InitBinder("currencyConvertRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(currencyConvertRequestValidator);
    }

    @CrossOrigin(origins="*")
    @GetMapping("/current")
    public ResponseEntity<?> currentCurrency(){
        return ResponseEntity.ok(currencyConvertService.currentCurrency());
    }

    @CrossOrigin(origins="*")
    @GetMapping("/convert")
    public ResponseEntity<?> currencyConvert(@Valid CurrencyConvertRequest currencyConvertRequest){
        return ResponseEntity.ok(currencyConvertService.currencyConvert(currencyConvertRequest));
    }
}
