package com.warrantyhub.productWarrantyPurchaseService.repository;

import com.warrantyhub.productWarrantyPurchaseService.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {
}