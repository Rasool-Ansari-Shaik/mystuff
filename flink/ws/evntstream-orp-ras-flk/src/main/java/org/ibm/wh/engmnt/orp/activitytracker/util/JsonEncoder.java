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
package org.ibm.wh.engmnt.orp.activitytracker.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ibm.wh.engmnt.orp.activitytracker.model.CadfEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonEncoder {
	private static Logger logger = LoggerFactory.getLogger(JsonEncoder.class);

	public static String encode(CadfEvent event) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		try {
			return objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			logger.error("Failed to encode message", e);
			return null;
		}
	}
}
