package com.wirebarley.core.component.currency_layer.domain;

public enum CurrencyType {
    USD("USD"),KRW("KRW"),JPY("JPY"),PHP("PHP");

    private final String getCurrencyName;

    CurrencyType(String currencyName) {
        getCurrencyName = currencyName;
    }
}
