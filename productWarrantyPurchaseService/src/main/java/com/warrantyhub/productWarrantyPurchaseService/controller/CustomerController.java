package com.warrantyhub.productWarrantyPurchaseService.controller;

import com.warrantyhub.productWarrantyPurchaseService.dto.ProductResponse;
import com.warrantyhub.productWarrantyPurchaseService.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final PurchaseService purchaseService;

    public CustomerController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/me/products")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {
        return ResponseEntity.ok(purchaseService.getCustomerProducts(authentication.getName()));
    }
}