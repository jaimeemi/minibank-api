package com.minibank.service.impl;

import com.minibank.component.TransferPersistenceAdapter;
import com.minibank.models.dto.TransferDto;
import com.minibank.models.enums.StatusEnum;
import com.minibank.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferPersistenceAdapter transferPersistenceAdapter;

    @Override
    public TransferDto processTransfer(TransferDto transferDto) {
        setStatus(transferDto);

        TransferDto savedDto = transferPersistenceAdapter.save(transferDto);

        log.info("Transferencia procesada con ID: {} y Estado: {}", savedDto.getId(), savedDto.getStatus());
        return savedDto;
    }

    private void setStatus(TransferDto transferDto) {
        if (transferDto.getAmount().compareTo(BigDecimal.valueOf(5000)) <= 0) {
            transferDto.setStatus(StatusEnum.APPROVED);
        } else {
            transferDto.setStatus(StatusEnum.PENDING);
        }
    }
}