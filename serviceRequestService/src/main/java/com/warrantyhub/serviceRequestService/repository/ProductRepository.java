package com.warrantyhub.serviceRequestService.repository;

import com.warrantyhub.serviceRequestService.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {
}