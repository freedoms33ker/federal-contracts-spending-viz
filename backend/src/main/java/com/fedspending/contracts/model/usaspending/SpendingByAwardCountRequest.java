package com.fedspending.contracts.model.usaspending;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /api/v2/search/spending_by_award_count/}.
 */
public record SpendingByAwardCountRequest(
        SearchFilters filters,
        @JsonProperty("spending_level") String spendingLevel) {
}
