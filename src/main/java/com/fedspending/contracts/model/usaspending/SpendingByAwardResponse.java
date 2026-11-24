package com.fedspending.contracts.model.usaspending;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response body for {@code POST /api/v2/search/spending_by_award/}. Result keys mirror the
 * requested {@code fields}, which USAspending returns verbatim (including spaces).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SpendingByAwardResponse(
        List<AwardResult> results,
        @JsonProperty("page_metadata") PageMetadata pageMetadata) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AwardResult(
            @JsonProperty("Award ID") String awardId,
            @JsonProperty("Recipient Name") String recipientName,
            @JsonProperty("Award Amount") BigDecimal awardAmount,
            @JsonProperty("Awarding Agency") String awardingAgency,
            @JsonProperty("Start Date") LocalDate startDate,
            @JsonProperty("End Date") LocalDate endDate) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PageMetadata(int page, boolean hasNext) {
    }
}
