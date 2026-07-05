package com.minibank.mapper;

import com.minibank.models.dto.TransferDto;
import com.minibank.models.entitys.TransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    TransferDto toDto(TransferEntity transferEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TransferEntity toEntity(TransferDto transferDto);
}
