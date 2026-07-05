package com.minibank.controller;

import com.minibank.controller.impl.TransferControllerImpl;
import com.minibank.models.dto.TransferDto;
import com.minibank.models.enums.StatusEnum;
import com.minibank.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferControllerImplTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferControllerImpl transferController;

    @Test
    void cuandoProcesaTransferencia_entoncesRetorna200ConResultado() {
        TransferDto request = new TransferDto();
        request.setOrigin("123456");
        request.setDestination("789012");
        request.setAmount(BigDecimal.valueOf(1500));

        TransferDto response = new TransferDto();
        response.setId(1L);
        response.setStatus(StatusEnum.APPROVED);

        when(transferService.processTransfer(request)).thenReturn(response);

        ResponseEntity<TransferDto> result = transferController.processTransfer(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getStatus()).isEqualTo(StatusEnum.APPROVED);
    }
}
