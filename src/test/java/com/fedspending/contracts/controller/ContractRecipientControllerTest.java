package com.fedspending.contracts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fedspending.contracts.model.dto.PagedResponse;
import com.fedspending.contracts.model.dto.RecipientDto;
import com.fedspending.contracts.service.ContractRecipientService;

@WebMvcTest(ContractRecipientController.class)
class ContractRecipientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractRecipientService contractRecipientService;

    @Test
    void searchReturnsRecipients() throws Exception {
        var recipient = new RecipientDto("6cf5fb1b-C", "LOCKHEED MARTIN CORPORATION", "G4KDGE4JFFK7",
                new BigDecimal("34098766945.40"));
        given(contractRecipientService.searchRecipients(any()))
                .willReturn(new PagedResponse<>(List.of(recipient), 2, true));

        mockMvc.perform(post("/api/v1/contracts/recipients/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate": "2024-10-01", "endDate": "2025-09-30", "limit": 100, "page": 2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].name").value("LOCKHEED MARTIN CORPORATION"))
                .andExpect(jsonPath("$.results[0].uei").value("G4KDGE4JFFK7"))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

    @Test
    void searchRejectsPageSizeOver100() throws Exception {
        mockMvc.perform(post("/api/v1/contracts/recipients/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate": "2024-10-01", "endDate": "2025-09-30", "limit": 101}
                                """))
                .andExpect(status().isBadRequest());
    }
}
