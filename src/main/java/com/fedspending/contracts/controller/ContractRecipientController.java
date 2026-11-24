package com.fedspending.contracts.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fedspending.contracts.model.dto.ContractSearchRequest;
import com.fedspending.contracts.model.dto.PagedResponse;
import com.fedspending.contracts.model.dto.RecipientDto;
import com.fedspending.contracts.service.ContractRecipientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contracts/recipients")
public class ContractRecipientController {

    private final ContractRecipientService contractRecipientService;

    public ContractRecipientController(ContractRecipientService contractRecipientService) {
        this.contractRecipientService = contractRecipientService;
    }

    @PostMapping("/search")
    public PagedResponse<RecipientDto> search(@Valid @RequestBody ContractSearchRequest request) {
        return contractRecipientService.searchRecipients(request);
    }
}
