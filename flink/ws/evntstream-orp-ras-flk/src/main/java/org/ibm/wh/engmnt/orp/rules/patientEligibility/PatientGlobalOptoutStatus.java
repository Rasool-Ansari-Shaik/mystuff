/*******************************************************************************
 *
 *   Watson Health Imaging Analytics
 *  
 *   IBM Confidential
 *  
 *   OCO Source Materials
 *  
 *   (C) Copyright IBM Corp. 2020
 *  
 *    The source code for this program is not published or otherwise
 *    divested of its trade secrets, irrespective of what has been
 *    deposited with the U.S. Copyright Office.
 *  
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.rules.patientEligibility;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class PatientGlobalOptoutStatus {

	private final static Logger LOGGER = LoggerFactory.getLogger(PatientGlobalOptoutStatus.class);

	/*
	 * checking for the global optout status for patient.
	 * 
	 * @CommRequest @PatientData @ContractConfig
	 */

	public String patientGlobalOptoutStatusCheck(JsonNode contractConfigInfo, JsonNode commRequest,
			JsonNode patientData) throws JsonProcessingException, ParseException {
		LOGGER.debug("check for patient global optout method started ");
		String statusReason = "";
		String commProductCode = null;

		// For loop for reading communication-product from communicationRequest
		for (int i = 0; i < commRequest.get("extension").size(); i++) {
			JsonNode commProduct = commRequest.get("extension").get(i);
			if (commProduct.get("url").asText().endsWith("communication-product")) {
				commProductCode = commProduct.get("valueCoding").get("code").asText();
				LOGGER.info("Communication product for communication request is " + commProductCode);
				break;
			}
		}

		LOGGER.debug("Verifying global optout status for product " + commProductCode);
		boolean allowGlobalOptoutStatus = contractConfigInfo.get("offerings").get(commProductCode).get("patient")
				.get("allowGlobalOptOut").asBoolean();
		LOGGER.info("Allow global optout for patient is: " + allowGlobalOptoutStatus);
		if (!allowGlobalOptoutStatus) {

			String patientCommProduct = null;
			String effectivePeriodStart = null;
			String effectivePeriodEnd = null;
			Boolean targetInZone = false;

			JsonNode patientExtension = patientData.get("extension");
			if (patientExtension != null) {
				// start of For loop for reading comm-product and effective period from patient
				for (int i = 0; i < patientExtension.size(); i++) {
					JsonNode commProduct = patientExtension.get(i);
					if (commProduct.get("url").asText().endsWith("communication-opt-out")) {
						LOGGER.debug("reading communication product and effective period from patient");
						JsonNode patientCommProductNode = commProduct.get("extension");
						int commProductIndex = isExtensionExists(patientCommProductNode, "communication-product");
						int effectivePeriodIndex = isExtensionExists(patientCommProductNode, "effective-period");
						if (commProductIndex != -1 && effectivePeriodIndex != -1) {
							patientCommProduct = patientCommProductNode.get(commProductIndex)
									.get("valueCodeableConcept").get("text").asText();

							LOGGER.info("Patient optout communication product  is " + patientCommProduct);

							JsonNode startDateNode = patientCommProductNode.get(effectivePeriodIndex).get("valuePeriod")
									.get("start");

							JsonNode endDateNode = patientCommProductNode.get(effectivePeriodIndex).get("valuePeriod")
									.get("end");
							LOGGER.info("Effective period Start Date is " + startDateNode);
							LOGGER.info("Effective period End Date is " + endDateNode);

							if (startDateNode != null) {
								effectivePeriodStart = patientCommProductNode.get(effectivePeriodIndex)
										.get("valuePeriod").get("start").asText();

							}
							if (endDateNode != null) {
								effectivePeriodEnd = patientCommProductNode.get(effectivePeriodIndex).get("valuePeriod")
										.get("end").asText();

							}

							ZonedDateTime target = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

							if (effectivePeriodStart != null && effectivePeriodEnd != null) {

								targetInZone = (target.isAfter(
										ZonedDateTime.ofInstant(Instant.parse(effectivePeriodStart), ZoneOffset.UTC))
										&& target.isBefore(ZonedDateTime.ofInstant(Instant.parse(effectivePeriodEnd),
												ZoneOffset.UTC)));
							} else if (effectivePeriodStart != null) {
								targetInZone = target.isAfter(
										ZonedDateTime.ofInstant(Instant.parse(effectivePeriodStart), ZoneOffset.UTC));
							}

							LOGGER.info("Current date is in effective timezone " + targetInZone);
							if (targetInZone) {
								if (commProductCode.equalsIgnoreCase(patientCommProduct)) {
									LOGGER.info("Communication product (" + commProductCode + ") is matched");
									// Updating Status reason
									statusReason = PropertyUtil
											.getProperty("commrequest.patientglobaloptoutstatus.value");
								} else {
									LOGGER.info("Communication product (" + commProductCode + ") is not matched");
								}
							}
						}
					}
				}
			}
		}
		LOGGER.debug("check for patient global optout method ended");
		LOGGER.info("Status Reason is " + statusReason);
		return statusReason;

	}

	public static int isExtensionExists(JsonNode data, String endsWithUrl) {

		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				JsonNode extensionNode = data.get(i);
				if (extensionNode.get("url").asText().endsWith(endsWithUrl)) {
					return i;
				}
			}
		}
		return -1;
	}

}
