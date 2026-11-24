package com.fedspending.contracts.model.usaspending;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response body for {@code POST /api/v2/search/spending_by_award_count/}. Counts are split by
 * award category. Contract award type codes A–D are reported under {@code contracts}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SpendingByAwardCountResponse(AwardCounts results) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AwardCounts(long contracts) {
    }
}
