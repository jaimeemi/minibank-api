package com.minibank.controller;

import com.minibank.models.entitys.Transfer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/transfer")
// Aquí iría @Tag(name = "Transfers", description = "Endpoint para la gestión de transferencias") cuando sumes Swagger
public interface TransferController {

    @PostMapping
        // Aquí irían tus @Operation(summary = "Registrar transferencia") y @ApiResponse
    ResponseEntity<?> processTransfer(@RequestBody Transfer transfer);
}
