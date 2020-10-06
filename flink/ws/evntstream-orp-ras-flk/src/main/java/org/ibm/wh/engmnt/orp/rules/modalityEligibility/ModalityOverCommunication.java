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
package org.ibm.wh.engmnt.orp.rules.modalityEligibility;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.ibm.wh.engmnt.orp.ops.FhirServerOps;
import org.ibm.wh.engmnt.orp.utility.DatabaseUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ModalityOverCommunication {

	private final static Logger LOGGER = LoggerFactory.getLogger(ModalityOverCommunication.class);

	private RestTemplate restTemplate;
	private FhirServerOps fhirServerOps;

	/*
	 * Method to check modality over communication for patient preference
	 */
	public String checkModalityOverCommunicationForPatientPreference(String patientPreference, String patientId,
			JsonNode contractConfigData) throws Exception {

		fhirServerOps = new FhirServerOps();
		restTemplate = new RestTemplate(fhirServerOps.getClientHttpRequestFactory());

		LOGGER.info("Checking Modality Over Communications Rules with patientId : " + patientId);
		String statusReason = "";
		if ("email".equalsIgnoreCase(patientPreference) || "sms".equalsIgnoreCase(patientPreference)) {
			statusReason = checkForOverCommunication(patientPreference, patientId, contractConfigData);
			LOGGER.info("Status reason for " + patientPreference + " Modality Over Communication : " + statusReason);
		}

		return statusReason;

	}

	/*
	 * Check Modality Over Communication for email
	 */
	private String checkForOverCommunication(String patientPreference, String patientId, JsonNode contractConfigData)
			throws Exception {
		
		String productName = PropertyUtil.getProperty("commrequest.commproduct.value");
		
		int noOfDays = contractConfigData.get("offerings").get(productName).get("modality").get(patientPreference)
				.get("overCommunicationLimits").get("noOfDays").asInt();

		LOGGER.info("overCommunicationLimits -> noOfDays from contract config : " + noOfDays);

		ZonedDateTime currentDate = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

		ZonedDateTime pastDate = currentDate.minusDays(noOfDays);

		LOGGER.info("pastDate : " + pastDate);
		String statusReason = "";
		int noOfCommunication = contractConfigData.get("offerings").get(productName).get("modality")
				.get(patientPreference).get("overCommunicationLimits").get("noOfCommunication").asInt();

		LOGGER.info("overCommunicationLimits -> noOfCommunication from contract config : " + noOfCommunication);
		/*
		 * Get patient's previous communication from FHIR
		 */
		JsonNode communicationData = fhirServerOps.getPreviousCommunicationRecords(patientId, patientPreference,
				pastDate.toString(), restTemplate);

		int totalNumberOfPreviousCommunications = communicationData.get("total").asInt();
		LOGGER.info("Previous " + patientPreference + " communication for a Patient : "
				+ totalNumberOfPreviousCommunications);
		if (totalNumberOfPreviousCommunications >= noOfCommunication) {
			statusReason = PropertyUtil.getProperty("commrequest.modalityOverCommunication.value")
					.replace("<patientPreference>", patientPreference);
		}
		return statusReason;

	}

}
