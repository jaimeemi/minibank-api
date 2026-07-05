package com.minibank.service;

import com.minibank.component.TransferPersistenceAdapter;
import com.minibank.models.dto.TransferDto;
import com.minibank.models.enums.StatusEnum;
import com.minibank.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private TransferPersistenceAdapter transferPersistenceAdapter;

    @InjectMocks
    private TransferServiceImpl transferService;

    private TransferDto dto;

    @BeforeEach
    void setUp() {
        dto = new TransferDto();
        dto.setOrigin("123456");
        dto.setDestination("789012");
    }

    @Test
    void dadoMontoMenorOIgual5000_cuandoProcesa_entoncesAprueba() {
        dto.setAmount(BigDecimal.valueOf(3000));
        TransferDto saved = new TransferDto();
        saved.setStatus(StatusEnum.APPROVED);
        when(transferPersistenceAdapter.save(any())).thenReturn(saved);

        TransferDto result = transferService.processTransfer(dto);

        assertThat(dto.getStatus()).isEqualTo(StatusEnum.APPROVED);
        assertThat(result.getStatus()).isEqualTo(StatusEnum.APPROVED);
    }

    @Test
    void dadoMontoExacto5000_cuandoProcesa_entoncesAprueba() {
        dto.setAmount(BigDecimal.valueOf(5000));
        TransferDto saved = new TransferDto();
        saved.setStatus(StatusEnum.APPROVED);
        when(transferPersistenceAdapter.save(any())).thenReturn(saved);

        transferService.processTransfer(dto);

        assertThat(dto.getStatus()).isEqualTo(StatusEnum.APPROVED);
    }

    @Test
    void dadoMontoMayorA5000_cuandoProcesa_entoncesQuedaPendiente() {
        dto.setAmount(BigDecimal.valueOf(5001));
        TransferDto saved = new TransferDto();
        saved.setStatus(StatusEnum.PENDING);
        when(transferPersistenceAdapter.save(any())).thenReturn(saved);

        TransferDto result = transferService.processTransfer(dto);

        assertThat(dto.getStatus()).isEqualTo(StatusEnum.PENDING);
        assertThat(result.getStatus()).isEqualTo(StatusEnum.PENDING);
    }
}
