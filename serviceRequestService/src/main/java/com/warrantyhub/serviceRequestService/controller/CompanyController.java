package com.warrantyhub.serviceRequestService.controller;

import com.warrantyhub.serviceRequestService.dto.CompanyRequest;
import com.warrantyhub.serviceRequestService.dto.CompanyResponse;
import com.warrantyhub.serviceRequestService.dto.ServiceRequestResponse;
import com.warrantyhub.serviceRequestService.service.CompanyService;
import com.warrantyhub.serviceRequestService.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompanyController {

    private final CompanyService companyService;
    private final ServiceRequestService serviceRequestService;

    public CompanyController(CompanyService companyService, ServiceRequestService serviceRequestService) {
        this.companyService = companyService;
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping("/api/companies")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.createCompany(request));
    }

    @GetMapping("/api/company/service-requests")
    public ResponseEntity<List<ServiceRequestResponse>> getCompanyServiceRequests(Authentication authentication) {
        return ResponseEntity.ok(serviceRequestService.getCompanyServiceRequests(authentication.getName()));
    }
}