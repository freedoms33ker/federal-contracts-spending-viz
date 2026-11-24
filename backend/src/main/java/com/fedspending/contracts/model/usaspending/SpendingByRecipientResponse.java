package com.fedspending.contracts.model.usaspending;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response body for {@code POST /api/v2/search/spending_by_category/recipient/}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SpendingByRecipientResponse(
        List<RecipientResult> results,
        @JsonProperty("page_metadata") PageMetadata pageMetadata) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RecipientResult(
            @JsonProperty("recipient_id") String recipientId,
            String name,
            String uei,
            BigDecimal amount) {
    }
}
