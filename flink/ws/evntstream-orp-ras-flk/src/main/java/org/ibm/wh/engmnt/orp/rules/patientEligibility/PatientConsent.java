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
package org.ibm.wh.engmnt.orp.rules.patientEligibility;

import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class PatientConsent {
	private final static Logger LOGGER = LoggerFactory.getLogger(PatientConsent.class);

	public String checkForPatientConsent(JsonNode contractConfigInfo, JsonNode patientData, JsonNode commRequest)
			throws JsonMappingException, JsonProcessingException {

		/*
		 * Method to check for Patient Consent at Contract Config and Patient Record.
		 * 
		 * @CommRequest is a Communication Request
		 */

		String statusReason = "";

		String commProductCode = null;

		// For loop for reading communication-product from communicationRequest
		for (int i = 0; i < commRequest.get("extension").size(); i++) {
			JsonNode commProduct = commRequest.get("extension").get(i);
			if (commProduct.get("url").asText().endsWith("communication-product")) {
				commProductCode = commProduct.get("valueCoding").get("code").asText();
				// LOGGER.info("Communication product for communication request is " +
				// commProductCode);
				break;
			}
		}

		String commProduct = PropertyUtil.getProperty("commrequest.commproduct.value");
		if (commProduct.equalsIgnoreCase(commProductCode)) {
			LOGGER.debug("Verifying  Patient Consent Check in Contract Configuration ");
			boolean patientConsent = contractConfigInfo.get("offerings").get(commProductCode).get("patient")
					.get("consent").asBoolean();
			LOGGER.info("Patient Consent check as per Contract config is:: " + patientConsent);

			JsonNode patientTelecomNode = patientData.get("telecom");

			if (patientConsent) {

				if (patientTelecomNode != null) {
					LOGGER.info("Checking Implied Patient Consent");
					for (int i = 0; i < patientTelecomNode.size(); i++) {
						if (patientTelecomNode.get(i).get("system").asText().equals("sms")
								|| patientData.get("telecom").get(i).get("system").asText().equals("email")) {
							LOGGER.info("Patient has Implied Patient Consent");
							break;
						}
					}
				} else {
					LOGGER.info("Patient has no implied consent");
					statusReason = PropertyUtil.getProperty("commrequest.noconsent.value");
				}
			}else {
				LOGGER.info("Patient consent check is not enabled in Contract configuration" );
				
			}
		}else {
			LOGGER.info("Communication product ("+commProductCode+") in communication request is not same as Communication product ("+commProduct+") " );
		}
		
		return statusReason;

	}

}
