package com.comm.engine.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comm")
public class CommEngineController {
	
	
	@Value("${comm.engine.message}")
	private String message;
	
	@GetMapping(value = "/message")
	public Message doSendMessage() {
		
		return new Message(message);
	}

}
