package com.minibank.models.entitys;

import com.minibank.models.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;

    private String destination;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}