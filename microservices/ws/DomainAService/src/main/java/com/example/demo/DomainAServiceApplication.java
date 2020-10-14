package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import brave.sampler.Sampler;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class DomainAServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DomainAServiceApplication.class, args);
	}
	
	@Bean
	public Sampler defaultSampler() {
		return Sampler.ALWAYS_SAMPLE;
	}
	
	@GetMapping("domainA")
	public String getMessage() {
		return message;
	}
	
	@Value("${message}")
	private String message;

}
