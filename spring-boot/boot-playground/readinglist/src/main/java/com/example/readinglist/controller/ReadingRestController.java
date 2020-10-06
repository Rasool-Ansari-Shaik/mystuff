package com.example.readinglist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadingRestController {
	
	@GetMapping("hello")
	public String sayHello() {
		return "I say hello";
	}

}
