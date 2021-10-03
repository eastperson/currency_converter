package com.wirebarley.api.validation;

import com.wirebarley.api.exception.CurrencyConvertException;
import com.wirebarley.api.model.CurrencyConvertRequest;
import com.wirebarley.core.constant.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CurrencyConvertRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return CurrencyConvertRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CurrencyConvertRequest currencyConvertRequest = (CurrencyConvertRequest) target;
        if(currencyConvertRequest.getAmount() == null || currencyConvertRequest.getAmount() < 0 || currencyConvertRequest.getAmount()> 10000) {
            throw new CurrencyConvertException(ResponseCode.BAD_REQUEST,"송금액이 바르지 않습니다", HttpStatus.BAD_REQUEST);
        }
        if(currencyConvertRequest.getType() == null){
            throw new CurrencyConvertException(ResponseCode.BAD_REQUEST,"통화 코드가 바르지 않습니다", HttpStatus.BAD_REQUEST);
        }
    }
}
