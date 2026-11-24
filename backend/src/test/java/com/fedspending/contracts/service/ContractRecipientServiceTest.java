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
import com.fedspending.contracts.model.usaspending.SpendingByRecipientRequest;
import com.fedspending.contracts.model.usaspending.SpendingByRecipientResponse;

@ExtendWith(MockitoExtension.class)
class ContractRecipientServiceTest {

    @Mock
    private UsaSpendingClient usaSpendingClient;

    @InjectMocks
    private ContractRecipientService contractRecipientService;

    @Test
    void mapsSearchRequestAndResults() {
        var result = new SpendingByRecipientResponse.RecipientResult(
                "6cf5fb1b-C", "LOCKHEED MARTIN CORPORATION", "G4KDGE4JFFK7", new BigDecimal("34098766945.40"));
        given(usaSpendingClient.searchSpendingByRecipient(any())).willReturn(new SpendingByRecipientResponse(
                List.of(result), new PageMetadata(3, true)));

        var request = new ContractSearchRequest(
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 9, 30), List.of("aircraft"), 100, 3);
        var response = contractRecipientService.searchRecipients(request);

        assertThat(response.results()).singleElement()
                .satisfies(recipient -> {
                    assertThat(recipient.name()).isEqualTo("LOCKHEED MARTIN CORPORATION");
                    assertThat(recipient.uei()).isEqualTo("G4KDGE4JFFK7");
                    assertThat(recipient.totalAmount()).isEqualByComparingTo("34098766945.40");
                });
        assertThat(response.page()).isEqualTo(3);
        assertThat(response.hasNext()).isTrue();

        var captor = ArgumentCaptor.forClass(SpendingByRecipientRequest.class);
        verify(usaSpendingClient).searchSpendingByRecipient(captor.capture());
        SpendingByRecipientRequest sent = captor.getValue();
        assertThat(sent.filters().awardTypeCodes()).containsExactly("A", "B", "C", "D");
        assertThat(sent.filters().keywords()).containsExactly("aircraft");
        assertThat(sent.limit()).isEqualTo(100);
        assertThat(sent.page()).isEqualTo(3);
    }

    @Test
    void returnsEmptyPageWhenUpstreamHasNoResults() {
        given(usaSpendingClient.searchSpendingByRecipient(any()))
                .willReturn(new SpendingByRecipientResponse(null, null));

        var request = new ContractSearchRequest(
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 9, 30), null, null, null);
        var response = contractRecipientService.searchRecipients(request);

        assertThat(response.results()).isEmpty();
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.hasNext()).isFalse();
    }
}
