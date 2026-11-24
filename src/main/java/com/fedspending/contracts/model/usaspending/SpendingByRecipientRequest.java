package com.fedspending.contracts.model.usaspending;

/**
 * Request body for {@code POST /api/v2/search/spending_by_category/recipient/}. Results are always
 * ranked by obligated amount, descending.
 */
public record SpendingByRecipientRequest(SearchFilters filters, int limit, int page) {
}
