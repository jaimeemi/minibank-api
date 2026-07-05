package com.minibank.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minibank.models.enums.StatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDto {
    private Long id;
    private String origin;
    private String destination;
    private BigDecimal amount;
    private StatusEnum status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestedDate;
}