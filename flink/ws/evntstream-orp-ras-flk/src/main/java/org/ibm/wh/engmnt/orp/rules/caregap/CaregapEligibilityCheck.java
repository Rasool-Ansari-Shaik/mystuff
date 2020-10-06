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
import org.ibm.wh.engmnt.orp.utility.DatabaseUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fhir.model.type.code.CommunicationRequestStatus;

public class CaregapEligibilityCheck {
	private ObjectMapper jsonParser;
	private RestTemplate restTemplate;
	private FhirServerOps fhirServerOps;

	private final static Logger LOGGER = LoggerFactory.getLogger(CaregapEligibilityCheck.class);

	public boolean checkForPatientCaregapRules(String communicationRequestId) throws Exception {
		LOGGER.info("Checking Caregap Rules with communicationRequestId :" + communicationRequestId);

		jsonParser = new ObjectMapper();
		fhirServerOps = new FhirServerOps();
		restTemplate = new RestTemplate(fhirServerOps.getClientHttpRequestFactory());
		String communicationRequestUrl = PropertyUtil.getProperty("fhir.communicationrequest.endpoint");
		DatabaseUtil dataBaseUtil = new DatabaseUtil();
		String commReqData = fhirServerOps.getFhirData(restTemplate, communicationRequestUrl + communicationRequestId);
		JsonNode communicationRequestNode = jsonParser.readValue(commReqData, JsonNode.class);
		String contractInfo = dataBaseUtil.getConfigData(PropertyUtil.getProperty("contractconfig.tablename"));
		JsonNode contractConfigData = jsonParser.readValue(contractInfo, JsonNode.class);
		String patientIdRef = communicationRequestNode.get("subject").get("reference").asText();
		String patientId = patientIdRef.split("/")[1];

		/* Practitioner Role */

		JsonNode extension = communicationRequestNode.get("extension");
		String practitionerRole = null;
		for (int i = 0; i < extension.size(); i++) {
			JsonNode communicationData = extension.get(i);
			if (communicationData.get("url").asText().endsWith("communication-on-behalf-of")) {
				practitionerRole = communicationData.get("valueReference").get("reference").asText();
				break;
			}
		}

		String practitionerRoleId = practitionerRole.split("/")[1];
		LOGGER.info("Checking PractitionerRoleId ::" + practitionerRoleId);
		String practitionerRoleRecord = fhirServerOps.getFhirData(restTemplate,
				PropertyUtil.getProperty("fhir.practitionerrole.endpoint") + practitionerRoleId);

		LOGGER.info("PractionerRole record retrieved successfully retrieved from FHIR Server with id: "
				+ practitionerRoleId);

		JsonNode practitionerRoleData = jsonParser.readValue(practitionerRoleRecord, JsonNode.class);
		String practitionerReferenceId = practitionerRoleData.get("practitioner").get("reference").asText()
				.split("/")[1];
		String locationReferenceId = practitionerRoleData.get("location").get(0).get("reference").asText()
				.split("/")[1];

		String practitionerRecord = fhirServerOps.getFhirData(restTemplate,
				PropertyUtil.getProperty("fhir.practitioner.endpoint") + practitionerReferenceId);
		LOGGER.info("Practioner record retrieved successfully retrieved from FHIR Server with id: "
				+ practitionerReferenceId);

		String locationRecord = fhirServerOps.getFhirData(restTemplate,
				PropertyUtil.getProperty("fhir.location.endpoint") + locationReferenceId);
		LOGGER.info(
				"Location record retrieved successfully retrieved from FHIR Server with id: " + locationReferenceId);

		String careGapConfigInfo = dataBaseUtil.getConfigData(PropertyUtil.getProperty("caregapconfig.tablename"));
		String practitionerConfigInfo = dataBaseUtil
				.getConfigData(PropertyUtil.getProperty("practitionerconfig.tablename"));

		String locationConfigInfo = dataBaseUtil.getConfigData(PropertyUtil.getProperty("locationconfig.tablename"));

		LOGGER.info("Checking Patient CareGap Optout Status");
		CaregapOptout careGapOptOutCheck = new CaregapOptout();
		String patientCareGapOptOutStatusReason = careGapOptOutCheck
				.checkForPatientCaregapOptout(communicationRequestNode);
		if (patientCareGapOptOutStatusReason != null && !patientCareGapOptOutStatusReason.isEmpty()) {
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED, patientCareGapOptOutStatusReason,
					communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}

		/* Check For CareGap Past Communication History Rule */
		LOGGER.info("Checking for Caregap Past Communication");
		JsonNode communicationData = fhirServerOps.getLatestCommunicationRecord(communicationRequestNode, restTemplate);
		CaregapPastCommunication careGapPastCommunication = new CaregapPastCommunication();
		String patientCommHistory = careGapPastCommunication.checkForPatientPastCommunication(communicationData,
				contractConfigData);
		if (patientCommHistory != null && !patientCommHistory.isEmpty()) {
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestNode.get("id").asText());
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED, patientCommHistory,
					communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}

		/* Check For Past Appointments LookBack Rule */

		LOGGER.info("Checking Appointment LookBack Rule");

		AppointmentLookBack appointmentLookBack = new AppointmentLookBack();
		boolean appointmentLookBackFlag = appointmentLookBack.checkForPastAppointments(communicationRequestId,
				patientId, locationRecord, practitionerRecord, careGapConfigInfo, practitionerConfigInfo,
				locationConfigInfo);
		LOGGER.info("Appointment look back Status :" + appointmentLookBackFlag);
		if (appointmentLookBackFlag) {
			String appointmentLookBackStatusReason = PropertyUtil.getProperty("commrequest.appointmentlookback");
			LOGGER.info("Revoking CommunicationRequest status with id ::" + communicationRequestId
					+ " with statusReason :" + appointmentLookBackStatusReason);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED, appointmentLookBackStatusReason,
					communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}

		return true;
	}
}
