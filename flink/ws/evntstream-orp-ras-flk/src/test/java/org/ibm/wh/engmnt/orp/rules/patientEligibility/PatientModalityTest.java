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

/* Junit testing for the Patient Modality check.
 * @CommunicationRequest as input
 */

public class PatientModalityTest {
	@Test
	public void checkPatientModality() throws JsonMappingException, JsonProcessingException, IOException, ParseException{
		String absolutePathForPatient=Paths.get("src","test","inputPayloads","cdm-patient.json").toFile().getAbsolutePath();
		ObjectMapper objectMapper = new ObjectMapper();
		PatientModality patientModality=new PatientModality();
		JsonNode patientData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		String patientModalityStatusReason=patientModality.checkForPatientModality(patientData);
		assertNotNull(patientModalityStatusReason);
	}
}
