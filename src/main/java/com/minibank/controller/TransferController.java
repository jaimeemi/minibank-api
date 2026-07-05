package com.minibank.controller;

import com.minibank.models.dto.TransferDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Transferencias", description = "API de operaciones bancarias para MiniBank")
@RequestMapping("/transfer")
public interface TransferController {

    @Operation(
            summary = "Procesar una nueva transferencia",
            description = "Recibe los datos de una transferencia, valida el monto para determinar aprobación automática y la persiste.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transferencia procesada exitosamente",
                            content = @Content(schema = @Schema(implementation = TransferDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping
    ResponseEntity<TransferDto> processTransfer(@Valid @RequestBody TransferDto transferDto);
}
