package com.minibank.component;

import com.minibank.mapper.TransferMapper;
import com.minibank.models.dto.TransferDto;
import com.minibank.models.entities.TransferEntity;
import com.minibank.models.enums.StatusEnum;
import com.minibank.repositories.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferPersistenceAdapterTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransferPersistenceAdapter persistenceAdapter;

    @Test
    void cuandoGuarda_entoncesRetornaDtoConIdYEstado() {
        TransferDto inputDto = new TransferDto();
        inputDto.setOrigin("123456");
        inputDto.setDestination("789012");
        inputDto.setAmount(BigDecimal.valueOf(1500));
        inputDto.setStatus(StatusEnum.APPROVED);

        TransferEntity entity = new TransferEntity();
        TransferEntity savedEntity = new TransferEntity();
        savedEntity.setId(1L);
        savedEntity.setStatus(StatusEnum.APPROVED);

        TransferDto expectedDto = new TransferDto();
        expectedDto.setId(1L);
        expectedDto.setStatus(StatusEnum.APPROVED);

        when(transferMapper.toEntity(inputDto)).thenReturn(entity);
        when(transferRepository.save(entity)).thenReturn(savedEntity);
        when(transferMapper.toDto(savedEntity)).thenReturn(expectedDto);

        TransferDto result = persistenceAdapter.save(inputDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(StatusEnum.APPROVED);
    }
}
