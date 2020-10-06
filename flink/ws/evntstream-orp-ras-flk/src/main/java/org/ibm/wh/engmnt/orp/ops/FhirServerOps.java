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

package org.ibm.wh.engmnt.orp.ops;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
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
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.resource.CommunicationRequest;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.CodeableConcept;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.Uri;
import com.ibm.fhir.model.type.code.CommunicationRequestStatus;

public class FhirServerOps {

	private final static Logger LOGGER = LoggerFactory.getLogger(FhirServerOps.class);

	/**
	 * getClientHttpRequestFactory
	 * @return clientHttpRequestFactory
	 */
	public HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient());
		return clientHttpRequestFactory;
	}

	/**
	 * httpClient
	 * @return client
	 */
	public HttpClient httpClient() {
		LOGGER.info("Establishing connection to fhir server");
		String user = PropertyUtil.getProperty("fhir.user");
		String pwd = PropertyUtil.getProperty("fhir.password");
		Credentials credentials = new UsernamePasswordCredentials(user, pwd);
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		return client;
	}

	/**
	 * getFhirData
	 * @param client
	 * @param url
	 * @return fhirRecord
	 * @throws Exception
	 */
	public String getFhirData(RestTemplate client, String url) throws Exception {
		String fhirRecord = client.getForObject(url, String.class);
		return fhirRecord;
	}
	
	/**
	 * insertIntoFhirRecord
	 * @param client
	 * @param url
	 * @param requestBody
	 * @return responseMap
	 */
	public Map<String, String> insertIntoFhirRecord(RestTemplate client, String url, String requestBody) {
		LOGGER.info("Inserting data into fhir server");
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
	
	/**
	 * updateFhirRecord
	 * @param client
	 * @param url
	 * @param requestBody
	 */
	public void updateFhirRecord(RestTemplate client, String url, String requestBody) {
		LOGGER.info("Updating record back to fhir server");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
		ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, request, String.class);
	}
	/**
	 * updateCommReqStatus
	 * @param status
	 * @param statusReason
	 * @param communicationRequestUrl
	 * @param communicationRequestId
	 * @param restTemplate
	 * @throws Exception
	 */
	public void updateCommReqStatus(CommunicationRequestStatus status, String statusReason,
			String communicationRequestUrl, String communicationRequestId, RestTemplate restTemplate) throws Exception {
		String commData = getFhirData(restTemplate, communicationRequestUrl + communicationRequestId);
		InputStream in = new ByteArrayInputStream(commData.getBytes());
		CommunicationRequest commRequest = FHIRParser.parser(Format.JSON).parse(in);
		CommunicationRequest commRequestFinal = null;
		Code code = null;
		Coding coding = null;
		CodeableConcept statReason = null;
		List<Coding> statusReasonList = null;
		if (statusReason != null) {

			code = Code.builder().value(statusReason).build();
			coding = Coding.builder()
					.system(Uri
							.of("http://ibm.com/fhir/cdm/CodeSystem/communication-status-reason-general-code-system"))
					.code(code).display(com.ibm.fhir.model.type.String.of(statusReason)).build();
			statReason = CodeableConcept.builder().coding(coding).build();
			if (commRequest.getStatusReason() != null) {
				statusReasonList = new ArrayList<Coding>(commRequest.getStatusReason().getCoding());
				statusReasonList.add(coding);
				commRequestFinal = commRequest.toBuilder()
						.statusReason(CodeableConcept.builder().coding(statusReasonList).build()).build();
			} else {
				commRequestFinal = commRequest.toBuilder().statusReason(statReason).build();
			}
		}
		if (status != null) {
			
			if ((statusReasonList != null && !statusReasonList.isEmpty()) || commRequestFinal != null) {
				commRequestFinal = commRequestFinal.toBuilder().status(status).build();
			}
			else {
				commRequestFinal = commRequest.toBuilder().status(status).build();
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FHIRGenerator.generator(Format.JSON, true).generate(commRequestFinal, baos);
		updateFhirRecord(restTemplate, communicationRequestUrl + "?_id=" + communicationRequestId, baos.toString());
	}
    
	/**
	 * getLatestCommunicationRecord
	 * @param commReqData
	 * @param client
	 * @return latestCommRecord
	 * @throws Exception
	 */
	public JsonNode getLatestCommunicationRecord(JsonNode commReqData, RestTemplate client) throws Exception {
		String careGapId = null;
		JsonNode commRequest = commReqData.get("extension");
		for (int i = 0; i < commRequest.size(); i++) {
			JsonNode commReqNode = commRequest.get(i);
			if (commReqNode.get("url").asText().endsWith("communication-care-gap")) {
				careGapId = commReqNode.get("valueString").asText();
				break;
			}
		}
		String patientId = commReqData.get("subject").get("reference").asText();
		String url = PropertyUtil.getProperty("fhir.patient.recentcommunication.endpoint");
		String updatedUrl = url.replaceAll("Patient/<patientID>", patientId).replaceAll("<care-gap-id>", careGapId);
		String latestCommunication = getFhirData(client, updatedUrl);
		ObjectMapper jsonParser = new ObjectMapper();
		JsonNode latestCommRecord = jsonParser.readValue(latestCommunication, JsonNode.class);
		return latestCommRecord;
	}

	/**
	 * 
	 * @param patientId
	 * @param patientPreference
	 * @param pastDate
	 * @param client
	 * @return pastCommRecord
	 * @throws Exception
	 */
	public JsonNode getPreviousCommunicationRecords(String patientId, String patientPreference, String pastDate,
			RestTemplate client) throws Exception {

		String url = PropertyUtil.getProperty("fhir.patient.pastcommunication.endpoint");
		String updatedUrl = url.replaceAll("<patientID>", patientId).replaceAll("<comm-mode>", patientPreference)
				.replaceAll("<pastDate>", pastDate);
		String pastCommunication = getFhirData(client, updatedUrl);
		ObjectMapper jsonParser = new ObjectMapper();
		JsonNode pastCommRecord = jsonParser.readValue(pastCommunication, JsonNode.class);
		return pastCommRecord;
	}
	
	
	/**
	 * 
	 * @param patientId
	 * @param patientPreference
	 * @param pastDate
	 * @param client
	 * @return pastCommRecord
	 * @throws Exception
	 */
	public JsonNode getPreviousAppointmentRecords(String patientId,String pastDate,
			RestTemplate client) throws Exception {

		String url = PropertyUtil.getProperty("fhir.patient.pastappointment.endpoint");
		String updatedUrl = url.replaceAll("<patientID>", patientId).replaceAll("<pastDate>", pastDate);
		String pastAppointment = getFhirData(client, updatedUrl);
		ObjectMapper jsonParser = new ObjectMapper();
		JsonNode pastAppointmentRecord = jsonParser.readValue(pastAppointment, JsonNode.class);
		return pastAppointmentRecord;
	}
	
	
	
	
	
	
}
