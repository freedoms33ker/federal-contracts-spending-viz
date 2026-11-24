package com.fedspending.contracts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fedspending.contracts.client.UsaSpendingClient;
import com.fedspending.contracts.model.dto.ContractAwardDto;
import com.fedspending.contracts.model.dto.ContractAwardSearchRequest;
import com.fedspending.contracts.model.dto.PagedResponse;
import com.fedspending.contracts.model.usaspending.SpendingByAwardRequest;
import com.fedspending.contracts.model.usaspending.SpendingByAwardResponse;

@Service
public class ContractAwardService {

    /** USAspending award type codes for contracts: BPA call, purchase order, delivery order, definitive contract. */
    static final List<String> CONTRACT_AWARD_TYPE_CODES = List.of("A", "B", "C", "D");

    static final List<String> AWARD_FIELDS = List.of(
            "Award ID", "Recipient Name", "Award Amount", "Awarding Agency", "Start Date", "End Date");

    private final UsaSpendingClient usaSpendingClient;

    public ContractAwardService(UsaSpendingClient usaSpendingClient) {
        this.usaSpendingClient = usaSpendingClient;
    }

    public PagedResponse<ContractAwardDto> searchContractAwards(ContractAwardSearchRequest request) {
        SpendingByAwardResponse response = usaSpendingClient.searchSpendingByAward(toUsaSpendingRequest(request));

        if (response == null || response.results() == null) {
            return new PagedResponse<>(List.of(), request.page(), false);
        }

        List<ContractAwardDto> awards = response.results().stream()
                .map(ContractAwardService::toDto)
                .toList();
        boolean hasNext = response.pageMetadata() != null && response.pageMetadata().hasNext();
        return new PagedResponse<>(awards, request.page(), hasNext);
    }

    private static SpendingByAwardRequest toUsaSpendingRequest(ContractAwardSearchRequest request) {
        var filters = new SpendingByAwardRequest.Filters(
                CONTRACT_AWARD_TYPE_CODES,
                List.of(new SpendingByAwardRequest.TimePeriod(request.startDate(), request.endDate())),
                request.keywords());
        return new SpendingByAwardRequest(filters, AWARD_FIELDS, request.limit(), request.page(), "Award Amount", "desc");
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
