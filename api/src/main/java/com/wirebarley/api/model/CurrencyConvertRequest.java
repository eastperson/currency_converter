package com.wirebarley.api.model;

import com.wirebarley.core.component.currency_layer.domain.CurrencyType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CurrencyConvertRequest {

    @NotNull
    private Double amount;

    @NotNull
    private CurrencyType type;

}
