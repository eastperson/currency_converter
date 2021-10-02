package com.wirebarley.api.web.advice;

import com.wirebarley.api.exception.CurrencyLayerClientException;
import lombok.Value;
import org.modelmapper.internal.util.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CurrencyApiRestAdvice {

    @ExceptionHandler(CurrencyLayerClientException.class)
    public ResponseEntity<?> handleCurrencyLayerClientException(CurrencyLayerClientException e){
        return currencyException(e.getCode(),e.getMessage(),e.getHttpStatus());
    }

    protected static <T> ResponseEntity<?> currencyException(String code, String msg, HttpStatus httpStatus) {
        return new ResponseEntity<>(Map.of("code",code,"msg",msg), httpStatus);
    }

}
