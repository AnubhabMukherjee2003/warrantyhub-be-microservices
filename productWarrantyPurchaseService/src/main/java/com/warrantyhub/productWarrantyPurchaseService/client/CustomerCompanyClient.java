package com.warrantyhub.productWarrantyPurchaseService.client;

import com.warrantyhub.productWarrantyPurchaseService.dto.CompanyResponse;
import com.warrantyhub.productWarrantyPurchaseService.dto.CustomerRequest;
import com.warrantyhub.productWarrantyPurchaseService.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-company-service")
public interface CustomerCompanyClient {

    @GetMapping("/api/companies/by-email/{email}")
    CompanyResponse getCompanyByEmail(@PathVariable("email") String email);

    @GetMapping("/api/customers/by-email/{email}")
    CustomerResponse getCustomerByEmail(@PathVariable("email") String email);

    @PostMapping("/api/customers")
    CustomerResponse createCustomer(@RequestBody CustomerRequest request);
}
