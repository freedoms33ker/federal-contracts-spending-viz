package com.fedspending.contracts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fedspending.contracts.client.UsaSpendingClient;
import com.fedspending.contracts.model.dto.ContractAwardDto;
import com.fedspending.contracts.model.dto.ContractSearchRequest;
import com.fedspending.contracts.model.dto.PagedResponse;
import com.fedspending.contracts.model.usaspending.SpendingByAwardCountRequest;
import com.fedspending.contracts.model.usaspending.SpendingByAwardCountResponse;
import com.fedspending.contracts.model.usaspending.SpendingByAwardRequest;
import com.fedspending.contracts.model.usaspending.SpendingByAwardResponse;

@Service
public class ContractAwardService {

    static final List<String> AWARD_FIELDS = List.of(
            "Award ID", "Recipient Name", "Award Amount", "Awarding Agency", "Start Date", "End Date");

    static final String AWARD_SPENDING_LEVEL = "awards";

    private final UsaSpendingClient usaSpendingClient;

    public ContractAwardService(UsaSpendingClient usaSpendingClient) {
        this.usaSpendingClient = usaSpendingClient;
    }

    public PagedResponse<ContractAwardDto> searchContractAwards(ContractSearchRequest request) {
        SpendingByAwardResponse response = usaSpendingClient.searchSpendingByAward(toUsaSpendingRequest(request));
        int totalPages = pageCount(countRecords(request), request.limit());

        if (response == null || response.results() == null) {
            return new PagedResponse<>(List.of(), request.page(), totalPages, request.page() < totalPages);
        }

        List<ContractAwardDto> awards = response.results().stream()
                .map(ContractAwardService::toDto)
                .toList();
        return new PagedResponse<>(awards, request.page(), totalPages, request.page() < totalPages);
    }

    private long countRecords(ContractSearchRequest request) {
        SpendingByAwardCountResponse response = usaSpendingClient.countSpendingByAward(
                new SpendingByAwardCountRequest(ContractSearchFilters.from(request), AWARD_SPENDING_LEVEL));
        return response == null || response.results() == null ? 0 : response.results().contracts();
    }

    private static int pageCount(long totalRecords, int pageSize) {
        if (totalRecords <= 0 || pageSize <= 0) {
            return 0;
        }
        long pages = (totalRecords + pageSize - 1) / pageSize;
        return pages > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) pages;
    }

    private static SpendingByAwardRequest toUsaSpendingRequest(ContractSearchRequest request) {
        return new SpendingByAwardRequest(ContractSearchFilters.from(request), AWARD_FIELDS,
                request.limit(), request.page(), "Award Amount", "desc");
    }

    private static ContractAwardDto toDto(SpendingByAwardResponse.AwardResult result) {
        return new ContractAwardDto(
                result.awardId(),
                result.recipientName(),
                result.awardAmount(),
                result.awardingAgency(),
                result.startDate(),
                result.endDate());
    }
}
