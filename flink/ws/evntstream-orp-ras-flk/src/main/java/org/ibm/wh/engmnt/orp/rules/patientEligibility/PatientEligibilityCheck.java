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

import org.ibm.wh.engmnt.orp.ops.FhirServerOps;
import org.ibm.wh.engmnt.orp.utility.DatabaseUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fhir.model.type.code.CommunicationRequestStatus;

public class PatientEligibilityCheck {

	private ObjectMapper jsonParser;
	private RestTemplate restTemplate;
	private FhirServerOps fhirServerOps;

	private final static Logger LOGGER = LoggerFactory.getLogger(PatientEligibilityCheck.class);

	/*
	 * Method to check various rules on Patient Eligibility
	 * 
	 * @communicationRequestId is an identifier of Communication Request
	 * 
	 */
	public boolean checkForPatientRules(String communicationRequestId) throws Exception {

		LOGGER.info("Checking Patient Rules with communicationRequestId :" + communicationRequestId);
		jsonParser = new ObjectMapper();
		fhirServerOps = new FhirServerOps();
		restTemplate = new RestTemplate(fhirServerOps.getClientHttpRequestFactory());
		String communicationRequestUrl = PropertyUtil.getProperty("fhir.communicationrequest.endpoint");

		String commReqData = fhirServerOps.getFhirData(restTemplate, communicationRequestUrl + communicationRequestId);
		JsonNode communicationRequestNode = jsonParser.readValue(commReqData, JsonNode.class);
		String patientIdRef = communicationRequestNode.get("subject").get("reference").asText();
		String patientId = patientIdRef.split("/")[1];

		String patientRecord = fhirServerOps.getFhirData(restTemplate,
				PropertyUtil.getProperty("fhir.patient.endpoint") + patientId);
		JsonNode patientJsonData = jsonParser.readValue(patientRecord, JsonNode.class);
		DatabaseUtil dataBaseUtil = new DatabaseUtil();
		String contractInfo = dataBaseUtil.getConfigData(PropertyUtil.getProperty("contractconfig.tablename"));
		JsonNode contractConfigData = jsonParser.readValue(contractInfo, JsonNode.class);

		/* Check For Patient Consent Rule */
		LOGGER.info("Checking For Patient Consent Rule");
		PatientConsent patientConsent = new PatientConsent();
		String patientConsentStatusReason = patientConsent.checkForPatientConsent(contractConfigData, patientJsonData,
				communicationRequestNode);
		if (!patientConsentStatusReason.isEmpty()) {
			LOGGER.info("Revoking CommRequest status as Patient has no consent ");
			LOGGER.info("Revoking CommRequest status with status reason ::" + patientConsentStatusReason);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED,
					patientConsentStatusReason, communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}

		/* Check For Patient Modality Rule */
		LOGGER.info("Checking For Patient Modality Rule");
		PatientModality patientModality = new PatientModality();
		String patientModalityStatusReason = patientModality.checkForPatientModality(patientJsonData);
		if (patientModalityStatusReason != null && !patientModalityStatusReason.isEmpty()) {
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
			LOGGER.info("Revoking CommRequest status with status reason ::" + patientModalityStatusReason);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED,
					patientModalityStatusReason, communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}

		/* Check For Attributed Practitioner Rule */

		LOGGER.info("Checking for Patient Attributed Practitioner Rule ");
		PatientAttributedPractioner patientAttributedPractioner = new PatientAttributedPractioner();
		String patientAttributedPractitionerStatusReason = patientAttributedPractioner
				.checkForPatientPractitioner(contractConfigData, patientJsonData, communicationRequestNode);
		if (!patientAttributedPractitionerStatusReason.isEmpty()) {
			LOGGER.info("Patient doesn't have an Attributed Practitioner");
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
			LOGGER.info("Updating Status Reason ::" + patientAttributedPractitionerStatusReason);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED,
					patientAttributedPractitionerStatusReason, communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}

		/* Check for Patient Active Status */
		LOGGER.info("Checking For Patient Active Status Rule");
		PatientActiveStatus patientActive = new PatientActiveStatus();
		String patientActiveStatusReason = patientActive.activePatientCheck(contractConfigData, patientJsonData);
		if (!patientActiveStatusReason.isEmpty()) {
			LOGGER.info("Patient is Inactive");
			LOGGER.info("Revoking Communication Request Status");
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED,
					patientActiveStatusReason, communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}

		/* Check For Patient Importance Status Rule */
		LOGGER.info("Checking For Patient Importance Status Rule");
		PatientImportance patientImportanceCheck = new PatientImportance();
		String patientImportanceStatusReason = patientImportanceCheck.validateImportanceStatus(patientJsonData,
				contractConfigData);
		if (patientImportanceStatusReason != null && !patientImportanceStatusReason.isEmpty()) {
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED,
					patientImportanceStatusReason, communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}
		
		/* Check For Patient Global Opt-out Rule */
		LOGGER.info("Checking For Patient Global Opt-out Rule");
		PatientGlobalOptoutStatus patientGlobalOptOut = new PatientGlobalOptoutStatus();
		String patientGlobalOptoutStatusReason = patientGlobalOptOut.patientGlobalOptoutStatusCheck(contractConfigData,
				communicationRequestNode, patientJsonData);
		if (patientGlobalOptoutStatusReason != null && !patientGlobalOptoutStatusReason.isEmpty()) {
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED,
					patientGlobalOptoutStatusReason, communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}
		
		/*Check Patient Decease Status*/
		LOGGER.info("Checking For Patient Decease Rule");
		PatientDeceaseStatus patientDecease = new PatientDeceaseStatus();
		String patientDeceaseStatusReason = patientDecease.checkForPatientDecease(patientJsonData);
		if (patientDeceaseStatusReason != null && !patientDeceaseStatusReason.isEmpty()) {
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED,
					patientDeceaseStatusReason, communicationRequestUrl, communicationRequestId, restTemplate);
			return false;
		}
		return true;
	}

}
