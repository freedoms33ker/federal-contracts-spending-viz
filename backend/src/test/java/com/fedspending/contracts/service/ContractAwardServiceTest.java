package com.fedspending.contracts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fedspending.contracts.client.UsaSpendingClient;
import com.fedspending.contracts.model.dto.ContractSearchRequest;
import com.fedspending.contracts.model.usaspending.PageMetadata;
import com.fedspending.contracts.model.usaspending.SpendingByAwardCountRequest;
import com.fedspending.contracts.model.usaspending.SpendingByAwardCountResponse;
import com.fedspending.contracts.model.usaspending.SpendingByAwardRequest;
import com.fedspending.contracts.model.usaspending.SpendingByAwardResponse;

@ExtendWith(MockitoExtension.class)
class ContractAwardServiceTest {

    @Mock
    private UsaSpendingClient usaSpendingClient;

    @InjectMocks
    private ContractAwardService contractAwardService;

    @Test
    void mapsSearchRequestAndResults() {
        var result = new SpendingByAwardResponse.AwardResult("W91-001", "Acme Corp", new BigDecimal("42.50"),
                "Department of Defense", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 30));
        given(usaSpendingClient.searchSpendingByAward(any())).willReturn(new SpendingByAwardResponse(
                List.of(result), new PageMetadata(2, false)));
        given(usaSpendingClient.countSpendingByAward(any())).willReturn(new SpendingByAwardCountResponse(
                new SpendingByAwardCountResponse.AwardCounts(4321)));

        var request = new ContractSearchRequest(
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 9, 30), List.of("cyber"), 25, 2);
        var response = contractAwardService.searchContractAwards(request);

        assertThat(response.results()).singleElement()
                .satisfies(award -> {
                    assertThat(award.awardId()).isEqualTo("W91-001");
                    assertThat(award.amount()).isEqualByComparingTo("42.50");
                });
        assertThat(response.page()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(173);
        assertThat(response.hasNext()).isTrue();

        var captor = ArgumentCaptor.forClass(SpendingByAwardRequest.class);
        verify(usaSpendingClient).searchSpendingByAward(captor.capture());
        SpendingByAwardRequest sent = captor.getValue();
        assertThat(sent.filters().awardTypeCodes()).containsExactly("A", "B", "C", "D");
        assertThat(sent.filters().keywords()).containsExactly("cyber");
        assertThat(sent.limit()).isEqualTo(25);
        assertThat(sent.page()).isEqualTo(2);

        var countCaptor = ArgumentCaptor.forClass(SpendingByAwardCountRequest.class);
        verify(usaSpendingClient).countSpendingByAward(countCaptor.capture());
        assertThat(countCaptor.getValue().filters().keywords()).containsExactly("cyber");
        assertThat(countCaptor.getValue().filters().awardTypeCodes()).containsExactly("A", "B", "C", "D");
    }

    @Test
    void returnsEmptyPageWhenUpstreamHasNoResults() {
        given(usaSpendingClient.searchSpendingByAward(any())).willReturn(new SpendingByAwardResponse(null, null));

        var request = new ContractSearchRequest(
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 9, 30), null, null, null);
        var response = contractAwardService.searchContractAwards(request);

        assertThat(response.results()).isEmpty();
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.totalPages()).isZero();
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    void dividesResultCountByPageSize() {
        given(usaSpendingClient.searchSpendingByAward(any()))
                .willReturn(new SpendingByAwardResponse(List.of(), new PageMetadata(1, true)));
        given(usaSpendingClient.countSpendingByAward(any())).willReturn(new SpendingByAwardCountResponse(
                new SpendingByAwardCountResponse.AwardCounts(5_786_192)));

        var request = new ContractSearchRequest(
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 9, 30), null, 25, 1);

        assertThat(contractAwardService.searchContractAwards(request).totalPages()).isEqualTo(231_448);
    }
}
