package com.es.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class ConsumerApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApp.class);

	public static JsonObject main(JsonObject inputArgs) {
		LOGGER.debug("in main method - started");
		
		LOGGER.info("inside Kafka consumer with data: "+inputArgs);
		
		return inputArgs;
	}
}
