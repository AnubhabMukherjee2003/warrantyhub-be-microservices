package com.warrantyhub.customerCompanyService.repository;

import com.warrantyhub.customerCompanyService.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {
}