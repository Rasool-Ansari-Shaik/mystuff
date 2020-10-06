package com.es.producer.config;

import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConfiguration.class);
	
	public static Properties getConfig(String topicName) {
		
		LOGGER.debug("in getConfig method - started");
		
		LOGGER.info("setting up the properties");
		
		//creating properties
    	List<Object> bootStrapServers = null; 
    	String securityProtocolConfig = null; 
    	String sslProtocolConfig = null; 
    	String saslMechanism = null; 
    	String username = null; 
    	String password = null; 
    	String saslJaasConfig = null; 
    	String sslEnabledProtocols = null;
    	String sslEndpointIdentificationAlgorithm = null;
    	
    	Properties properties = new Properties();
    	try {
			Configuration config = new PropertiesConfiguration("application.properties");
			bootStrapServers = config.getList(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
			securityProtocolConfig = config.getString(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG);
			sslProtocolConfig = config.getString(SslConfigs.SSL_PROTOCOL_CONFIG);
			saslMechanism = config.getString(SaslConfigs.SASL_MECHANISM);
			username = config.getString("username");
			password = config.getString("password");
//			saslJaasConfig = config.getString(SaslConfigs.SASL_JAAS_CONFIG);		
//			saslJaasConfig = saslJaasConfig.replace("::username::", username).replace("::password::", password);
			saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""+username+"\" password=\"" + password + "\";";
			sslEnabledProtocols = config.getString(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG);
			sslEndpointIdentificationAlgorithm = config.getString(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			LOGGER.error("ConfigurationException",e);
		}
    	    	
    	properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
    	properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocolConfig);
    	properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, sslProtocolConfig);
    	properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
    	properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
    	properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, sslEnabledProtocols);
    	properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, sslEndpointIdentificationAlgorithm);
    	properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	
    	LOGGER.info("Properties: "+properties);
    	
		LOGGER.debug("in getConfig method - end");
		
		return properties;
	}
}
