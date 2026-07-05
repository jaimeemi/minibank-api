package com.minibank.service;

import com.minibank.models.dto.TransferDto;

public interface TransferService {

    TransferDto processTransfer(TransferDto transferDto);
}
