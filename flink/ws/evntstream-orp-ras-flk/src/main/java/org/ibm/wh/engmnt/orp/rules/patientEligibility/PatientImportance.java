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

import java.util.ArrayList;
import java.util.List;

import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class PatientImportance {

	private final static Logger LOGGER = LoggerFactory.getLogger(PatientImportance.class);
	public String statusReason = "";

	/**
	 * Method to read importanceStatus node from Contract Config
	 * 
	 * @param contractConfigData
	 * @return listOfCodes
	 */
	public List<String> getAllowedImportantStatus(JsonNode contractConfigData) {
		List<String> listOfCodes = new ArrayList<>();
		String code = "";
		String productName=PropertyUtil.getProperty("commrequest.commproduct.value");
		JsonNode importanceStatus = contractConfigData.get("offerings").get(productName).get("patient")
				.get("importanceStatus");

		boolean allowVip = importanceStatus.get(PropertyUtil.getProperty("importancestatus.allowvip")).asBoolean();
		LOGGER.info("allow vip status in Contract Config: " + allowVip);
		if (!allowVip) {
			code = PropertyUtil.getProperty("patient.importance.vip");
			listOfCodes.add(code);
		}
		boolean allowBadDebt = importanceStatus.get(PropertyUtil.getProperty("importancestatus.allowBaddebt"))
				.asBoolean();
		LOGGER.info("allow bad-debt status in Contract Config: " + allowBadDebt);
		if (!allowBadDebt) {
			code = PropertyUtil.getProperty("patient.importance.baddebt");
			listOfCodes.add(code);
		}
		boolean allowDoNotContact = importanceStatus.get(PropertyUtil.getProperty("importancestatus.allowdonotcontact"))
				.asBoolean();
		LOGGER.info("allow donotcontact status in Contract Config: " + allowDoNotContact);
		if (!allowDoNotContact) {
			code = PropertyUtil.getProperty("patient.importance.donotcontact");
			listOfCodes.add(code);
		}
		boolean allowNoCommPrivacy = importanceStatus.get(PropertyUtil.getProperty("importancestatus.allownocommprivacy")).asBoolean();
		LOGGER.info("allow NoCommPrivacy status in Contract Config: " + allowNoCommPrivacy);
		if (!allowNoCommPrivacy) {
			code = PropertyUtil.getProperty("patient.importance.nocommprivacy");
			listOfCodes.add(code);
		}
		boolean allowNoCommHealthReminder = importanceStatus
				.get(PropertyUtil.getProperty("importancestatus.allownocommhealthreminder")).asBoolean();
		LOGGER.info("allow NoCommHealthReminder status in Contract Config: " + allowNoCommHealthReminder);
		if (!allowNoCommHealthReminder) {
			code = PropertyUtil.getProperty("patient.importance.nocommhealthreminder");
			listOfCodes.add(code);
		}
		LOGGER.info("Allowed importance status codes : " + listOfCodes);

		return listOfCodes;
	}

	/**
	 * Method to validate Patient Importance Status
	 * 
	 * @param patientData
	 * @param contractConfigData is Contract Config data
	 * @return statusReason
	 */
	public String validateImportanceStatus(JsonNode patientData, JsonNode contractConfigData) {
		List<String> listOfCodes = getAllowedImportantStatus(contractConfigData);
		String code = "";
		JsonNode patientExtensions = patientData.get("extension");
		if (patientExtensions != null) {
			for (int i = 0; i < patientExtensions.size(); i++) {
				String statusUrl = patientExtensions.get(i).get("url").asText();
				if (statusUrl != null && statusUrl.contains("patient-importance")) {
					code = patientExtensions.get(i).get("valueCodeableConcept").get("coding").get(0).get("code")
							.asText();
					if (listOfCodes.stream().anyMatch(code::equalsIgnoreCase)) {
						break;
					}
				}
			}
		}
		if(code.isEmpty()) {
			LOGGER.info("Patient doesn't have Importance Status Code");
		} else {
			statusReason = PropertyUtil.getProperty("commrequest.importance."+code.toLowerCase()+".statusreason");
			LOGGER.info("Patient has Importance Status Code: " + code + " and statusReason: "+statusReason);
		}
		return statusReason;
	}
}