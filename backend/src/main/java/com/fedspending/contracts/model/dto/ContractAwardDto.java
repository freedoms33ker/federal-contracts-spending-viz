package com.fedspending.contracts.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractAwardDto(
        String awardId,
        String recipientName,
        BigDecimal amount,
        String awardingAgency,
        LocalDate startDate,
        LocalDate endDate) {
}
