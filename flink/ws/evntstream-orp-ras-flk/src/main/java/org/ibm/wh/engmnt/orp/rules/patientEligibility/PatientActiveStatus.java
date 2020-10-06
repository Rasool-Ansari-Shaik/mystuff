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

public class PatientActiveStatus {

	private final static Logger LOGGER = LoggerFactory.getLogger(PatientActiveStatus.class);

	public String activePatientCheck(JsonNode contractConfigData, JsonNode patientData) {

		String statusReason = "";
		String productName=PropertyUtil.getProperty("commrequest.commproduct.value");
		boolean activeFlagContract = contractConfigData.get("offerings").get(productName).get("patient")
				.get("allowOnlyActive").asBoolean();
		LOGGER.info("Allow Active Patients from Tenant Configuration " + activeFlagContract);
		if (activeFlagContract) {
			JsonNode patientActiveNode = patientData.get("active");
			if (patientActiveNode != null) {
				boolean isActivePatient = patientActiveNode.asBoolean();
				LOGGER.info("Patient Active status :" + isActivePatient);
				if (!isActivePatient) {
					statusReason = "patient-inactive";
				}
			}
		}

		LOGGER.info("Patient Active check status Reason " + statusReason);

		return statusReason;
	}

}
