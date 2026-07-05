package com.minibank.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseException(DataAccessException ex) {
        log.error("Error crítico de persistencia en BD: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.clear();
        response.put("status", "ERROR");
        response.put("message", "El servicio de almacenamiento no está disponible temporalmente.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "BAD_REQUEST");
        response.put("message", "Error de validación en los campos enviados.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
