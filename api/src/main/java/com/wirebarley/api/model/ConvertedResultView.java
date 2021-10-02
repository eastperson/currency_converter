package com.wirebarley.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConvertedResultView {

    private String formattedConvertedAmount;
    private Double convertedAmount;
}
