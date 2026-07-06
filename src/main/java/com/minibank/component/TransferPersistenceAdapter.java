package com.minibank.component;

import com.minibank.mapper.TransferMapper;
import com.minibank.models.dto.TransferDto;
import com.minibank.models.entities.TransferEntity;
import com.minibank.repositories.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferPersistenceAdapter {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;

    @Transactional
    public TransferDto save(TransferDto transferDto) {
        TransferEntity entity = transferMapper.toEntity(transferDto);
        TransferEntity savedEntity = transferRepository.save(entity);
        return transferMapper.toDto(savedEntity);
    }
}