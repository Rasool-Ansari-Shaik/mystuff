/*******************************************************************************
 *  * Watson Health Imaging Analytics
 *  *
 *  * IBM Confidential
 *  *
 *  * OCO Source Materials
 *  *
 *  * (C) Copyright IBM Corp. 2020
 *  *
 *  * The source code for this program is not published or otherwise
 *  * divested of its trade secrets, irrespective of what has been
 *  * deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.ops;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventStreamOps {
	private final static Logger LOGGER = LoggerFactory.getLogger(EventStreamOps.class);
	public Properties eventStreamProperties(String eventStreamspwd) {
		
		Properties props = new Properties();
		LOGGER.debug("Reading EventStream Kafka properties");
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				PropertyUtil.getProperty("atc.eventstream.bootstrapservers"));
		props.put("sasl.mechanism", PropertyUtil.getProperty("atc.eventstream.saslmechanism"));
		props.put("security.protocol", PropertyUtil.getProperty("atc.eventstream.securityprotocol"));
		props.put("sasl.jaas.config",
				"org.apache.kafka.common.security.plain.PlainLoginModule required username=\"token\" password=\""
						+ eventStreamspwd + "\""+";");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				PropertyUtil.getProperty("atc.eventstream.keydeserializer"));
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				PropertyUtil.getProperty("atc.eventstream.valuedeserializer"));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, PropertyUtil.getProperty("atc.eventstream.groupid"));

		return props;

	}

}
