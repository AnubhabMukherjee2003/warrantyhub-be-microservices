package com.warrantyhub.customerCompanyService.controller;

import com.warrantyhub.customerCompanyService.dto.CustomerRequest;
import com.warrantyhub.customerCompanyService.dto.CustomerResponse;
import com.warrantyhub.customerCompanyService.dto.ProductResponse;
import com.warrantyhub.customerCompanyService.dto.ServiceRequestResponse;
import com.warrantyhub.customerCompanyService.service.CustomerService;
import com.warrantyhub.customerCompanyService.service.ServiceRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceRequestService serviceRequestService;

    public CustomerController(CustomerService customerService, ServiceRequestService serviceRequestService) {
        this.customerService = customerService;
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/me/products")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerProducts(authentication.getName()));
    }

    @GetMapping("/service-requests")
    public ResponseEntity<List<ServiceRequestResponse>> getMyServiceRequests(Authentication authentication) {
        return ResponseEntity.ok(serviceRequestService.getCustomerServiceRequests(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id,
                                                           @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}