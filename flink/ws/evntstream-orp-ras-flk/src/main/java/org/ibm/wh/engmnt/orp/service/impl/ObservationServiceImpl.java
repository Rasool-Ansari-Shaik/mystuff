/*******************************************************************************
 * Watson Health Imaging Analytics
 *
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2020
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.service.impl;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.ibm.wh.engmnt.orp.ops.FhirServerOps;
import org.ibm.wh.engmnt.orp.rules.caregap.CaregapEligibilityCheck;
import org.ibm.wh.engmnt.orp.rules.modalityEligibility.ModalityEligibilityCheck;
import org.ibm.wh.engmnt.orp.rules.patientEligibility.PatientEligibilityCheck;
import org.ibm.wh.engmnt.orp.service.ObservationService;
import org.ibm.wh.engmnt.orp.utility.DatabaseUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fhir.model.type.code.CommunicationRequestStatus;

public class ObservationServiceImpl implements ObservationService {
	private final static Logger LOGGER = LoggerFactory.getLogger(ObservationServiceImpl.class);
	private static final long serialVersionUID = 1L;
	private transient ObjectMapper jsonParser;
	private transient RestTemplate restTemplate;
	private transient FhirServerOps fhirServerOps;

	@Override
	public void processObservation(String observationMsg) throws Exception {
		LOGGER.debug("Extracting Observation Event started");

		if (jsonParser == null && restTemplate == null) {
			jsonParser = new ObjectMapper();
			fhirServerOps = new FhirServerOps();
			restTemplate = new RestTemplate(fhirServerOps.getClientHttpRequestFactory());
		}
		LOGGER.info("Observation Event from the topic  " + observationMsg);
		JsonNode observationNode = jsonParser.readValue(observationMsg, JsonNode.class);

		if (observationNode.get("eventType").asText().equalsIgnoreCase("observationIdentified")) {
			String tenantId = observationNode.get("tenantId").asText();
			String id = observationNode.get("identifier").asText();
			String observationEvent = fhirServerOps.getFhirData(restTemplate,
					PropertyUtil.getProperty("fhir.observation.endpoint") + id);
			JsonNode observationEventDetails = jsonParser.readValue(observationEvent, JsonNode.class);
			LOGGER.info("Reading Observation with id: " + observationEventDetails.get("id").asText());

			String[] patientMPI = observationEventDetails.get("subject").get("reference").asText().split("/");
			String patientMpiId = patientMPI[1];
			String patientMpiUrl = PropertyUtil.getProperty("fhir.patient.endpoint") + patientMpiId;
			String patientMpiRecord = fhirServerOps.getFhirData(restTemplate, patientMpiUrl);
			JsonNode patientMpiData = jsonParser.readValue(patientMpiRecord, JsonNode.class);
			String sourceSystemName = getSourceSystemFromContract();
			String patientId = getPatientFromMpiRecord(patientMpiData, sourceSystemName);
			if (patientId != null && !patientId.isEmpty()) {
				String patientUrl = PropertyUtil.getProperty("fhir.patient.endpoint") + patientId;
				String patientRecord = fhirServerOps.getFhirData(restTemplate, patientUrl);
				LOGGER.info("Patient id : " + patientId);

				String[] practitionerRole = null;
				JsonNode extensionData = observationEventDetails.get("extension");

				for (int i = 0; i < extensionData.size(); i++) {
					String url = extensionData.get(i).get("url").asText();
					if (url.endsWith("assigned-practitioner")) {

						practitionerRole = extensionData.get(i).get("valueReference").get("reference").asText()
								.split("/");
						break;
					}
				}

				String practitionerRoleId = practitionerRole[1];

				String practitionerRoleUrl = PropertyUtil.getProperty("fhir.practitionerrole.endpoint")
						+ practitionerRoleId;
				String practitionerRoleRecord = fhirServerOps.getFhirData(restTemplate, practitionerRoleUrl);
				LOGGER.info("PractionerRole record retrieved successfully retrieved from FHIR Server with id: "
						+ practitionerRoleId);
				JsonNode practitionerRoleData = jsonParser.readValue(practitionerRoleRecord, JsonNode.class);
				String[] practitioner = practitionerRoleData.get("practitioner").get("reference").asText().split("/");
				String practitionerId = practitioner[1];
				String practitionerUrl = PropertyUtil.getProperty("fhir.practitioner.endpoint") + practitionerId;
				String practitionerRecord = fhirServerOps.getFhirData(restTemplate, practitionerUrl);
				LOGGER.info("Practioner record retrieved successfully retrieved from FHIR Server with id: "
						+ practitionerId);
				String[] location = practitionerRoleData.get("location").get(0).get("reference").asText().split("/");
				String locationId = location[1];
				String locationUrl = PropertyUtil.getProperty("fhir.location.endpoint") + locationId;
				String locationRecord = fhirServerOps.getFhirData(restTemplate, locationUrl);
				LOGGER.info("Location record retrieved successfully retrieved from FHIR Server with id: " + locationId);
				LOGGER.info("Fetching the information from Contract Config DB table");
				DatabaseUtil dataBaseUtil = new DatabaseUtil();
				String contractInfo = dataBaseUtil.getConfigData(PropertyUtil.getProperty("contractconfig.tablename"));
				LOGGER.info("Fetching the information from Location Config DB table");
				String locationConfigInfo = dataBaseUtil.getConfigData(PropertyUtil.getProperty("locationconfig.tablename"));;
				String careGapName = null;
				// CareGapID from Observation
				JsonNode measurePopulation = observationEventDetails.get("code").get("coding");
				for (int j = 0; j < measurePopulation.size(); j++) {

					if (measurePopulation.get(j).get("system").asText().endsWith("measure-population-type-code-system")
							&& measurePopulation.get(j).get("code").asText().equals("care-gap")) {

						for (int i = 0; i < observationEventDetails.get("extension").size(); i++) {
							String url = observationEventDetails.get("extension").get(i).get("url").asText();
							if (url.endsWith("measure-population-id")) {
								careGapName = observationEventDetails.get("extension").get(i).get("valueId").asText();
								break;
							}

						}

						break;

					}

				}
				JsonNode locationAddress = jsonParser.readValue(locationRecord, JsonNode.class);
				String facilityAddress = locationAddress.get("address").get("line").asText() + " "
						+ locationAddress.get("address").get("city").asText() + " "
						+ locationAddress.get("address").get("district").asText() + " "
						+ locationAddress.get("address").get("state").asText() + " "
						+ locationAddress.get("address").get("postalCode").asText() + " "
						+ locationAddress.get("address").get("country").asText();
				CommunicationRequestServiceImpl commRequestServiceImpl = new CommunicationRequestServiceImpl();
				LOGGER.info("Reading the CommunicationRequest Model");
				String communicationRequestData = commRequestServiceImpl.generateFhirCommunicationRequestModel(
						contractInfo, tenantId, practitionerId, practitionerRoleId, patientRecord, practitionerRecord,
						careGapName, facilityAddress, locationConfigInfo);
				String communicationRequestUrl = PropertyUtil.getProperty("fhir.communicationrequest.endpoint");
				LOGGER.info("Posting CommunicationRequest Payload to FHIR Server");
				Map<String, String> communicationResponse = fhirServerOps.insertIntoFhirRecord(restTemplate,
						communicationRequestUrl, communicationRequestData);
				String locationHeaderUrl = communicationResponse.get("locationHeader");
				String[] locationHeaderData = locationHeaderUrl
						.substring(
								locationHeaderUrl.indexOf(communicationRequestUrl) + communicationRequestUrl.length())
						.split("/");
				String communicationRequestId = locationHeaderData[0];
				LOGGER.info("CommunicationRequest successfully inserted into FHIR Server with id: "
						+ communicationRequestId);
				/*
				 * CarePlanServiceImpl carePlanModelImpl = new CarePlanServiceImpl();
				 * LOGGER.info("Reading the CarPlan Model");
				 * 
				 * UUID carePlanIdentifier = UUID.randomUUID();
				 * 
				 * String careplanData =
				 * carePlanModelImpl.generateFhirCarePlanModel(carePlanIdentifier.toString(),
				 * communicationRequestId, practitionerRoleId, patientId, careGapName);
				 * 
				 * String careplanUrl = PropertyUtil.getProperty("fhir.careplan.endpoint");
				 * LOGGER.info("Posting CarePlan Payload to FHIR Server"); Map<String, String>
				 * carePlanResponse = fhirServerOps.insertIntoFhirRecord(restTemplate,
				 * careplanUrl, careplanData); String carePlanLocationHeaderUrl =
				 * carePlanResponse.get("locationHeader"); String[] carePlanLocationHeaderData =
				 * carePlanLocationHeaderUrl
				 * .substring(carePlanLocationHeaderUrl.indexOf(careplanUrl) +
				 * careplanUrl.length()).split("/"); String carePlanId =
				 * carePlanLocationHeaderData[0];
				 * LOGGER.info("CarePlan successfully inserted into FHIR Server with id: " +
				 * carePlanId);
				 */
				PatientEligibilityCheck patientEligibilityCheck = new PatientEligibilityCheck();
				boolean patientEligibility = patientEligibilityCheck.checkForPatientRules(communicationRequestId);
				LOGGER.info("Patient Eligibility Check Status is : " + patientEligibility);

				if (patientEligibility) {
					LOGGER.info("Patient eligibility check is passed");
					CaregapEligibilityCheck caregapModalityCheck = new CaregapEligibilityCheck();
					boolean caregapEligibility = caregapModalityCheck
							.checkForPatientCaregapRules(communicationRequestId);
					LOGGER.info("CareGap Eligibility Check Status is : " + caregapEligibility);
					if (caregapEligibility) {
						LOGGER.info("Patient Caregap rules check is passed");
						/*
						 * Check Modality eligibility rules
						 */
						ModalityEligibilityCheck modalityEligibilityCheck = new ModalityEligibilityCheck();
						boolean modalityEligibility = modalityEligibilityCheck
								.modalityEligibilitySequence(communicationRequestId);
						LOGGER.info("Modality Eligibility Check status is : " + modalityEligibility);
						if (modalityEligibility) {
							LOGGER.info("Updating Comm request status as Active ");

							fhirServerOps.updateCommReqStatus(CommunicationRequestStatus.ACTIVE, null,
									communicationRequestUrl, communicationRequestId, restTemplate);
						} else {
							LOGGER.info("Modality rules check is failed");
						}

					} else {
						LOGGER.info("Patient Caregap rules check is failed");
					}

				} else {
					LOGGER.info("Patient eligibility check is failed");
				}

			} else {
				LOGGER.warn("No Patient Record found for the source system " + sourceSystemName
						+ " in Patient MPI Record with ID: " + patientMpiId);
			}

		}

	}

	/* Method for Strict Source System Rule */

	public String getPatientFromMpiRecord(JsonNode patientMpiData, String sourceSystemName) throws Exception {
		LOGGER.debug("Start of Source system rule method");

		String patientId = "";
		JsonNode identifierNode = patientMpiData.get("identifier");
		if (identifierNode != null) {
			for (int i = 0; i < identifierNode.size(); i++) {
				JsonNode patientSourceSystem = identifierNode.get(i);
				if (patientSourceSystem.get("system") != null
						&& patientSourceSystem.get("system").asText().contains(sourceSystemName)) {
					LOGGER.info("MatchPatient resource contains the SourceSystem name in the system url "
							+ patientSourceSystem.get("system").asText());
					patientId = patientSourceSystem.get("value").asText();
					break;
				}
			}
		}

		return patientId;
	}

	public String getSourceSystemFromContract() throws JsonMappingException, JsonProcessingException, SQLException {
		DatabaseUtil dataBaseUtil = new DatabaseUtil();
		String contractConfigData = dataBaseUtil.getConfigData(PropertyUtil.getProperty("contractconfig.tablename"));
		String sourceSystemName = "";
		jsonParser = new ObjectMapper();
		String productName = PropertyUtil.getProperty("commrequest.commproduct.value");
		JsonNode contractinfo = jsonParser.readValue(contractConfigData, JsonNode.class);
		JsonNode sourceSystem = contractinfo.get("offerings").get(productName).get("patient").get("sourceSystem");
		Iterator fields = sourceSystem.fieldNames();
		while (fields.hasNext()) {
			String source = fields.next().toString();
			JsonNode sourceSystemNode = sourceSystem.get(source);
			if (sourceSystemNode.get("enabled").asBoolean()) {
				LOGGER.info(source + " SourceSystem type is enabled " + sourceSystemNode.get("enabled"));
				if (source.equals("strict")) {
					sourceSystemName = sourceSystemNode.get("name").asText();
					break;
				}
			}
		}

		return sourceSystemName;

	}

}