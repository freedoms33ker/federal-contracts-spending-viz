package com.fedspending.contracts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fedspending.contracts.model.dto.ContractAwardDto;
import com.fedspending.contracts.model.dto.PagedResponse;
import com.fedspending.contracts.service.ContractAwardService;

@WebMvcTest(ContractAwardController.class)
class ContractAwardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractAwardService contractAwardService;

    @Test
    void searchReturnsAwards() throws Exception {
        var award = new ContractAwardDto("W91-001", "Acme Corp", new BigDecimal("1500000.00"),
                "Department of Defense", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        given(contractAwardService.searchContractAwards(any()))
                .willReturn(new PagedResponse<>(List.of(award), 1, 4, true));

        mockMvc.perform(post("/api/v1/contracts/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate": "2024-10-01", "endDate": "2025-09-30"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].awardId").value("W91-001"))
                .andExpect(jsonPath("$.results[0].recipientName").value("Acme Corp"))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.totalPages").value(4));
    }

    @Test
    void searchRejectsInvertedDateRange() throws Exception {
        mockMvc.perform(post("/api/v1/contracts/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate": "2025-10-01", "endDate": "2025-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("startDate must be on or before endDate"));
    }
}
