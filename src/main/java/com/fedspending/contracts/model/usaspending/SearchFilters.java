package com.fedspending.contracts.model.usaspending;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The {@code filters} object shared by USAspending's {@code /api/v2/search/*} endpoints.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SearchFilters(
        @JsonProperty("award_type_codes") List<String> awardTypeCodes,
        @JsonProperty("time_period") List<TimePeriod> timePeriod,
        List<String> keywords) {

    public record TimePeriod(
            @JsonProperty("start_date") LocalDate startDate,
            @JsonProperty("end_date") LocalDate endDate) {
    }
}
