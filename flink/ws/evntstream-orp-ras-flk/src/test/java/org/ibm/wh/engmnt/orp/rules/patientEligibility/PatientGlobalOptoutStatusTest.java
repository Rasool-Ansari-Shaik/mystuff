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
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientGlobalOptoutStatusTest {
	
	
	/*
	 * Junit testing for the global optout status for patient.
	 * 
	 * @CommRequest @PatientData @ContractConfig
	 */

	@Test
	public void checkForPatientGlobalOptoutSuccess() throws ParseException, IOException {

		String absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
				.getAbsolutePath();
		String absolutePathForCommRequest = Paths.get("src", "test", "inputPayloads", "CommunicationRequest.json")
				.toFile().getAbsolutePath();
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "cdm-patient.json").toFile()
				.getAbsolutePath();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
				JsonNode.class);
		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommRequest).toFile(),
				JsonNode.class);
		JsonNode patientData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		PatientGlobalOptoutStatus patientGlobalOptoutStatus=new PatientGlobalOptoutStatus();
		String result = patientGlobalOptoutStatus.patientGlobalOptoutStatusCheck(contractConfigData, communicationData,
				patientData);

		Assert.assertEquals("opt-out-product", result);

	}

	@Test
	public void checkForPatientGlobalOptoutFailure()
			throws JsonMappingException, JsonProcessingException, ParseException, IOException {

		String absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads", "contract-failure.json")
				.toFile().getAbsolutePath();
		String absolutePathForCommRequest = Paths.get("src", "test", "inputPayloads", "commReq-failure.json").toFile()
				.getAbsolutePath();
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "cdm-patient-failure.json").toFile()
				.getAbsolutePath();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
				JsonNode.class);
		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommRequest).toFile(),
				JsonNode.class);
		JsonNode patientData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		PatientGlobalOptoutStatus patientGlobalOptoutStatus=new PatientGlobalOptoutStatus();
		String result = patientGlobalOptoutStatus.patientGlobalOptoutStatusCheck(contractConfigData, communicationData,
				patientData);

		Assert.assertEquals("", result);

	}

}
