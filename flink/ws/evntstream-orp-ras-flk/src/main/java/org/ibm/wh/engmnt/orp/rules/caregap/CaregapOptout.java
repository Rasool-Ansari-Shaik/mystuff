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

import org.ibm.wh.engmnt.orp.ops.FhirServerOps;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CaregapOptout {
	private static FhirServerOps fhirServerOps;
	private transient static RestTemplate restTemplate;
	private transient static ObjectMapper jsonParser;
	private final static Logger LOGGER = LoggerFactory.getLogger(CaregapOptout.class);

	/*
	 * Method to check for Patient CareGap Opt-out
	 * 
	 * @CommRequest is a Communication Request
	 */
	public String checkForPatientCaregapOptout(JsonNode commRequest) throws Exception {
		if (jsonParser == null) {
			jsonParser = new ObjectMapper();
			fhirServerOps = new FhirServerOps();
			restTemplate = new RestTemplate(fhirServerOps.getClientHttpRequestFactory());
		}

		String careGapValue = null;
		for (int i = 0; i < commRequest.get("extension").size(); i++) {
			JsonNode commProduct = commRequest.get("extension").get(i);
			if (commProduct.get("url").asText().endsWith("communication-care-gap")) {
				careGapValue = commProduct.get("valueString").asText();
				LOGGER.info("CommunicationRequest Caregap value: " + careGapValue);
				break;
			}
		}

		String patientId = commRequest.get("subject").get("reference").asText().split("/")[1];
		String patientUrl = PropertyUtil.getProperty("fhir.patient.endpoint") + patientId;
		String patientRecord = fhirServerOps.getFhirData(restTemplate, patientUrl);
		
		LOGGER.info("Patient record retrieved from FHIR Server with id: " + patientId);

		JsonNode val = jsonParser.readValue(patientRecord, JsonNode.class);
		JsonNode mainExtension = val.get("extension");
		String patientCareGapValue = null;
		String statusReason = "";
		for (int i = 0; i < mainExtension.size(); i++) {
			if (mainExtension.get(i).get("url").asText().endsWith("communication-opt-out")) {
				JsonNode innerData = mainExtension.get(i).get("extension");
				for (int j = 0; j < innerData.size(); j++) {
					if (innerData.get(j).get("url").asText().endsWith("communication-care-gap")) {
						patientCareGapValue = innerData.get(j).get("valueString").asText();
						LOGGER.info("Checking CommunicationRequest Caregap value and Patient Opt-out Caregap");
						if (careGapValue.equals(patientCareGapValue)) {
							statusReason = PropertyUtil.getProperty("commrequest.patientCareGapOptOutStatus.value");
							LOGGER.info("Patient CareGap OptOut StatusReason :" + statusReason);
							break;
						}
					}
				}
			}
			if (!statusReason.isEmpty())
				break;
		}
		if (statusReason.isEmpty()) {
			LOGGER.info("Patient CareGap is Not Opted-Out ");
		}
		return statusReason;
	}

}
