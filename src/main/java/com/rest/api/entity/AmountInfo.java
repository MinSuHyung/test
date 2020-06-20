package com.rest.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AmountInfo {
    private Integer paymentAmount;
    private Integer vatAmount;
}
