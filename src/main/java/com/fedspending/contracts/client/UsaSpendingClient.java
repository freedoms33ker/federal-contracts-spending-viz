package com.fedspending.contracts.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fedspending.contracts.model.usaspending.SpendingByAwardRequest;
import com.fedspending.contracts.model.usaspending.SpendingByAwardResponse;
import com.fedspending.contracts.model.usaspending.SpendingByRecipientRequest;
import com.fedspending.contracts.model.usaspending.SpendingByRecipientResponse;

/**
 * Thin HTTP wrapper around the USAspending v2 API. Keeps wire-format concerns out of the service layer.
 */
@Component
public class UsaSpendingClient {

    static final String SPENDING_BY_AWARD_PATH = "/api/v2/search/spending_by_award/";
    static final String SPENDING_BY_RECIPIENT_PATH = "/api/v2/search/spending_by_category/recipient/";

    private final RestClient restClient;

    public UsaSpendingClient(RestClient usaSpendingRestClient) {
        this.restClient = usaSpendingRestClient;
    }

    public SpendingByAwardResponse searchSpendingByAward(SpendingByAwardRequest request) {
        return post(SPENDING_BY_AWARD_PATH, request, SpendingByAwardResponse.class);
    }

    public SpendingByRecipientResponse searchSpendingByRecipient(SpendingByRecipientRequest request) {
        return post(SPENDING_BY_RECIPIENT_PATH, request, SpendingByRecipientResponse.class);
    }

    private <T> T post(String path, Object body, Class<T> responseType) {
        try {
            return restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(responseType);
        } catch (RestClientException ex) {
            throw new UsaSpendingClientException("USAspending request to " + path + " failed", ex);
        }
    }
}
