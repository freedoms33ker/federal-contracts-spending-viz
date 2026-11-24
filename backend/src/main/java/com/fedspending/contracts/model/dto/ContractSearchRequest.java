package com.fedspending.contracts.model.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContractSearchRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        List<@Size(min = 3) String> keywords,
        @Min(1) @Max(100) Integer limit,
        @Min(1) Integer page) {

    public ContractSearchRequest {
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
        limit = limit == null ? 10 : limit;
        page = page == null ? 1 : page;
    }

    @AssertTrue(message = "startDate must be on or before endDate")
    boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }
}
