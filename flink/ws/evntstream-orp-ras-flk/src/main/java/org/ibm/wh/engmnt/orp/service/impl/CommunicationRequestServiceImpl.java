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

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import org.ibm.wh.engmnt.orp.service.CommunicationRequestService;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.generator.FHIRGenerator;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import com.ibm.fhir.model.resource.CommunicationRequest;
import com.ibm.fhir.model.resource.CommunicationRequest.Payload;
import com.ibm.fhir.model.type.Attachment;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.CodeableConcept;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.DateTime;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.Element;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.Identifier;
import com.ibm.fhir.model.type.Meta;
import com.ibm.fhir.model.type.Period;
import com.ibm.fhir.model.type.Reference;
import com.ibm.fhir.model.type.Uri;
import com.ibm.fhir.model.type.Url;
import com.ibm.fhir.model.type.code.CommunicationPriority;
import com.ibm.fhir.model.type.code.CommunicationRequestStatus;

public class CommunicationRequestServiceImpl implements CommunicationRequestService {
	private final static Logger LOGGER = LoggerFactory.getLogger(ObservationServiceImpl.class);

	public String generateFhirCommunicationRequestModel(String contractConfigInfo, String tenantID,
			String practitionerId, String practitionerRoleId, String patientInfo, String practitionerInfo,
			String careGapName, String facilityAddress, String locationConfigInfo)
			throws JsonMappingException, JsonProcessingException, FHIRGeneratorException, ParseException {
		LOGGER.debug("generateFhirCommunicationRequestModel method started");
		ObjectMapper jsonParser = new ObjectMapper();
		//JsonNode contractConfigData = jsonParser.readValue(contractConfigInfo, JsonNode.class);
		//JsonNode practitionerData = jsonParser.readValue(practitionerInfo, JsonNode.class);
		JsonNode patientData = jsonParser.readValue(patientInfo, JsonNode.class);
		JsonNode locationData = jsonParser.readValue(locationConfigInfo, JsonNode.class);
		//String textPreference = null;
		//String emailPreference = null;
		CommunicationRequest commRequest = null;
	//	int telecomFlag = 0;
		/*
		 * 
		 * JsonNode patientTelecomNode = patientData.get("telecom"); if
		 * (patientTelecomNode != null) { for (int i = 0; i <
		 * patientData.get("telecom").size(); i++) {
		 * 
		 * JsonNode patientMissingNode = patientData.get("telecom").get(i).get("rank");
		 * 
		 * if (patientMissingNode == null) { continue; }
		 * 
		 * String patientRank = patientData.get("telecom").get(i).get("rank").asText();
		 * 
		 * String preference = patientData.get("telecom").get(i).get("system").asText();
		 * 
		 * if (patientRank != null && !patientRank.isEmpty() && patientRank.equals("1"))
		 * {
		 * 
		 * if (preference.equalsIgnoreCase("sms")) {
		 * 
		 * textPreference = "1";
		 * 
		 * }
		 * 
		 * else if (preference.equalsIgnoreCase("email")) { emailPreference = "1"; }
		 * 
		 * } else if (patientRank != null && !patientRank.isEmpty() &&
		 * patientRank.equals("2")) {
		 * 
		 * if (preference.equalsIgnoreCase("sms")) {
		 * 
		 * textPreference = "2";
		 * 
		 * }
		 * 
		 * else if (preference.equalsIgnoreCase("email")) { emailPreference = "2"; } }
		 * 
		 * }
		 * 
		 * } else { telecomFlag = 1; }
		 * 
		 * if (textPreference == null && emailPreference == null) {
		 * 
		 * String contractConfigTextRank =
		 * contractConfigData.get("offerings").get("outreach").get("modality")
		 * .get("text").get("rank").asText();
		 * 
		 * String contractConfigEmailRank =
		 * contractConfigData.get("offerings").get("outreach").get("modality")
		 * .get("email").get("rank").asText();
		 * 
		 * if (contractConfigTextRank != null &&
		 * contractConfigTextRank.equalsIgnoreCase("1")) { textPreference = "1"; } else
		 * if (contractConfigEmailRank != null &&
		 * contractConfigEmailRank.equalsIgnoreCase("1")) { emailPreference = "1"; }
		 * else if (contractConfigTextRank != null &&
		 * contractConfigTextRank.equalsIgnoreCase("2")) { textPreference = "2"; } else
		 * if (contractConfigEmailRank != null &&
		 * contractConfigEmailRank.equalsIgnoreCase("2")) { emailPreference = "2"; }
		 * 
		 * }
		 */

		String patientId = patientData.get("id").asText();
		String preference = "none";
		CodeableConcept statusReason = null;
		CommunicationRequestStatus status = null;
		Extension communicationContact = null;
		Extension tenantId = null;
		// Extension communicationMode = null;
		// Extension communicationVendor = null;
		// Extension payloadData = null;
		// String patientPreference = null;
		// String patientPhone = null;
		// String patientEmail = null;
		// int flag = 1;
		tenantId = Extension.builder().url("http://ibm.com/fhir/cdm/StructureDefinition/tenant-id")
				.value(com.ibm.fhir.model.type.String.of(tenantID)).build();

		String updatedSender = "example-contact";
		communicationContact = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-contact")
				.value(com.ibm.fhir.model.type.String.of(updatedSender)).build();
		status = CommunicationRequestStatus.DRAFT;
		/*
		 * LOGGER.info("Checking for Patient Preference"); if ((textPreference != null
		 * && textPreference.equals("1")) && textPreferenceFlag && telecomFlag == 0) {
		 * LOGGER.info("Checking for Modality Text Preference for Rank 1 ");
		 * 
		 * for (int i = 0; i < patientData.get("telecom").size(); i++) {
		 * patientPreference = patientData.get("telecom").get(i).get("system").asText();
		 * if (patientPreference.equalsIgnoreCase("Text") ||
		 * patientPreference.equalsIgnoreCase("sms") ||
		 * patientPreference.equalsIgnoreCase("phone")) { patientPhone =
		 * patientData.get("telecom").get(i).get("value").asText(); if (patientPhone !=
		 * null && !patientPhone.isEmpty()) { preference = "Text"; } else { flag = 0; }
		 * break; } } } else if ((emailPreference != null &&
		 * emailPreference.equals("1")) && emailPreferenceFlag && telecomFlag == 0) {
		 * LOGGER.info("Checking for Modality Email Preference for Rank 1 "); for (int i
		 * = 0; i < patientData.get("telecom").size(); i++) { patientPreference =
		 * patientData.get("telecom").get(i).get("system").asText(); if
		 * (patientPreference.equalsIgnoreCase("Email")) { patientEmail =
		 * patientData.get("telecom").get(i).get("value").asText(); if (patientEmail !=
		 * null && !patientEmail.isEmpty()) { preference = "Email"; } else { flag = 0; }
		 * break; } } } else if ((textPreference != null && textPreference.equals("2"))
		 * && textPreferenceFlag && telecomFlag == 0) {
		 * LOGGER.info("Checking for Modality Text Preference for Rank 2 "); for (int i
		 * = 0; i < patientData.get("telecom").size(); i++) { patientPreference =
		 * patientData.get("telecom").get(i).get("system").asText(); if
		 * (patientPreference.equalsIgnoreCase("Text") ||
		 * patientPreference.equalsIgnoreCase("sms")) { patientPhone =
		 * patientData.get("telecom").get(i).get("value").asText(); if (patientPhone !=
		 * null && !patientPhone.isEmpty()) { preference = "Text"; } else { flag = 0; }
		 * break; } } } else if ((emailPreference != null &&
		 * emailPreference.equals("2")) && emailPreferenceFlag && telecomFlag == 0) {
		 * LOGGER.info("Checking for Modality Email Preference for Rank 2 "); for (int i
		 * = 0; i < patientData.get("telecom").size(); i++) { patientPreference =
		 * patientData.get("telecom").get(i).get("system").asText(); if
		 * (patientPreference.equalsIgnoreCase("Email")) { patientEmail =
		 * patientData.get("telecom").get(i).get("value").asText(); if (patientEmail !=
		 * null && !patientEmail.isEmpty()) { preference = "Email"; } else { flag = 0; }
		 * break; } } } else { flag = 0; } if (flag == 0) { LOGGER.
		 * info("Patient has no communication preference, so revoking CommRequest status"
		 * ); status = CommunicationRequestStatus.REVOKED; Code code =
		 * Code.builder().value(PropertyUtil.getProperty("commrequest.noconsent.value"))
		 * .build(); Coding coding = Coding.builder() .system(Uri .of(
		 * "http://ibm.com/fhir/cdm/CodeSystem/communication-status-reason-general-code-system"
		 * )) .code(code)
		 * .display(com.ibm.fhir.model.type.String.of(PropertyUtil.getProperty(
		 * "commrequest.noconsent.value"))) .build(); statusReason =
		 * CodeableConcept.builder().coding(coding).build();
		 * 
		 * } else { status = CommunicationRequestStatus.DRAFT; if
		 * (preference.equalsIgnoreCase("text") || preference.equalsIgnoreCase("sms")) {
		 * 
		 * String firstName =
		 * patientData.get("name").get(0).get("given").get(0).asText(); String
		 * providerName = practitionerData.get("name").get(0).get("text").asText();
		 * String providerContactNumber =
		 * practitionerData.get("telecom").get(0).get("value").asText();
		 * 
		 * String textSender =
		 * contractConfigData.get("offerings").get("outreach").get("modality").get(
		 * "text") .get("sender").asText();
		 * 
		 * Code commMode = Code.builder().value("text").build(); Coding commModeCoding =
		 * Coding.builder() .system(Uri.of(
		 * "http://ibm.com/fhir/cdm/CodeSystem/eng-communication-mode-code-system"))
		 * .code(commMode).build(); communicationMode = Extension.builder()
		 * .url("http://ibm.com/fhir/cdm/StructureDefinition/communication-mode").value(
		 * commModeCoding) .build();
		 * 
		 * String vendor = "Genesis"; String carrier = "99901";
		 * 
		 * String vendor =
		 * contractConfigData.get("offerings").get("outreach").get("modality").get(
		 * "text") .get("vendors").get(0).get("name").asText(); String carrier =
		 * contractConfigData.get("offerings").get("outreach").get("modality").get(
		 * "text") .get("carrier").asText();
		 * 
		 * Reference vendorReference = Reference.builder()
		 * .reference(com.ibm.fhir.model.type.String.of("Organization/" +
		 * vendor)).build(); communicationVendor = Extension.builder()
		 * .url("http://ibm.com/fhir/cdm/StructureDefinition/communication-vendor").
		 * value(vendorReference) .build();
		 * 
		 * String payload =
		 * "{\"sender\":\"18773558433\",\"phone_number\":\"18602519382\",\"carrier\":\"99901\",\"patientName\":\"Rob\",\"providerName\":\"James\",\"facilityName\":\"Mediciti\",\"facilityPhoneNo\":\"888-888-8888\",\"careGapName\":\"COVID-19 routine checkup\",\"facilityAddress\":\"Dallas\"}"
		 * ;
		 * 
		 * String phoneNumber = null; for (int i = 0; i <
		 * patientData.get("telecom").size(); i++) { patientPreference =
		 * patientData.get("telecom").get(i).get("system").asText(); if
		 * (patientPreference.equalsIgnoreCase("sms")) { phoneNumber =
		 * patientData.get("telecom").get(i).get("value").asText(); } }
		 * 
		 * LinkedHashMap<String, Object> map = jsonParser.readValue(payload,
		 * LinkedHashMap.class); map.put("phone_number", phoneNumber);
		 * map.put("carrier", carrier); map.put("patientName", firstName);
		 * map.put("providerName", providerName); map.put("facilityPhoneNo",
		 * providerContactNumber); map.put("careGapName", careGapName);
		 * map.put("facilityAddress", facilityAddress); map.put("sender", textSender);
		 * String updated = jsonParser.writeValueAsString(map).toString();
		 * 
		 * payloadData = Extension.builder() .url(
		 * "http://ibm.com/fhir/cdm/StructureDefinition/communication-payload-data")
		 * .value(com.ibm.fhir.model.type.String.of(updated)).build();
		 * 
		 * }
		 * 
		 * else if (preference.equalsIgnoreCase("Email")) {
		 * 
		 * startTime =
		 * locationData.get("locations").get(0).get("modality").get("preference").get(
		 * "email") .get("window").get("startTime").asText(); endTime =
		 * locationData.get("locations").get(0).get("modality").get("preference").get(
		 * "email") .get("window").get("endTime").asText();
		 * 
		 * for (int i = 0; i < patientData.get("telecom").size(); i++) {
		 * patientPreference = patientData.get("telecom").get(i).get("system").asText();
		 * if (patientPreference.equalsIgnoreCase("Email")) { patientEmail =
		 * patientData.get("telecom").get(i).get("value").asText(); } }
		 * 
		 * String firstName =
		 * patientData.get("name").get(0).get("given").get(0).asText(); String
		 * providerName = practitionerData.get("name").get(0).get("text").asText();
		 * String providerContactNumber =
		 * practitionerData.get("telecom").get(0).get("value").asText();
		 * 
		 * Code commMode = Code.builder().value("email").build(); Coding commModeCoding
		 * = Coding.builder() .system(Uri.of(
		 * "http://ibm.com/fhir/cdm/CodeSystem/eng-communication-mode-code-system"))
		 * .code(commMode).build(); communicationMode = Extension.builder()
		 * .url("http://ibm.com/fhir/cdm/StructureDefinition/communication-mode").value(
		 * commModeCoding) .build();
		 * 
		 * String vendor =
		 * contractConfigData.get("offerings").get("outreach").get("modality").get(
		 * "email") .get("vendors").get(0).get("name").asText();
		 * 
		 * String vendor = "Jango"; Reference vendorReference = Reference.builder()
		 * .reference(com.ibm.fhir.model.type.String.of("Organization/" +
		 * vendor)).build();
		 * 
		 * communicationVendor = Extension.builder()
		 * .url("http://ibm.com/fhir/cdm/StructureDefinition/communication-vendor").
		 * value(vendorReference) .build();
		 * 
		 * String payload =
		 * "{\"toEmailAddress\":\"iftaqm55@in.ibm.com\",\"fromEmail\":\"noreply@phytel.com\",\"fromName\":\"IBM-WatsonHealth\",\"subject\":\"Health Reminder \",\"patientName\":\"Rob\",\"providerName\":\"James\",\"facilityName\":\"Mediciti\",\"facilityPhoneNo\":\"888-888-8888\",\"careGapName\":\"COVID-19 routine checkup\",\"facilityAddress\":\"Dallas\"}"
		 * ;
		 * 
		 * LinkedHashMap<String, Object> map = jsonParser.readValue(payload,
		 * LinkedHashMap.class); map.put("toEmailAddress", patientEmail);
		 * map.put("patientName", firstName); map.put("providerName", providerName);
		 * map.put("facilityPhoneNo", providerContactNumber); map.put("careGapName",
		 * careGapName); map.put("facilityAddress", facilityAddress);
		 * 
		 * String updated = jsonParser.writeValueAsString(map).toString();
		 * 
		 * payloadData = Extension.builder() .url(
		 * "http://ibm.com/fhir/cdm/StructureDefinition/communication-payload-data")
		 * .value(com.ibm.fhir.model.type.String.of(updated)).build();
		 * 
		 * } }
		 */

		Identifier identifier = Identifier.builder().value(com.ibm.fhir.model.type.String.of("ABC123"))
				.system(Uri.builder().value("http://www.jurisdiction.com/insurer/123456").build()).build();
		Reference basedOn = Reference.builder().display(com.ibm.fhir.model.type.String.of("EligibilityRequest"))
				.build();
		Reference replaces = Reference.builder()
				.display(com.ibm.fhir.model.type.String.of("prior CommunicationRequest")).build();
		Code code = Code.builder().value("SolicitedAttachmentRequest").build();
		Coding coding = Coding.builder().system(Uri.of("http://acme.org/messagetypes")).code(code).build();
		CodeableConcept category = CodeableConcept.builder().coding(coding).build();
		Reference subject = Reference.builder().reference(com.ibm.fhir.model.type.String.of("Patient/" + patientId))
				.build();
		DateTime authoredOn = DateTime.builder().value("2020-06-14").build();
		Reference requester = Reference.builder()
				.reference(com.ibm.fhir.model.type.String.of("Practitioner/" + practitionerId)).build();
		Reference recipient = Reference.builder().reference(com.ibm.fhir.model.type.String.of("Patient/" + patientId))
				.build();
		Reference sender = Reference.builder()
				.reference(com.ibm.fhir.model.type.String.of("PractitionerRole/" + practitionerRoleId)).build();
		Code reasoncode = Code.builder().value("260385009").build();
		Coding reasonCoding = Coding.builder().system(Uri.of("http://snomed.info/sct")).code(reasoncode).build();
		CodeableConcept reasonCode = CodeableConcept.builder().coding(reasonCoding).build();
		Reference reference = Reference.builder().reference(com.ibm.fhir.model.type.String.of("Observation/example"))
				.build();
		CodeableConcept medium = CodeableConcept.builder().text(com.ibm.fhir.model.type.String.of(preference)).build();
		Extension communicationPrioritySequence = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-priority-sequence").value(Decimal.of(1))
				.build();

		Code commProduct = Code.builder().value("ihe").build();
		Coding commCoding = Coding.builder()
				.system(Uri.of("http://ibm.com/fhir/cdm/CodeSystem/eng-product-code-system")).code(commProduct).build();
		Extension communicationProduct = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-product").value(commCoding).build();
		Extension communicationCareGap = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-care-gap")
				.value(com.ibm.fhir.model.type.String.of(careGapName)).build();

		Extension communicationTemplate = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-template")
				.value(com.ibm.fhir.model.type.String.of("CARE101")).build();
		Attachment attachment = Attachment.builder().url(Url.of("http://mediafile.com/dummy-Url")).build();
		Extension communicationMedia = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-media").value(attachment).build();
		Extension communicationRetryLimit = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-retry-limt")
				.value(com.ibm.fhir.model.type.String.of("5")).build();

		Reference behalfOfReference = Reference.builder()
				.reference(com.ibm.fhir.model.type.String.of("PractitionerRole/" + practitionerRoleId)).build();
		Extension communicationonBehalfOf = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-on-behalf-of").value(behalfOfReference)
				.build();

		Reference behalfOfMeasure = Reference.builder().reference(com.ibm.fhir.model.type.String.of("Measure/example"))
				.build();
		Extension communicationMeasure = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-measure").value(behalfOfMeasure)
				.build();

		Meta meta = Meta.builder().extension(tenantId).build();
		commRequest = CommunicationRequest.builder().id("fm-solicit").identifier(identifier).meta(meta).basedOn(basedOn)
				.replaces(replaces).status(status).statusReason(statusReason).category(category)
				.priority(CommunicationPriority.ROUTINE).subject(subject).authoredOn(authoredOn).requester(requester)
				.recipient(recipient).sender(sender).reasonCode(reasonCode).medium(medium)
				.payload(Payload.builder().content(com.ibm.fhir.model.type.String.string("Test")).build())
				.extension(communicationPrioritySequence).extension(communicationContact)
				.extension(communicationProduct).extension(communicationMedia).extension(communicationCareGap)
				.extension(communicationTemplate).extension(communicationRetryLimit).extension(communicationonBehalfOf)
				.reasonReference(reference).extension(communicationMeasure).build();

		/*
		 * if (telecomFlag == 1) { commRequest =
		 * CommunicationRequest.builder().id("fm-solicit").identifier(identifier).meta(
		 * meta)
		 * .basedOn(basedOn).replaces(replaces).status(status).statusReason(statusReason
		 * ).category(category)
		 * .priority(CommunicationPriority.ROUTINE).subject(subject).authoredOn(
		 * authoredOn)
		 * .requester(requester).recipient(recipient).sender(sender).reasonCode(
		 * reasonCode).medium(medium)
		 * .payload(Payload.builder().content(com.ibm.fhir.model.type.String.string(
		 * "Test")).build())
		 * .extension(communicationPrioritySequence).extension(communicationContact)
		 * .extension(communicationProduct).extension(communicationMedia).extension(
		 * communicationCareGap)
		 * .extension(communicationTemplate).extension(communicationRetryLimit)
		 * .extension(communicationonBehalfOf).occurrence(occurencePeriod).
		 * reasonReference(reference) .extension(communicationMeasure).build(); } else {
		 * commRequest =
		 * CommunicationRequest.builder().id("fm-solicit").identifier(identifier).meta(
		 * meta)
		 * .basedOn(basedOn).replaces(replaces).status(status).statusReason(statusReason
		 * ).category(category)
		 * .priority(CommunicationPriority.ROUTINE).subject(subject).authoredOn(
		 * authoredOn)
		 * .requester(requester).recipient(recipient).sender(sender).reasonCode(
		 * reasonCode).medium(medium)
		 * .payload(Payload.builder().content(com.ibm.fhir.model.type.String.string(
		 * "Test")).build())
		 * .extension(communicationPrioritySequence).extension(communicationContact)
		 * .extension(communicationProduct).extension(communicationMode).extension(
		 * communicationMedia)
		 * .extension(communicationCareGap).extension(communicationTemplate).extension(
		 * payloadData)
		 * .extension(communicationRetryLimit).extension(communicationVendor)
		 * .extension(communicationonBehalfOf).occurrence(occurencePeriod).
		 * reasonReference(reference) .extension(communicationMeasure).build(); }
		 */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FHIRGenerator.generator(Format.JSON, true).generate(commRequest, baos);
		LOGGER.debug("generateFhirCommunicationRequestModel method ended");
		return baos.toString();
	}
}
