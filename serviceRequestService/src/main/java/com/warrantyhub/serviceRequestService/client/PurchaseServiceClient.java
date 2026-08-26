package com.warrantyhub.serviceRequestService.client;

import com.warrantyhub.serviceRequestService.dto.PurchaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-warranty-purchase-service")
public interface PurchaseServiceClient {

    @GetMapping("/api/purchases/{id}")
    PurchaseResponse getPurchaseById(@PathVariable("id") Long id);
}
