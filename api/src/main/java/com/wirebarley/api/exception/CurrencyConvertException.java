package com.wirebarley.api.exception;

import org.springframework.http.HttpStatus;

public class CurrencyConvertException extends RuntimeException{

    private String code;
    private HttpStatus httpStatus;

    public CurrencyConvertException(String code, String msg, HttpStatus httpStatus) {
        super(msg);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode(){
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
