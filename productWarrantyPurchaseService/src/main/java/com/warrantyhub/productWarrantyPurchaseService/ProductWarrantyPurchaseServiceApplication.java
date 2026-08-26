package com.warrantyhub.productWarrantyPurchaseService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProductWarrantyPurchaseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductWarrantyPurchaseServiceApplication.class, args);
	}

}
