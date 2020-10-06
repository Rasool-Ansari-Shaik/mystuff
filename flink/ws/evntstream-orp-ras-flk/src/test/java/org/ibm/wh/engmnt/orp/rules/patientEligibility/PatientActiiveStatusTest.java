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

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientActiiveStatusTest {

	@Test
	public void checkForPatientActiveStatus() throws JsonMappingException, JsonProcessingException, IOException {

		String absolutePathForContract = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
				.getAbsolutePath();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContract).toFile(),
				JsonNode.class);
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "patient-consent.json").toFile()
				.getAbsolutePath();
		JsonNode patientJsonData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		PatientActiveStatus patientActive=new PatientActiveStatus();
		String patientActiveStatus = patientActive.activePatientCheck(contractConfigData, patientJsonData);
		Assert.assertEquals(patientActiveStatus, "");
	}

	@Test
	public void checkForPatientInactiveStatus() throws JsonMappingException, JsonProcessingException, IOException {

		String absolutePathForContract = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
				.getAbsolutePath();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContract).toFile(),
				JsonNode.class);
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "patient-inactive.json").toFile()
				.getAbsolutePath();
		JsonNode patientJsonData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		PatientActiveStatus patientActive=new PatientActiveStatus();
		String patientActiveStatus = patientActive.activePatientCheck(contractConfigData, patientJsonData);
		Assert.assertEquals(patientActiveStatus, "patient-inactive");
	}

}
