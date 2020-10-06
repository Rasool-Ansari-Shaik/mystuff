package com.es.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.producer.config.ProducerConfiguration;

public class ProducerApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerApp.class);

	public static void main(String[] args) {
		
		LOGGER.debug("in main method - started");

		if (args.length == 0) {
			LOGGER.error("Enter topic name");
			//return;
		}
		// Kafka consumer configuration settings
		String topicName = "topic-1a"; //args[0].toString();

		Properties properties = ProducerConfiguration.getConfig(topicName);

		// create Producer
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);

		String messageToDeliver = "Hello World, This is a sample message from Producer 2";
		
		// creating producer record
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, messageToDeliver);

		kafkaProducer.send(record, (metaData, exception) -> {

			if (exception == null) {
				LOGGER.info("Successfully received the details as \n" + "Topic: " + metaData.topic() + "\n"
						+ "Partition: " + metaData.partition() + "\n" + "Offset: " + metaData.offset() + "\n"
						+ "Timestamp: " + metaData.timestamp());
			} else {
				LOGGER.error("Can't Produce, getting error", exception);
			}
		});

		kafkaProducer.flush();
		kafkaProducer.close();

		LOGGER.info("in main method - completed");
	}
}
