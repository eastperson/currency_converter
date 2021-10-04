package com.wirebarley.api.web.advice;

import com.wirebarley.api.exception.CurrencyConvertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CurrencyApiRestAdvice {

    @ExceptionHandler(CurrencyConvertException.class)
    public ResponseEntity<?> handleCurrencyLayerClientException(CurrencyConvertException e){
        log.error("[API Error] Code : {},Msg : {}",e.getCode(),e.getMessage());
        return currencyException(e.getCode(),e.getMessage(),e.getHttpStatus());
    }

    protected static <T> ResponseEntity<?> currencyException(String code, String msg, HttpStatus httpStatus) {
        return new ResponseEntity<>(Map.of("code",code,"msg",msg), httpStatus);
    }

}
