package com.warrantyhub.productWarrantyPurchaseService.service;

import com.warrantyhub.productWarrantyPurchaseService.client.CustomerCompanyClient;
import com.warrantyhub.productWarrantyPurchaseService.dto.CompanyResponse;
import com.warrantyhub.productWarrantyPurchaseService.dto.CustomerRequest;
import com.warrantyhub.productWarrantyPurchaseService.dto.CustomerResponse;
import com.warrantyhub.productWarrantyPurchaseService.dto.ProductResponse;
import com.warrantyhub.productWarrantyPurchaseService.dto.PurchaseRequest;
import com.warrantyhub.productWarrantyPurchaseService.dto.PurchaseResponse;
import com.warrantyhub.productWarrantyPurchaseService.model.Product;
import com.warrantyhub.productWarrantyPurchaseService.model.Purchase;
import com.warrantyhub.productWarrantyPurchaseService.model.Warranty;
import com.warrantyhub.productWarrantyPurchaseService.repository.ProductRepository;
import com.warrantyhub.productWarrantyPurchaseService.repository.PurchaseRepository;
import com.warrantyhub.productWarrantyPurchaseService.repository.WarrantyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseService {

    private final CustomerCompanyClient customerCompanyClient;
    private final ProductRepository productRepository;
    private final WarrantyRepository warrantyRepository;
    private final PurchaseRepository purchaseRepository;

    public PurchaseService(CustomerCompanyClient customerCompanyClient,
                           ProductRepository productRepository,
                           WarrantyRepository warrantyRepository,
                           PurchaseRepository purchaseRepository) {
        this.customerCompanyClient = customerCompanyClient;
        this.productRepository = productRepository;
        this.warrantyRepository = warrantyRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, String companyEmail) {
        CompanyResponse company = customerCompanyClient.getCompanyByEmail(companyEmail);
        if (company == null) {
            throw new IllegalArgumentException("Company not found for email: " + companyEmail);
        }

        CustomerResponse customer;
        try {
            customer = customerCompanyClient.getCustomerByEmail(request.getCustomerEmail());
        } catch (Exception e) {
            CustomerRequest customerReq = new CustomerRequest();
            customerReq.setName(request.getCustomerName());
            customerReq.setEmail(request.getCustomerEmail());
            customerReq.setPhone(request.getCustomerPhone());
            customerReq.setPassword(request.getCustomerPassword());
            customer = customerCompanyClient.createCustomer(customerReq);
        }

        Product product = new Product();
        product.setCompanyId(company.getCompanyId());
        product.setCompanyName(company.getCompanyName());
        product.setCompanyEmail(company.getEmail());
        product.setProductName(request.getProductName());
        product.setCategory(request.getProductCategory());
        product.setModelNumber(request.getModelNumber());
        Product savedProduct = productRepository.save(product);

        Warranty warranty = new Warranty();
        warranty.setWarrantyPeriod(request.getWarrantyPeriod());
        warranty.setWarrantyUnit(request.getWarrantyUnit());
        warranty.setTerms(request.getWarrantyTerms());
        Warranty savedWarranty = warrantyRepository.save(warranty);

        Purchase purchase = new Purchase();
        purchase.setCustomerId(customer.getCustomerId());
        purchase.setCustomerEmail(customer.getEmail());
        purchase.setCustomerName(customer.getName());
        purchase.setProduct(savedProduct);
        purchase.setWarranty(savedWarranty);
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setInvoiceNumber(request.getInvoiceNumber());

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return new PurchaseResponse(
                savedPurchase.getPurchaseId(),
                customer.getCustomerId(),
                savedProduct.getProductId(),
                savedWarranty.getWarrantyId(),
                company.getCompanyId(),
                savedPurchase.getPurchaseDate(),
                savedPurchase.getInvoiceNumber()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getCustomerProducts(String email) {
        return purchaseRepository.findByCustomerEmail(email)
                .stream()
                .map(purchase -> {
                    Product p = purchase.getProduct();
                    return new ProductResponse(
                            purchase.getPurchaseId(),
                            p.getProductId(),
                            p.getProductName(),
                            p.getCategory(),
                            p.getModelNumber(),
                            p.getCompanyId(),
                            p.getCompanyName()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchaseResponse getPurchaseResponse(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found with ID: " + purchaseId));

        return new PurchaseResponse(
                purchase.getPurchaseId(),
                purchase.getCustomerId(),
                purchase.getProduct() != null ? purchase.getProduct().getProductId() : null,
                purchase.getWarranty() != null ? purchase.getWarranty().getWarrantyId() : null,
                purchase.getProduct() != null ? purchase.getProduct().getCompanyId() : null,
                purchase.getPurchaseDate(),
                purchase.getInvoiceNumber()
        );
    }
}
