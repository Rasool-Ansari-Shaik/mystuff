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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class PatientModalityOptout {
	private final static Logger LOGGER = LoggerFactory.getLogger(PatientModalityOptout.class);

	public String checkPatientModalityOptout(String preference, JsonNode patientJson) throws Exception {

		String statusReason = "";
		String modalityOptedout = "";

		JsonNode mainExtension = patientJson.get("extension");
		for (int i = 0; i < mainExtension.size(); i++) {
			if (mainExtension.get(i).get("url").asText().endsWith("communication-opt-out")) {
				JsonNode innerData = mainExtension.get(i).get("extension");
				for (int j = 0; j < innerData.size(); j++) {
					if (innerData.get(j).get("url").asText().endsWith("communication-mode")) {
						modalityOptedout = innerData.get(j).get("valueCodeableConcept").get("text").asText();
						LOGGER.debug("Patient Modality Optout : " + modalityOptedout);
						if (modalityOptedout.equalsIgnoreCase(preference)) {
							statusReason = "np-opt-out-" + preference;
							LOGGER.info("Status reason for Modality Optout : " + statusReason);
							break;
						}

					}
				}
			}
			if (!statusReason.isEmpty())
				break;
		}
		if (statusReason.isEmpty()) {
			LOGGER.info("Patient Modality is Opted-Out ");
		}
		return statusReason;

	}
}
