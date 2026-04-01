package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransferenciaResponse(Long fromId, Long toId, BigDecimal amount, OffsetDateTime processedAt, String message) {
}
