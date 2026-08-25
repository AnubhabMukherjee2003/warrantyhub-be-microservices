package com.warrantyhub.customerCompanyService.repository;

import com.warrantyhub.customerCompanyService.model.RequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestStatusHistoryRepository
        extends JpaRepository<RequestStatusHistory, Long> {

        List<RequestStatusHistory> findByRequest_RequestIdOrderByChangedAtAsc(Long requestId);
        
        java.util.Optional<RequestStatusHistory> findFirstByRequest_RequestIdOrderByChangedAtDesc(Long requestId);
}