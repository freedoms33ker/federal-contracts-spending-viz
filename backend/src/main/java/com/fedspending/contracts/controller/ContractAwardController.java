package com.fedspending.contracts.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fedspending.contracts.model.dto.ContractAwardDto;
import com.fedspending.contracts.model.dto.ContractSearchRequest;
import com.fedspending.contracts.model.dto.PagedResponse;
import com.fedspending.contracts.service.ContractAwardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractAwardController {

    private final ContractAwardService contractAwardService;

    public ContractAwardController(ContractAwardService contractAwardService) {
        this.contractAwardService = contractAwardService;
    }

    @PostMapping("/search")
    public PagedResponse<ContractAwardDto> search(@Valid @RequestBody ContractSearchRequest request) {
        return contractAwardService.searchContractAwards(request);
    }
}
