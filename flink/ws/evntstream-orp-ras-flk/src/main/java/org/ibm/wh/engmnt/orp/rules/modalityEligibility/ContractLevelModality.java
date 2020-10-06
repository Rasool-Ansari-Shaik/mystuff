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

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/* Method ContractLevelModalityCheck is to Check Enabled Modalities For Contract Level */

public class ContractLevelModality {
	private final static Logger LOGGER = LoggerFactory.getLogger(ContractLevelModality.class);

	public String checkContractLevelModalityCheck(String preference, JsonNode contractConfigJson) throws SQLException {
		String statusReason = "";
		JsonNode modalities = contractConfigJson.get("modality").get("preference");
		
		
		

		if (!modalities.get(preference).get("enabled").asBoolean()) {
			statusReason = "np-tenant-" + preference + "-disabled";
		}

		if (!statusReason.isEmpty()) {
			LOGGER.info("Status Reason For " + preference + " Modality at the Contract Level ::" + statusReason);
		} else {
			LOGGER.info("" + preference + " Modality is enabled at the Contract Level");
		}

		return statusReason;
	}

}
