package com.rest.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CancellationResultDTO {
    private Integer cancellationAmount;
    private Integer cancellationVatAmount;
    private LocalDateTime canceledAt;
}
