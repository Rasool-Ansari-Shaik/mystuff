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
import java.text.ParseException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;

public class PatientAttributedPractitionerTest {
	
	@Test
	public void checkPatientModalityTestSuccess() throws JsonMappingException, JsonProcessingException, IOException, ParseException{
		String absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
				.getAbsolutePath();
		String absolutePathForCommRequest = Paths.get("src", "test", "inputPayloads", "CommunicationRequest.json")
				.toFile().getAbsolutePath();
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "cdm-patient.json").toFile()
				.getAbsolutePath();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode patientData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
				JsonNode.class);
		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommRequest).toFile(),
				JsonNode.class);
		PatientAttributedPractioner patientAttributedPractioner = new PatientAttributedPractioner();
		String patientAttributedPractitionerStatus = patientAttributedPractioner
				.checkForPatientPractitioner(contractConfigData, patientData, communicationData);
		
		assertNotNull(patientAttributedPractitionerStatus);
	}
	
	
	@Test
	public void checkPatientModalityTestFailure() throws JsonMappingException, JsonProcessingException, IOException, ParseException{
		String absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
				.getAbsolutePath();
		String absolutePathForCommRequest = Paths.get("src", "test", "inputPayloads", "CommunicationRequest.json")
				.toFile().getAbsolutePath();
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "cdm-patient-noPractitioner.json").toFile()
				.getAbsolutePath();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode patientData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
				JsonNode.class);
		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommRequest).toFile(),
				JsonNode.class);
		PatientAttributedPractioner patientAttributedPractioner = new PatientAttributedPractioner();
		String patientAttributedPractitionerStatus = patientAttributedPractioner
				.checkForPatientPractitioner(contractConfigData, patientData, communicationData);
		Assert.assertEquals(patientAttributedPractitionerStatus, "Outreach-Practitioner-Not-Found");
		assertNotNull(patientAttributedPractitionerStatus);
	}
	
	
	

}
