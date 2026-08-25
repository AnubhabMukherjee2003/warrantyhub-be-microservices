package com.warrantyhub.productWarrantyPurchaseService.repository;

import com.warrantyhub.productWarrantyPurchaseService.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarrantyRepository
        extends JpaRepository<Warranty, Long> {
}