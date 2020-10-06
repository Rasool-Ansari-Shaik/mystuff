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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ibm.wh.engmnt.orp.utility.DatabaseUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.generator.FHIRGenerator;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.parser.exception.FHIRParserException;
import com.ibm.fhir.model.resource.CommunicationRequest;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.CodeableConcept;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.Uri;
import com.ibm.fhir.model.type.code.CommunicationRequestStatus;

public class MeasureReportServiceImpl extends RichMapFunction<String, String> {
	private final static Logger LOGGER = LoggerFactory.getLogger(MeasureReportServiceImpl.class);
	private static final long serialVersionUID = 1L;
	private transient ObjectMapper jsonParser;
	private transient RestTemplate restTemplate;

	@Override
	public String map(String measureReport) throws Exception {
		LOGGER.debug("Extracting measure report started");
		if (jsonParser == null && restTemplate == null) {
			jsonParser = new ObjectMapper();
			restTemplate = new RestTemplate(getClientHttpRequestFactory());
		}
		JsonNode measureReportData = jsonParser.readValue(measureReport, JsonNode.class);
		LOGGER.info("Reading MeasureReport with id: " + measureReportData.get("id").asText());

		String[] patientData = measureReportData.get("subject").get("reference").asText().split("/");
		String patientId = patientData[1];
		String patientUrl = PropertyUtil.getProperty("fhir.patient.endpoint") + patientId;
		String patientRecord = getFhirData(restTemplate, patientUrl);
		LOGGER.info("Patient record retrieved from FHIR Server with id: " + patientId);

		JsonNode patientJsonData = jsonParser.readValue(patientRecord, JsonNode.class);
		String[] practitionerRole = patientJsonData.get("generalPractitioner").get(0).get("reference").asText()
				.split("/");
		String practitionerRoleId = practitionerRole[1];

		String practitionerRoleUrl = PropertyUtil.getProperty("fhir.practitionerrole.endpoint") + practitionerRoleId;
		String practitionerRoleRecord = getFhirData(restTemplate, practitionerRoleUrl);
		LOGGER.info("PractionerRole record retrieved successfully retrieved from FHIR Server with id: "
				+ practitionerRoleId);
		JsonNode practitionerRoleData = jsonParser.readValue(practitionerRoleRecord, JsonNode.class);
		String[] practitioner = practitionerRoleData.get("practitioner").get("reference").asText().split("/");
		String practitionerId = practitioner[1];
		String practitionerUrl = PropertyUtil.getProperty("fhir.practitioner.endpoint") + practitionerId;
		String practitionerRecord = getFhirData(restTemplate, practitionerUrl);
		LOGGER.info("Practioner record retrieved successfully retrieved from FHIR Server with id: " + practitionerId);
		String[] location = practitionerRoleData.get("location").get(0).get("reference").asText().split("/");
		String locationId = location[1];
		String locationUrl = PropertyUtil.getProperty("fhir.location.endpoint") + locationId;
		String locationRecord = getFhirData(restTemplate, locationUrl);
		LOGGER.info("Location record retrieved successfully retrieved from FHIR Server with id: " + locationId);
		LOGGER.info("Fetching the information from Contract Config DB table");
		DatabaseUtil dataBaseUtil = new DatabaseUtil();
		String contractInfo = dataBaseUtil.getConfigData(PropertyUtil.getProperty("contractconfig.tablename"));
		String careGapName = null;
		for (int i = 0; i < measureReportData.get("group").get(0).get("population").size(); i++) {
			String code = measureReportData.get("group").get(0).get("population").get(i).get("code").get("coding")
					.get(0).get("code").asText();
			int count = measureReportData.get("group").get(0).get("population").get(i).get("count").asInt();
			if (code.equalsIgnoreCase("care-gap") && count > 0) {
				careGapName = measureReportData.get("group").get(0).get("population").get(i).get("id").asText();
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
		String communicationRequestData = commRequestServiceImpl.generateFhirCommunicationRequestModel(contractInfo,"117",
				practitionerId, practitionerRoleId, patientRecord, practitionerRecord, careGapName, facilityAddress,
				"");
		String communicationRequestUrl = PropertyUtil.getProperty("fhir.communicationrequest.endpoint");
		LOGGER.info("Posting CommunicationRequest Payload to FHIR Server");
		Map<String, String> communicationResponse = insertIntoFhirRecord(restTemplate, communicationRequestUrl,
				communicationRequestData);
		String locationHeaderUrl = communicationResponse.get("locationHeader");
		String[] locationHeaderData = locationHeaderUrl
				.substring(locationHeaderUrl.indexOf(communicationRequestUrl) + communicationRequestUrl.length())
				.split("/");
		String communicationRequestId = locationHeaderData[0];
		LOGGER.info("CommunicationRequest successfully inserted into FHIR Server with id: " + communicationRequestId);
		String measureReportIdentifier = measureReportData.get("identifier").get(0).get("value").asText();
		CarePlanServiceImpl carePlanModelImpl = new CarePlanServiceImpl();
		LOGGER.info("Reading the CarPlan Model");
		String careplanData = carePlanModelImpl.generateFhirCarePlanModel(measureReportIdentifier,
				communicationRequestId, practitionerRoleId, patientId, careGapName);
		String careplanUrl = PropertyUtil.getProperty("fhir.careplan.endpoint");
		LOGGER.info("Posting CarePlan Payload to FHIR Server");
		Map<String, String> carePlanResponse = insertIntoFhirRecord(restTemplate, careplanUrl, careplanData);
		String carePlanLocationHeaderUrl = carePlanResponse.get("locationHeader");
		String[] carePlanLocationHeaderData = carePlanLocationHeaderUrl
				.substring(carePlanLocationHeaderUrl.indexOf(careplanUrl) + careplanUrl.length()).split("/");
		String carePlanId = carePlanLocationHeaderData[0];
		LOGGER.info("CarePlan successfully inserted into FHIR Server with id: " + carePlanId);

		String commReqData = getFhirData(restTemplate, communicationRequestUrl + communicationRequestId);
		
		String fhirCommData = getFhirData(restTemplate, communicationRequestUrl + communicationRequestId);

		return fhirCommData;
	}

	public HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient());
		return clientHttpRequestFactory;
	}

	public HttpClient httpClient() {
		String user = PropertyUtil.getProperty("fhir.user");
		String pwd = PropertyUtil.getProperty("fhir.password");
		Credentials credentials = new UsernamePasswordCredentials(user, pwd);
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		return client;
	}

	public String getFhirData(RestTemplate client, String url) throws Exception {
		String fhirRecord = client.getForObject(url, String.class);
		return fhirRecord;
	}

	public Map<String, String> insertIntoFhirRecord(RestTemplate client, String url, String requestBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
		ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, request, String.class);
		String json = response.getBody();
		HttpStatus httpStatusCode = response.getStatusCode();
		HttpHeaders responseHeaders = response.getHeaders();
		Map<String, String> responseMap = new HashMap<String, String>();
		responseMap.put("statusCode", httpStatusCode.toString());
		responseMap.put("locationHeader", responseHeaders.get("Location").toString());
		LOGGER.debug("Returning CommunicationRequest report");
		return responseMap;
	}

	public void updateFhirRecord(RestTemplate client, String url, String requestBody) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
		ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, request, String.class);
	}

	public void updateCommReqStatus(String commData, CommunicationRequestStatus status, String statusReason,
			String communicationRequestUrl, String communicationRequestId)
			throws FHIRParserException, FHIRGeneratorException {
		InputStream in = new ByteArrayInputStream(commData.getBytes());

		CommunicationRequest commRequest = FHIRParser.parser(Format.JSON).parse(in);

		CommunicationRequest commRequestFinal = null;
		Code code = Code.builder().value(statusReason).build();
		Coding coding = Coding.builder()
				.system(Uri.of("http://ibm.com/fhir/cdm/CodeSystem/communication-status-reason-general-code-system"))
				.code(code).display(com.ibm.fhir.model.type.String.of(statusReason)).build();

		CodeableConcept statReason = CodeableConcept.builder().coding(coding).build();

		if (commRequest.getStatusReason() != null) {
			commRequestFinal = commRequest.toBuilder().status(status)
					.statusReason(commRequest.getStatusReason().toBuilder().coding(coding).build()).build();
		} else {
			commRequestFinal = commRequest.toBuilder().status(status).statusReason(statReason).build();

		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FHIRGenerator.generator(Format.JSON, true).generate(commRequestFinal, baos);
		updateFhirRecord(restTemplate, communicationRequestUrl + "?_id=" + communicationRequestId, baos.toString());

	}

}