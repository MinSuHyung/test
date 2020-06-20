package com.rest.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class GetResultDTO {
    private String uid;
    private String maskedCardNum;
    private String validPeriod;
    private String cvc;
    private Integer paymentAmount;
    private Integer vatAmount;
    private LocalDateTime createdAt;

    private Integer cancellationSumAmount;
    private Integer cancellationVatSumAmount;

    private Integer balPayAmount;
    private Integer balVatAmount;

    private List<CancellationResultDTO> cancellationResultDTOS;
}

