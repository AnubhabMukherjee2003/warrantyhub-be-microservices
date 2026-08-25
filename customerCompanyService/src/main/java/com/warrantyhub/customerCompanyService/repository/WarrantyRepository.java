package com.warrantyhub.customerCompanyService.repository;

import com.warrantyhub.customerCompanyService.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarrantyRepository
        extends JpaRepository<Warranty, Long> {
}