package com.minibank.controller.impl;

import com.minibank.controller.TransferController;
import com.minibank.models.dto.TransferDto;
import com.minibank.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransferControllerImpl implements TransferController {

    private final TransferService transferService;

    @Override
    public ResponseEntity<TransferDto> processTransfer(TransferDto transferDto) {
        log.info("Iniciando POST Transfer. Origen: {}, Monto: {}", transferDto.getOrigin(), transferDto.getAmount());
        TransferDto result = transferService.processTransfer(transferDto);
        return ResponseEntity.ok(result);
    }
}