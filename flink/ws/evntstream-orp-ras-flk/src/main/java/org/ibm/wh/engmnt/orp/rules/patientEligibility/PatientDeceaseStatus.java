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

import com.fasterxml.jackson.databind.JsonNode;

public class PatientDeceaseStatus {

	private final static Logger LOGGER = LoggerFactory.getLogger(PatientDeceaseStatus.class);

	/**
	 * This method will check if patient is deceased or not and update status
	 * accordingly
	 * 
	 * @param communicationData
	 * @param contractConfigData
	 * @return statusReason
	 */
	public String checkForPatientDecease(JsonNode patientJsonData) {
		String statusReason = "";
		JsonNode deceasedDateTimeNode = patientJsonData.get("deceasedDateTime");
		if (deceasedDateTimeNode != null) {
			String patientDeceaseDate = deceasedDateTimeNode.asText();
			// Check if patient has deceaseDate then update the status
			LOGGER.info("Patient Deceased Date: " + patientDeceaseDate);
			statusReason = PropertyUtil.getProperty("patient.decease.status.value");
			LOGGER.info("Patient deceased status reason:" + statusReason);
		} else {
			LOGGER.info("Patient Not deceased");
		}
		return statusReason;
	}
}
