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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientImportanceTest {

	private transient ObjectMapper objectMapper;
	JsonNode patientData;
	JsonNode contractConfigData;
	public String result = null;

	public JsonNode getPatientData(String patientFileName)
			throws JsonParseException, JsonMappingException, IOException {
		String absolutePathForPatientImportance = Paths.get("src", "test", "inputPayloads", patientFileName).toFile()
				.getAbsolutePath();
		objectMapper = new ObjectMapper();
		patientData = objectMapper.readValue(Paths.get(absolutePathForPatientImportance).toFile(), JsonNode.class);
		return patientData;
	}

	@Test
	public void checkForPatientImportanceTest() throws JsonParseException, JsonMappingException, IOException {

		for (int i = 1; i <= 5; i++) {
			patientData = getPatientData("patient-importance-" + i + ".json");
			String absolutePathForContractInfo = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
					.getAbsolutePath();
			contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContractInfo).toFile(),
					JsonNode.class);
			PatientImportance patientImportance = new PatientImportance();
			String statusReason = patientImportance.validateImportanceStatus(patientData, contractConfigData);
			assertNotNull(statusReason);
		}
	}
}
