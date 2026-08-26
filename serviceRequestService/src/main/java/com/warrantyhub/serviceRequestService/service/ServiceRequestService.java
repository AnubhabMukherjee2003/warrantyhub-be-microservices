package com.warrantyhub.serviceRequestService.service;

import com.warrantyhub.serviceRequestService.client.PurchaseServiceClient;
import com.warrantyhub.serviceRequestService.dto.*;
import com.warrantyhub.serviceRequestService.exception.PurchaseNotFoundException;
import com.warrantyhub.serviceRequestService.exception.ServiceRequestNotFoundException;
import com.warrantyhub.serviceRequestService.model.RequestStatusHistory;
import com.warrantyhub.serviceRequestService.model.ServiceRequest;
import com.warrantyhub.serviceRequestService.repository.RequestStatusHistoryRepository;
import com.warrantyhub.serviceRequestService.repository.ServiceRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final RequestStatusHistoryRepository historyRepository;
    private final PurchaseServiceClient purchaseServiceClient;

    public ServiceRequestService(
            ServiceRequestRepository serviceRequestRepository,
            RequestStatusHistoryRepository historyRepository,
            PurchaseServiceClient purchaseServiceClient) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.historyRepository = historyRepository;
        this.purchaseServiceClient = purchaseServiceClient;
    }

    @Transactional
    public ServiceRequestResponse createRequest(ServiceRequestCreateRequest request) {
        PurchaseResponse purchase;
        try {
            purchase = purchaseServiceClient.getPurchaseById(request.getPurchaseId());
        } catch (Exception e) {
            throw new PurchaseNotFoundException(request.getPurchaseId());
        }

        String authenticatedEmail = getAuthenticatedUserEmail();

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setPurchaseId(purchase.getPurchaseId());
        serviceRequest.setIssueCategory(request.getIssueCategory());
        serviceRequest.setIssueDescription(request.getIssueDescription());
        serviceRequest.setPhotoUrl(request.getPhotoUrl());
        serviceRequest.setVideoUrl(request.getVideoUrl());

        LocalDateTime now = LocalDateTime.now();
        serviceRequest.setCreatedAt(now);
        serviceRequest.setUpdatedAt(now);

        ServiceRequest savedRequest = serviceRequestRepository.save(serviceRequest);

        RequestStatusHistory history = new RequestStatusHistory();
        history.setRequest(savedRequest);
        history.setStatus("OPEN");
        history.setRemarks("Service request created");
        history.setChangedBy(authenticatedEmail);
        history.setChangedAt(now);

        historyRepository.save(history);

        return toResponse(savedRequest);
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse getServiceRequest(Long id) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ServiceRequestNotFoundException(id));
        return toResponse(request);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getCompanyServiceRequests(String email) {
        return serviceRequestRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getCustomerServiceRequests(String email) {
        return serviceRequestRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ServiceRequestResponse changeStatus(Long id, StatusChangeRequest request) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ServiceRequestNotFoundException(id));

        String authenticatedEmail = getAuthenticatedUserEmail();
        String currentStatus = getCurrentStatus(serviceRequest);
        validateStatusTransition(currentStatus, request.getStatus());

        serviceRequest.setUpdatedAt(LocalDateTime.now());
        ServiceRequest savedRequest = serviceRequestRepository.save(serviceRequest);

        RequestStatusHistory history = new RequestStatusHistory();
        history.setRequest(savedRequest);
        history.setStatus(request.getStatus());
        history.setRemarks(request.getRemarks());
        history.setChangedBy(authenticatedEmail);
        history.setChangedAt(savedRequest.getUpdatedAt());
        historyRepository.save(history);

        return toResponse(savedRequest);
    }

    @Transactional(readOnly = true)
    public ServiceRequestHistoryResponse getStatusHistory(Long id) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ServiceRequestNotFoundException(id));

        PurchaseResponse purchaseResponse;
        try {
            purchaseResponse = purchaseServiceClient.getPurchaseById(request.getPurchaseId());
        } catch (Exception e) {
            purchaseResponse = new PurchaseResponse(request.getPurchaseId(), null, null, null, null, null, null);
        }

        ProductResponse productResponse = new ProductResponse(
                request.getPurchaseId(),
                purchaseResponse.getProductId(),
                "Product",
                "Electronics",
                "M-100",
                purchaseResponse.getCompanyId(),
                "Company"
        );

        WarrantyResponse warrantyResponse = new WarrantyResponse(
                purchaseResponse.getWarrantyId(),
                12,
                "MONTHS",
                "Standard warranty"
        );

        List<StatusHistoryResponse> historyList = historyRepository.findByRequest_RequestIdOrderByChangedAtAsc(id)
                .stream()
                .map(history -> new StatusHistoryResponse(
                        history.getHistoryId(),
                        history.getStatus(),
                        history.getRemarks(),
                        history.getChangedBy(),
                        history.getChangedAt()))
                .toList();

        return new ServiceRequestHistoryResponse(
                productResponse,
                warrantyResponse,
                toResponse(request),
                purchaseResponse,
                historyList);
    }

    private ServiceRequestResponse toResponse(ServiceRequest serviceRequest) {
        String currentStatus = getCurrentStatus(serviceRequest);

        return new ServiceRequestResponse(
                serviceRequest.getRequestId(),
                serviceRequest.getPurchaseId(),
                serviceRequest.getIssueCategory(),
                serviceRequest.getIssueDescription(),
                serviceRequest.getPhotoUrl(),
                serviceRequest.getVideoUrl(),
                currentStatus,
                serviceRequest.getCreatedAt(),
                serviceRequest.getUpdatedAt());
    }

    private String getCurrentStatus(ServiceRequest serviceRequest) {
        return historyRepository.findFirstByRequest_RequestIdOrderByChangedAtDesc(serviceRequest.getRequestId())
                .map(RequestStatusHistory::getStatus)
                .orElse("OPEN");
    }

    private void validateStatusTransition(String currentStatus, String targetStatus) {
        if (targetStatus == null || targetStatus.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        String current = normalizeStatus(currentStatus);
        String target = normalizeStatus(targetStatus);

        if ("REJECTED".equals(current) || "RESOLVED".equals(current)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Resolved or rejected requests cannot be changed");
        }

        Map<String, List<String>> allowedTransitions = Map.of(
                "OPEN", List.of("UNDER_REVIEW", "IN_PROGRESS"),
                "UNDER_REVIEW", List.of("APPROVED", "REJECTED", "IN_PROGRESS"),
                "APPROVED", List.of("PARTS_ORDERED", "IN_PROGRESS"),
                "PARTS_ORDERED", List.of("TECHNICIAN_ON_THE_WAY", "IN_PROGRESS"),
                "TECHNICIAN_ON_THE_WAY", List.of("RESOLVED", "IN_PROGRESS"),
                "IN_PROGRESS", List.of("RESOLVED", "REJECTED")
        );

        if (!allowedTransitions.getOrDefault(current, List.of()).contains(target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status transition from " + current + " to " + target);
        }
    }

    private String normalizeStatus(String status) {
        if ("CLOSED".equals(status)) {
            return "RESOLVED";
        }
        return status;
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
