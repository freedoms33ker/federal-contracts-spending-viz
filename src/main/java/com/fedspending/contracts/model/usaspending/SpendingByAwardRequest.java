package com.fedspending.contracts.model.usaspending;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@code POST /api/v2/search/spending_by_award/}.
 */
public record SpendingByAwardRequest(
        Filters filters,
        List<String> fields,
        int limit,
        int page,
        String sort,
        String order) {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Filters(
            @JsonProperty("award_type_codes") List<String> awardTypeCodes,
            @JsonProperty("time_period") List<TimePeriod> timePeriod,
            List<String> keywords) {
    }

    public record TimePeriod(
            @JsonProperty("start_date") LocalDate startDate,
            @JsonProperty("end_date") LocalDate endDate) {
    }
}
