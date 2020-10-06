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

import org.ibm.wh.engmnt.orp.utility.DatabaseUtil;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PractitionerModality {
	private static ObjectMapper jsonParser;
	private final static Logger LOGGER = LoggerFactory.getLogger(PractitionerModality.class);

	/*
	 * Method checkForPractitionerModalities is to Check Enabled Modalities For
	 * Attributed Practitioner
	 * @commRequest is a Communication Request
	 */

	public String checkForPractitionerModalityCheck(String practitionerRecord, String preference) throws Exception {
		    jsonParser = new ObjectMapper();
		    String statusReason = "";
			JsonNode practitionerJson = jsonParser.readValue(practitionerRecord, JsonNode.class);
			String practitionerNPIId = null;
			
		
			for (int i = 0; i < practitionerJson.get("identifier").size(); i++) {
				JsonNode identifier = practitionerJson.get("identifier").get(i);
				for (int j = 0; j < identifier.get("type").get("coding").size(); j++) {
					if (identifier.get("type").get("coding").get(j).get("code").asText().equals("NPI")) {
						practitionerNPIId = practitionerJson.get("identifier").get(j).get("value").asText();
					}
				}
			}

			if (practitionerNPIId != null) {
				DatabaseUtil dataBaseUtil = new DatabaseUtil();
				String practitionerData = dataBaseUtil.getConfigData(PropertyUtil.getProperty("practitionerconfig.tablename"));

				JsonNode practitionerWithModalities = jsonParser.readValue(practitionerData, JsonNode.class);

				for (int i = 0; i < practitionerWithModalities.get("practitioners").size(); i++) {
					if (practitionerWithModalities.get("practitioners").get(i).get("id").asText()
							.equals(practitionerNPIId)) {
						JsonNode modalitiesPreference = practitionerWithModalities.get("practitioners").get(i)
								.get("modality").get("preference");
						if (!modalitiesPreference.get(preference).get("enabled").asBoolean()) {
							statusReason = "np-sender-" + preference + "-disabled";
							break;
						}
					}
				}
			}
			if (!statusReason.isEmpty()) {
				LOGGER.info("Status Reason For " + preference + " Modality for Attribute Practitioner ::" + statusReason);
			} else {
				LOGGER.info("" + preference + " Modality is enabled for Attribute Practitioner");
			}
		return statusReason;
	}
}
