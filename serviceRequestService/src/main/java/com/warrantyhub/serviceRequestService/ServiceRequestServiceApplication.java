package com.warrantyhub.serviceRequestService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceRequestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRequestServiceApplication.class, args);
	}

}
