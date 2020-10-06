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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.ibm.wh.engmnt.orp.ops.FhirServerOps;
import org.ibm.wh.engmnt.orp.utility.DatabaseUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.generator.FHIRGenerator;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.resource.CommunicationRequest;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.Uri;
import com.ibm.fhir.model.type.code.CommunicationRequestStatus;

public class ModalityEligibilityCheck {

	private final static Logger LOGGER = LoggerFactory.getLogger(ModalityEligibilityCheck.class);
	private RestTemplate restTemplate;
	private FhirServerOps fhirServerOps;
	private ObjectMapper jsonParser;

	/**
	 * This method runs the mode sequence on the strict patient preference
	 * 
	 * @param communicationRequestId
	 * @return strictPatientStatus
	 * @throws Exception
	 */
	public boolean modalityEligibilitySequence(String communicationRequestId) throws Exception {

		LOGGER.info("Checking Modality Eligibility Rules with communicationRequestId :" + communicationRequestId);
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

		JsonNode patientTelecomNode = patientJsonData.get("telecom");

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

		String practitionerRecord = fhirServerOps.getFhirData(restTemplate,
				PropertyUtil.getProperty("fhir.practitioner.endpoint") + practitionerReferenceId);
		LOGGER.info("Practioner record retrieved successfully retrieved from FHIR Server with id: "
				+ practitionerReferenceId);

		Extension communicationMode = null;

		String eligibleModalityValue = "";

		String telecomData = patientTelecomNode.toString();
		List<JsonNode> telecomList = Arrays.asList(jsonParser.readValue(telecomData, JsonNode[].class));
		Map<String, String> telecomMap = telecomList.stream().filter(ele -> ele.get("rank") != null)
				.collect(Collectors.toMap(ele -> ele.get("rank").asText(), ele -> ele.get("system").asText(),
						(oldValue, newValue) -> oldValue, TreeMap::new));

		LOGGER.info("Patient telecom rank system map " + telecomMap);
		if (telecomMap != null && !telecomMap.isEmpty()) {

			for (Map.Entry<String, String> entry : telecomMap.entrySet()) {

				LOGGER.info("Modality sequence started for preference " + entry.getValue() + " with rank "
						+ entry.getKey());

				boolean checkForPatientModality = modalityEligibilityRules(contractConfigData, communicationRequestId,
						patientJsonData, practitionerRecord, entry.getValue());
				LOGGER.info(
						"Modality sequence ended for preference " + entry.getValue() + " with rank " + entry.getKey());
				if (checkForPatientModality) {
					LOGGER.info("Eligible Modality for preference " + entry.getValue() + " is : "
							+ checkForPatientModality);
					eligibleModalityValue = entry.getValue();
					break;
				} else {
					LOGGER.info("Eligible Modality for preference " + entry.getValue() + " is : "
							+ checkForPatientModality);
				}
			}

			if (!eligibleModalityValue.isEmpty()) {
				// update the commRequest with appropriate commMode
				String commRequestData = fhirServerOps.getFhirData(restTemplate,
						communicationRequestUrl + communicationRequestId);
				InputStream in = new ByteArrayInputStream(commRequestData.getBytes());

				CommunicationRequest commRequest = FHIRParser.parser(Format.JSON).parse(in);

				CommunicationRequest commRequestFinal = null;

				Code commMode = Code.builder().value(eligibleModalityValue).build();
				Coding commModeCoding = Coding.builder()
						.system(Uri.of("http://ibm.com/fhir/cdm/CodeSystem/eng-communication-mode-code-system"))
						.code(commMode).build();
				communicationMode = Extension.builder()
						.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-mode").value(commModeCoding)
						.build();
				commRequestFinal = commRequest.toBuilder().extension(communicationMode).build();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FHIRGenerator.generator(Format.JSON, true).generate(commRequestFinal, baos);
				LOGGER.info("Updating communication mode with the preference :" + eligibleModalityValue);
				fhirServerOps.updateFhirRecord(restTemplate, communicationRequestUrl + "?_id=" + communicationRequestId,
						baos.toString());
			}

			else {
				LOGGER.info("Patient has no eligible modality");

				// revoke the commRequest with status reason as no-eligible-modality..
				LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
				LOGGER.info("Revoking CommRequest status with status reason ::"
						+ PropertyUtil.getProperty("commrequest.noeligiblemodality"));
				String statusReason = PropertyUtil.getProperty("commrequest.noeligiblemodality");
				fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED, statusReason,
						communicationRequestUrl, communicationRequestId, restTemplate);
				return false;
			}

		} else {
			// revoke commRequestStatus as no-preferred-contact
			LOGGER.info("Patient has no preferred contact.");
			LOGGER.info("Revoking CommRequest status with id ::" + communicationRequestId);
			LOGGER.info("Revoking CommRequest status with status reason ::"
					+ PropertyUtil.getProperty("commrequest.nopreferredcontact"));
			String statusReason = PropertyUtil.getProperty("commrequest.nopreferredcontact");
			fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.REVOKED, statusReason, communicationRequestUrl,
					communicationRequestId, restTemplate);
			return false;
		}
		return true;
	}

	/*
	 * 
	 * This method executes modality rules
	 * 
	 * @param communicationRequestId
	 * 
	 * @return strictPatientStatus
	 * 
	 * @throws Exception
	 *
	 */
	public boolean modalityEligibilityRules(JsonNode contractConfigData, String commRequestId, JsonNode patientData,
			String practitionerRecord, String preference) throws Exception {
		LOGGER.info("Checking Modality rules");

		String communicationRequestUrl = PropertyUtil.getProperty("fhir.communicationrequest.endpoint");

		/* Contract Level Modality */
		LOGGER.info("Checking Contract Level Modality Rule");
		ContractLevelModality contractLevelModality = new ContractLevelModality();
		String contractLevelMoadlityStatusReason = contractLevelModality.checkContractLevelModalityCheck(preference,
				contractConfigData);
		if (contractLevelMoadlityStatusReason != null && !contractLevelMoadlityStatusReason.isEmpty()) {
			LOGGER.info("Updating CommunicationRequest status with id ::" + commRequestId + " with statusReason :"
					+ contractLevelMoadlityStatusReason);
			fhirServerOps.updateCommReqStatus(null, contractLevelMoadlityStatusReason, communicationRequestUrl,
					commRequestId, restTemplate);
			return false;
		}

		/* Modality Optout Rule */
		LOGGER.info("Checking Modality Opt out Rule");
		PatientModalityOptout patientModalityOptout = new PatientModalityOptout();
		String modalityOptoutStatus = patientModalityOptout.checkPatientModalityOptout(preference, patientData);

		if (modalityOptoutStatus != null && !modalityOptoutStatus.isEmpty()) {

			LOGGER.info("Updating CommunicationRequest status with id ::" + commRequestId + " with statusReason : "
					+ modalityOptoutStatus);
			fhirServerOps.updateCommReqStatus(null, modalityOptoutStatus, communicationRequestUrl, commRequestId,
					restTemplate);
			return false;
		}

		/* Modality Data Available Rule */
		LOGGER.info("Checking Modality Data Available Rule");
		ModalityDataAvailableForPatient modalityForPatient = new ModalityDataAvailableForPatient();
		String modalityDataAvailableStatusReason = modalityForPatient.checkPatientModalityData(preference, patientData);
		if (modalityDataAvailableStatusReason != null && !modalityDataAvailableStatusReason.isEmpty()) {
			LOGGER.info("Updating CommunicationRequest ::" + commRequestId + " with statusReason : "
					+ modalityDataAvailableStatusReason);
			fhirServerOps.updateCommReqStatus(null, modalityDataAvailableStatusReason, communicationRequestUrl,
					commRequestId, restTemplate);
			return false;
		}

		/* Check For Modality Over Communication Rule */
		LOGGER.info("Checking Modality Over Communication Rule");
		ModalityOverCommunication modalityOverCommunication = new ModalityOverCommunication();
		String patientId = patientData.get("id").asText();
		String statusReason = modalityOverCommunication.checkModalityOverCommunicationForPatientPreference(preference,
				patientId, contractConfigData);
		if (statusReason != null && !statusReason.isEmpty()) {
			LOGGER.info("Updating CommunicationRequest status with id ::" + commRequestId + " with statusReason : "
					+ statusReason);
			fhirServerOps.updateCommReqStatus(null, statusReason, communicationRequestUrl, commRequestId, restTemplate);
			return false;
		}

		/* Check For Practitioner Modality Rule */
		LOGGER.info("Checking Practitioner Level Modality Rule");
		String practitionerMoadlityStatusReason = null;
		LOGGER.info("Checking Modalities For Attributed Practitioner");
		PractitionerModality practitionerModality = new PractitionerModality();
		practitionerMoadlityStatusReason = practitionerModality.checkForPractitionerModalityCheck(practitionerRecord,
				preference);
		if (practitionerMoadlityStatusReason != null && !practitionerMoadlityStatusReason.isEmpty()) {
			LOGGER.info("Updating CommunicationRequest status with id ::" + commRequestId + " with statusReason :"
					+ practitionerMoadlityStatusReason);
			fhirServerOps.updateCommReqStatus(null, practitionerMoadlityStatusReason, communicationRequestUrl,
					commRequestId, restTemplate);
			return false;
		}

		return true;
	}
}
