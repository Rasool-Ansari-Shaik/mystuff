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
package org.ibm.wh.engmnt.orp.rules.caregap;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class CaregapPastCommunication {

	private final static Logger LOGGER = LoggerFactory.getLogger(CaregapPastCommunication.class);

	/**
	 * This method will check for both Successful and Unsuccessful Past
	 * communications
	 * 
	 * @param communicationData
	 * @param contractConfigData
	 * @return statusReason
	 */
	public String checkForPatientPastCommunication(JsonNode communicationData, JsonNode contractConfigData) {

		String statusReason = "";
		int configuredLimit = 0;
		String statusCodeType=null;
		JsonNode communicationNode = communicationData.get("entry");
		if (communicationNode != null) {

			JsonNode commNode = communicationNode.get(0).get("resource");
			JsonNode recievedCommNode = commNode.get("received");
			// Get Status Codes
			String successfulStatusCode = PropertyUtil.getProperty("communication.successful.statuscode.value");
			String unsuccessfulStatusCode = PropertyUtil.getProperty("communication.unsuccessful.statuscode.value");
			ArrayList<String> unsuccessfulStatusCodeItems = new ArrayList<String>(
					Arrays.asList(unsuccessfulStatusCode.split(",")));

			String recievedCommDate = recievedCommNode.asText();
			ZonedDateTime dateBefore = ZonedDateTime.ofInstant(Instant.parse(recievedCommDate), ZoneOffset.UTC);
			ZonedDateTime dateAfter = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

			long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
			// Check the Communication status code is unsuccessful
			String commStatusCode = commNode.get("status").asText();
			boolean isUnsuccessfulStatusCode = unsuccessfulStatusCodeItems.contains(commStatusCode);
			String productName=PropertyUtil.getProperty("commrequest.commproduct.value");
			// Check if Past Communication is Successful or Unsuccessful

			if (isUnsuccessfulStatusCode) {
				statusCodeType = "Unsuccessful";
				LOGGER.info("Communication Status code type: "+ statusCodeType);
				configuredLimit = contractConfigData.get("offerings").get(productName)
						.get("pastCommunicationPeriodInDays").get("unsuccessful").asInt();

			} else if (commStatusCode.equalsIgnoreCase(successfulStatusCode)) {
				statusCodeType = "successful";
				LOGGER.info("Communication Status code type: "+ statusCodeType);
				configuredLimit = contractConfigData.get("offerings").get(productName)
						.get("pastCommunicationPeriodInDays").get("successful").asInt();

			} else {
				LOGGER.info("This Communication Status code : "+ commStatusCode+" not considered");
			}
			LOGGER.info("Configured limit from the contract config is " + configuredLimit);
			if (statusCodeType != null && (noOfDaysBetween < configuredLimit) ) {
			LOGGER.info("Patient has already been contacted before " + noOfDaysBetween + " days ago");
				statusReason = PropertyUtil.getProperty("commrequest.patientcommunicationhistory.value");
			}
		}
		LOGGER.info("Status Reason is " + statusReason);
		return statusReason;
	}
}
