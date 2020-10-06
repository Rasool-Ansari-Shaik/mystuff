package com.example.readinglist.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "amazon")
public class AmazonConfiguration {

	private String associateID;
	
	public void setAssociateID(String associateID) {
		this.associateID = associateID;		
	}
	
	public String getAssociateID() {
		return associateID;
	}
}
