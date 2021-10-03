package com.wirebarley.core.component.currency_layer.domain;

import lombok.Data;

import java.util.HashMap;

@Data
public class CurrencyLayerResponse {

    private String success;
    private String terms;
    private String privacy;
    private String timestamp;
    private String source;
    private HashMap<String,String> quotes;

}
