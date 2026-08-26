package com.warrantyhub.serviceRequestService.controller;

import com.warrantyhub.serviceRequestService.dto.ServiceRequestResponse;
import com.warrantyhub.serviceRequestService.service.ServiceRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final ServiceRequestService serviceRequestService;

    public CustomerController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/service-requests")
    public ResponseEntity<List<ServiceRequestResponse>> getMyServiceRequests(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : "";
        return ResponseEntity.ok(serviceRequestService.getCustomerServiceRequests(email));
    }
}
