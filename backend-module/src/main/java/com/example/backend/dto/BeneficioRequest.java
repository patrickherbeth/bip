package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BeneficioRequest(
        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres.")
        String nome,
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres.")
        String descricao,
        @NotNull(message = "Valor é obrigatório.")
        @DecimalMin(value = "0.00", inclusive = true, message = "Valor deve ser maior ou igual a zero.")
        BigDecimal valor,
        @NotNull(message = "Ativo é obrigatório.")
        Boolean ativo
) {}
