package com.warrantyhub.productWarrantyPurchaseService.service;

import com.warrantyhub.productWarrantyPurchaseService.dto.PurchaseRequest;
import com.warrantyhub.productWarrantyPurchaseService.dto.PurchaseResponse;
import com.warrantyhub.productWarrantyPurchaseService.exception.CompanyNotFoundException;
import com.warrantyhub.productWarrantyPurchaseService.model.Company;
import com.warrantyhub.productWarrantyPurchaseService.model.Customer;
import com.warrantyhub.productWarrantyPurchaseService.model.Product;
import com.warrantyhub.productWarrantyPurchaseService.model.Purchase;
import com.warrantyhub.productWarrantyPurchaseService.model.Warranty;
import com.warrantyhub.productWarrantyPurchaseService.repository.CompanyRepository;
import com.warrantyhub.productWarrantyPurchaseService.repository.CustomerRepository;
import com.warrantyhub.productWarrantyPurchaseService.repository.ProductRepository;
import com.warrantyhub.productWarrantyPurchaseService.repository.PurchaseRepository;
import com.warrantyhub.productWarrantyPurchaseService.repository.WarrantyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService {

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final WarrantyRepository warrantyRepository;
    private final PurchaseRepository purchaseRepository;
    private final PasswordEncoder passwordEncoder;

    public PurchaseService(CompanyRepository companyRepository,
                           CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           WarrantyRepository warrantyRepository,
                           PurchaseRepository purchaseRepository,
                           PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.warrantyRepository = warrantyRepository;
        this.purchaseRepository = purchaseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, String companyEmail) {
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Customer savedCustomer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setName(request.getCustomerName());
                    customer.setEmail(request.getCustomerEmail());
                    customer.setPhone(request.getCustomerPhone());
                    customer.setPassword(passwordEncoder.encode(request.getCustomerPassword()));
                    return customerRepository.save(customer);
                });

        Product product = new Product();
        product.setCompany(company);
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
        purchase.setCustomer(savedCustomer);
        purchase.setProduct(savedProduct);
        purchase.setWarranty(savedWarranty);
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setInvoiceNumber(request.getInvoiceNumber());

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return new PurchaseResponse(
                savedPurchase.getPurchaseId(),
                savedCustomer.getCustomerId(),
                savedProduct.getProductId(),
                savedWarranty.getWarrantyId(),
                company.getCompanyId(),
                savedPurchase.getPurchaseDate(),
                savedPurchase.getInvoiceNumber()
        );
    }
}
