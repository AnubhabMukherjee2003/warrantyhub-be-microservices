package com.warrantyhub.serviceRequestService.repository;

import com.warrantyhub.serviceRequestService.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarrantyRepository
        extends JpaRepository<Warranty, Long> {
}