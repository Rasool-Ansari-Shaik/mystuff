package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import brave.sampler.Sampler;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableHystrix
@EnableHystrixDashboard
@RibbonClient(name="domain-c-service")
public class Microservice5Application {

	public static void main(String[] args) {
		SpringApplication.run(Microservice5Application.class, args);
	}
	
	@Bean
	public Sampler defaultSampler() {
		return Sampler.ALWAYS_SAMPLE;
	}
	
	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("ms5")
	@HystrixCommand(fallbackMethod = "getDefaultMessage")
	public String getDomainCMessage() {
		String url = "http://domain-c-service/domainC";
		String domainCMessage = restTemplate.getForObject(url, String.class);
		return message + "<br>" + domainCMessage;
	}
	
	public String getDefaultMessage() {
		return message + "<br>" + "Default message";
	}
	
	@Value("${message}")
	private String message;
	
}
