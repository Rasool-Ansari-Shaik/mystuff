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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

import org.ibm.wh.engmnt.orp.rules.modalityEligibility.ModalityDataAvailableForPatient;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ModalityDataAvailableForPatientTest {
	
	@Test
	public void checkForMoodalityDataAvailabilitySuccess() throws ParseException, IOException {
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "cdm-patient.json").toFile()
				.getAbsolutePath();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode patientData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		ModalityDataAvailableForPatient modalityDataAvailableForPatient=new ModalityDataAvailableForPatient();
		String result = modalityDataAvailableForPatient.checkPatientModalityData("email", patientData);
		assertNotNull(result);

	}
	
	@Test
	public void checkForMoodalityDataAvailabilityFailure() throws ParseException, IOException {
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "cdm-patient.json").toFile()
				.getAbsolutePath();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode patientData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		ModalityDataAvailableForPatient modalityDataAvailableForPatient=new ModalityDataAvailableForPatient();
		String result = modalityDataAvailableForPatient.checkPatientModalityData("sms", patientData);
		assertNotNull(result);

	}

}
