package com.fedspending.contracts.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fedspending.contracts.model.usaspending.SpendingByAwardRequest;
import com.fedspending.contracts.model.usaspending.SpendingByAwardResponse;

/**
 * Thin HTTP wrapper around the USAspending v2 API. Keeps wire-format concerns out of the service layer.
 */
@Component
public class UsaSpendingClient {

    static final String SPENDING_BY_AWARD_PATH = "/api/v2/search/spending_by_award/";

    private final RestClient restClient;

    public UsaSpendingClient(RestClient usaSpendingRestClient) {
        this.restClient = usaSpendingRestClient;
    }

    public SpendingByAwardResponse searchSpendingByAward(SpendingByAwardRequest request) {
        try {
            return restClient.post()
                    .uri(SPENDING_BY_AWARD_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(SpendingByAwardResponse.class);
        } catch (RestClientException ex) {
            throw new UsaSpendingClientException("USAspending spending_by_award request failed", ex);
        }
    }
}
