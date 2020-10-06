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

import java.text.ParseException;

import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class PatientAttributedPractioner {
	private final static Logger LOGGER = LoggerFactory.getLogger(PatientAttributedPractioner.class);

	/*
	 * Method to check for attributed Practitioner in the patient record.
	 * 
	 * @CommRequest is a Communication Request
	 */
	public String checkForPatientPractitioner(JsonNode contractConfigInfo, JsonNode patientInfo, JsonNode commRequest)
			throws JsonProcessingException, ParseException {
		String statusReason = "";

		String commProductCode = null;

		// For loop for reading communication-product from communicationRequest
		for (int i = 0; i < commRequest.get("extension").size(); i++) {
			JsonNode commProduct = commRequest.get("extension").get(i);
			if (commProduct.get("url").asText().endsWith("communication-product")) {
				commProductCode = commProduct.get("valueCoding").get("code").asText();
				break;
			}
		}

		LOGGER.debug("Verifying  status for attributedProvider ");
		boolean attributedProviderStatus = contractConfigInfo.get("offerings").get(commProductCode).get("patient")
				.get("attributedProvider").asBoolean();
		LOGGER.info("Attributed Provider Status Check for Patient in Contract Config:: " + attributedProviderStatus);

		if (attributedProviderStatus) {
			if (patientInfo.get("generalPractitioner") != null) {

				String patientPractitioner = patientInfo.get("generalPractitioner").get(0).get("reference").asText();
				if (!patientPractitioner.isEmpty() && patientPractitioner != null) {
					LOGGER.info("Patient has attributed practitioner");
				} else {

					statusReason = PropertyUtil.getProperty("commrequest.patientAttributedPractioner.value");

				}

			} else {
				statusReason = PropertyUtil.getProperty("commrequest.patientAttributedPractioner.value");

			}
		}

		return statusReason;
	}

}
