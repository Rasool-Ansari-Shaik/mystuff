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

import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class ModalityDataAvailableForPatient {

	private final static Logger LOGGER = LoggerFactory.getLogger(ModalityDataAvailableForPatient.class);

	/**
	 * Method to check the Patient Modality Data
	 * @param preference
	 * @param patientData
	 * @return statusReason
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public String checkPatientModalityData(String preference, JsonNode patientData)
			throws JsonMappingException, JsonProcessingException {
		LOGGER.info("Validating Modality Data Availability Rule for : " + preference);
		String statusReason = "";
		JsonNode patientTelecomNode = patientData.get("telecom");
		for (int i = 0; i < patientTelecomNode.size(); i++) {
			if (patientTelecomNode.get(i).get("system") != null) {
				if (patientTelecomNode.get(i).get("system").asText().equalsIgnoreCase(preference)) {
					if (patientTelecomNode.get(i).get("value") == null) {
						statusReason = PropertyUtil.getProperty("patient.communication.preference")
								.replace("<preference>", preference);
						LOGGER.info("Status reason for Modality Data availability rule : " + statusReason);
					} else {
						LOGGER.info("Modality Data available for the Patient");
					}
				}
			}
		}
		return statusReason;
	}
}
