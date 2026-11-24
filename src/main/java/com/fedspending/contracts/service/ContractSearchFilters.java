package com.fedspending.contracts.service;

import java.util.List;

import com.fedspending.contracts.model.dto.ContractSearchRequest;
import com.fedspending.contracts.model.usaspending.SearchFilters;

final class ContractSearchFilters {

    /** USAspending award type codes for contracts: BPA call, purchase order, delivery order, definitive contract. */
    static final List<String> CONTRACT_AWARD_TYPE_CODES = List.of("A", "B", "C", "D");

    private ContractSearchFilters() {
    }

    static SearchFilters from(ContractSearchRequest request) {
        return new SearchFilters(
                CONTRACT_AWARD_TYPE_CODES,
                List.of(new SearchFilters.TimePeriod(request.startDate(), request.endDate())),
                request.keywords());
    }
}
