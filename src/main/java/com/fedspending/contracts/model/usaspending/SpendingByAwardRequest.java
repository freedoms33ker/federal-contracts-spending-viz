package com.fedspending.contracts.model.usaspending;

import java.util.List;

/**
 * Request body for {@code POST /api/v2/search/spending_by_award/}.
 */
public record SpendingByAwardRequest(
        SearchFilters filters,
        List<String> fields,
        int limit,
        int page,
        String sort,
        String order) {
}
