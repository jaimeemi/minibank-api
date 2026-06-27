package com.minibank.controller.impl;

import com.minibank.controller.TransferController;
import com.minibank.models.entitys.Transfer;
import com.minibank.repositories.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransferControllerImpl implements TransferController {

    private final TransferRepository transferRepository;

    @Override
    public ResponseEntity<?> processTransfer(Transfer transfer) {
        log.info("Ejecución POST: Transfer para monto {}", transfer.getAmount());

        Transfer savedTransfer = transferRepository.save(transfer);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "id", savedTransfer.getId(),
                "message", "Transferencia persistida correctamente en base de datos"
        ));
    }
}