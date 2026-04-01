package com.example.backend.controller;

import com.example.backend.dto.ApiErrorResponse;
import com.example.ejb.exception.BusinessException;
import jakarta.persistence.OptimisticLockException;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream().map(this::formatFieldError).toList();
        return build(HttpStatus.BAD_REQUEST, "Falha de validação.", details);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLock(OptimisticLockException ex) {
        return build(HttpStatus.CONFLICT, "Conflito de concorrência detectado.", List.of(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.", List.of(ex.getMessage()));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, List<String> details) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, details));
    }
}
