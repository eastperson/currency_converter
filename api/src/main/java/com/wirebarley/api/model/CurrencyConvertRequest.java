package com.wirebarley.api.model;

import com.wirebarley.core.component.currency_layer.domain.CurrencyType;
import lombok.Data;

@Data
public class CurrencyConvertRequest {

    private Double amount;
    private CurrencyType type;

}
