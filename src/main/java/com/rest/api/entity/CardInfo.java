package com.rest.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CardInfo {
    private String cardNum;
    private String validPeriod;
    private String cvc;
}
