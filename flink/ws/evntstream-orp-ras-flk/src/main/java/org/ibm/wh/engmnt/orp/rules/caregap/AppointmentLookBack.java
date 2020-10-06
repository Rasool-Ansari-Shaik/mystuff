package org.ibm.wh.engmnt.orp.rules.caregap;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.ibm.wh.engmnt.orp.ops.FhirServerOps;
import org.ibm.wh.engmnt.orp.utility.CommonUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppointmentLookBack {

	private static ObjectMapper jsonParser;
	private RestTemplate restTemplate;
	private FhirServerOps fhirServerOps;

	private final static Logger LOGGER = LoggerFactory.getLogger(AppointmentLookBack.class);

	/**
	 * 
	 * @param commRequestId
	 * @param patientId
	 * @param locationInfo
	 * @param practitionerInfo
	 * @param careGapConfigInfo
	 * @param practitionerConfigInfo
	 * @param locationConfigInfo
	 * @return
	 * @throws Exception
	 */
	public boolean checkForPastAppointments(String commRequestId, String patientId, String locationInfo,
			String practitionerInfo, String careGapConfigInfo, String practitionerConfigInfo, String locationConfigInfo)
			throws Exception {
		jsonParser = new ObjectMapper();
		fhirServerOps = new FhirServerOps();
		restTemplate = new RestTemplate(fhirServerOps.getClientHttpRequestFactory());
		String communicationRequestUrl = PropertyUtil.getProperty("fhir.communicationrequest.endpoint");
		// Getting commRequest from FHIR
		String commReqData = fhirServerOps.getFhirData(restTemplate, communicationRequestUrl + commRequestId);

		JsonNode commReqNode = jsonParser.readValue(commReqData, JsonNode.class);

		// Getting careGapID from commRequest
		JsonNode commRecord = commReqNode.get("extension");

		List<JsonNode> commRecordList = Arrays.asList(jsonParser.readValue(commRecord.toString(), JsonNode[].class));

		LOGGER.info("Reading careGapId from communication Request");
		String careGapId = commRecordList.stream()
				.filter(ele -> ele.get("url").asText().endsWith("communication-care-gap"))
				.map(e -> e.get("valueString").asText()).reduce("", String::concat);

		// Getting Appointments data for the Patient with the lookback period

		LOGGER.info("CareGap Id from CommunicationRequest :" + careGapId);

		JsonNode careGapConfig = jsonParser.readValue(careGapConfigInfo, JsonNode.class);
		List<JsonNode> careGapConfigList = Arrays
				.asList(jsonParser.readValue(careGapConfig.get("caregaps").toString(), JsonNode[].class));

		List<JsonNode> careGapList = careGapConfigList.stream().filter(
				ele -> ele.get("Identifier") != null && ele.get("Identifier").asText().equalsIgnoreCase(careGapId))
				.collect(Collectors.toList());

		if (!careGapList.isEmpty()) {

			LOGGER.info("CareGap Id :" + careGapId + " exists in the careGap config data");
			JsonNode careGapData = careGapList.get(0);

			int noOfDays = careGapData.get("appointmentPeriodInDays").get("past").asInt();

			ZonedDateTime currentDateInUTC = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

			ZonedDateTime pastDate = currentDateInUTC.minusDays(noOfDays);

			LOGGER.info("pastDate : " + pastDate.toLocalDate());

			// Get patient's previous Appointments from FHIR

			LOGGER.info("Retrieving Patient Past Appointments data");
			JsonNode appointmentsData = fhirServerOps.getPreviousAppointmentRecords(patientId,
					pastDate.toLocalDate().toString(), restTemplate);

			int total = appointmentsData.get("total").asInt();

			LOGGER.info("Total number of past appointments within lookback period are :" + total);

			if (total > 0) {

				LOGGER.info("Checking for Enabled appointment complaince");
				List<JsonNode> appointmentComplainceList = Arrays.asList(
						jsonParser.readValue(careGapData.get("appointmentCompliance").toString(), JsonNode[].class));

				// Getting the enabled complaince

				String complainceOption = appointmentComplainceList.stream()
						.filter(ele -> ele.get("enabled") != null && ele.get("enabled").asBoolean())
						.map(e -> e.get("name").asText()).reduce("", String::concat);

				LOGGER.info("Appointment complaince value :" + complainceOption);

				JsonNode practitionerRecord = jsonParser.readValue(practitionerInfo, JsonNode.class);
				JsonNode locationRecord = jsonParser.readValue(locationInfo, JsonNode.class);

				List<JsonNode> practitionerList = Arrays.asList(
						jsonParser.readValue(practitionerRecord.get("identifier").toString(), JsonNode[].class));

				// Getting the PractitionerMpiID from Practitioner
				String practitionerNpiId = practitionerList.stream()
						.filter(ele -> ele.get("type").get("coding").findValue("code") != null
								&& ele.get("type").get("coding").findValue("code").asText().equalsIgnoreCase("NPI"))
						.map(e -> e.get("value").asText()).reduce("", String::concat);

				List<JsonNode> locationList = Arrays
						.asList(jsonParser.readValue(locationRecord.get("identifier").toString(), JsonNode[].class));

				// Getting the TIN ID from Location
				String locationTinId = locationList.stream()
						.filter(ele -> ele.get("type").get("coding").findValue("code") != null
								&& ele.get("type").get("coding").findValue("code").asText().equalsIgnoreCase("TAX"))
						.map(e -> e.get("value").asText()).reduce("", String::concat);

				CommonUtil commonUtil = new CommonUtil();

				/* Checking whether PractitionerNPI is enabled */
				boolean practitionerEnabledFlag = commonUtil.isPractitionerEnabled(practitionerNpiId,
						practitionerConfigInfo);
				/* Checking whether locationTIN is enabled */
				boolean locationEnabledFlag = commonUtil.isLocationEnabled(locationTinId, locationConfigInfo);

				LOGGER.info("isPractitionerEnabled :" + practitionerEnabledFlag);
				LOGGER.info("isLocationEnabled :" + locationEnabledFlag);

				if (complainceOption.equalsIgnoreCase("option0")) {

					if (practitionerEnabledFlag && locationEnabledFlag) {

						return true;
					}

				}

				else if (complainceOption.equalsIgnoreCase("option1")) {
					if (practitionerEnabledFlag) {
						return true;

					}

				}

				else if (complainceOption.equalsIgnoreCase("option2")) {

					if (locationEnabledFlag) {
						return true;
					}

				}

				else if (complainceOption.equalsIgnoreCase("option3")) {

					return true;

				}

			}

		}

		return false;
	}

}
