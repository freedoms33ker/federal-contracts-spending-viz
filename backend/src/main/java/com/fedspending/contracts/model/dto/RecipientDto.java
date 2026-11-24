package com.fedspending.contracts.model.dto;

import java.math.BigDecimal;

public record RecipientDto(
        String recipientId,
        String name,
        String uei,
        BigDecimal totalAmount) {
}
