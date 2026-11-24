package com.fedspending.contracts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fedspending.contracts.client.UsaSpendingClient;
import com.fedspending.contracts.model.dto.ContractSearchRequest;
import com.fedspending.contracts.model.dto.PagedResponse;
import com.fedspending.contracts.model.dto.RecipientDto;
import com.fedspending.contracts.model.usaspending.SpendingByRecipientRequest;
import com.fedspending.contracts.model.usaspending.SpendingByRecipientResponse;

@Service
public class ContractRecipientService {

    private final UsaSpendingClient usaSpendingClient;

    public ContractRecipientService(UsaSpendingClient usaSpendingClient) {
        this.usaSpendingClient = usaSpendingClient;
    }

    public PagedResponse<RecipientDto> searchRecipients(ContractSearchRequest request) {
        var upstreamRequest = new SpendingByRecipientRequest(
                ContractSearchFilters.from(request), request.limit(), request.page());
        SpendingByRecipientResponse response = usaSpendingClient.searchSpendingByRecipient(upstreamRequest);

        if (response == null || response.results() == null) {
            return new PagedResponse<>(List.of(), request.page(), false);
        }

        List<RecipientDto> recipients = response.results().stream()
                .map(ContractRecipientService::toDto)
                .toList();
        boolean hasNext = response.pageMetadata() != null && response.pageMetadata().hasNext();
        return new PagedResponse<>(recipients, request.page(), hasNext);
    }

    private static RecipientDto toDto(SpendingByRecipientResponse.RecipientResult result) {
        return new RecipientDto(result.recipientId(), result.name(), result.uei(), result.amount());
    }
}
