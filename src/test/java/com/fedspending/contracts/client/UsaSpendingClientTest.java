package com.fedspending.contracts.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.fedspending.contracts.model.usaspending.SpendingByAwardRequest;

class UsaSpendingClientTest {

    private static final String BASE_URL = "https://api.usaspending.test";

    private MockRestServiceServer server;
    private UsaSpendingClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new UsaSpendingClient(builder.build());
    }

    @Test
    void postsSearchAndParsesResponse() {
        server.expect(requestTo(BASE_URL + UsaSpendingClient.SPENDING_BY_AWARD_PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"filters": {"award_type_codes": ["A"],
                                     "time_period": [{"start_date": "2024-10-01", "end_date": "2025-09-30"}]},
                         "limit": 10, "page": 1}
                        """))
                .andRespond(withSuccess("""
                        {"results": [{"internal_id": 1, "Award ID": "W91-001", "Recipient Name": "Acme Corp",
                                      "Award Amount": 1500000.0, "Awarding Agency": "Department of Defense",
                                      "Start Date": "2025-01-01", "End Date": "2025-12-31"}],
                         "page_metadata": {"page": 1, "hasNext": true}}
                        """, MediaType.APPLICATION_JSON));

        var response = client.searchSpendingByAward(sampleRequest());

        assertThat(response.results()).singleElement()
                .satisfies(result -> {
                    assertThat(result.awardId()).isEqualTo("W91-001");
                    assertThat(result.startDate()).isEqualTo(LocalDate.of(2025, 1, 1));
                });
        assertThat(response.pageMetadata().hasNext()).isTrue();
        server.verify();
    }

    @Test
    void wrapsUpstreamErrors() {
        server.expect(requestTo(BASE_URL + UsaSpendingClient.SPENDING_BY_AWARD_PATH))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.searchSpendingByAward(sampleRequest()))
                .isInstanceOf(UsaSpendingClientException.class);
    }

    private static SpendingByAwardRequest sampleRequest() {
        var filters = new SpendingByAwardRequest.Filters(
                List.of("A"),
                List.of(new SpendingByAwardRequest.TimePeriod(LocalDate.of(2024, 10, 1), LocalDate.of(2025, 9, 30))),
                List.of());
        return new SpendingByAwardRequest(filters, List.of("Award ID"), 10, 1, "Award Amount", "desc");
    }
}
