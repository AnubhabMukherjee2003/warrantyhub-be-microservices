package com.warrantyhub.serviceRequestService.repository;

import com.warrantyhub.serviceRequestService.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByPurchaseId(Long purchaseId);
    List<ServiceRequest> findByPurchaseIdIn(List<Long> purchaseIds);
}