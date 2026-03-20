package com.norman.couture.exception;

import com.norman.couture.service.dto.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<MessageResponse> handleClientNotFound(ClientNotFoundException ex) {
        log.warn("Client introuvable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleClientAlreadyExists(ClientAlreadyExistsException ex) {
        log.warn("Conflit client: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(CommandeNotFoundException.class)
    public ResponseEntity<MessageResponse> handleCommandeNotFound(CommandeNotFoundException ex) {
        log.warn("Commande introuvable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(MesureNotFoundException.class)
    public ResponseEntity<MessageResponse> handleMesureNotFound(MesureNotFoundException ex) {
        log.warn("Mesure introuvable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(MesureStandardNotFoundException.class)
    public ResponseEntity<MessageResponse> handleMesureStandardNotFound(MesureStandardNotFoundException ex) {
        log.warn("Mesure standard manquante: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(new MessageResponse(ex.getMessage()));
    }
}
