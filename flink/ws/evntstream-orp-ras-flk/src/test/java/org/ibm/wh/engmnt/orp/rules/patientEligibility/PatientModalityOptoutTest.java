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

import java.nio.file.Paths;

import org.ibm.wh.engmnt.orp.rules.modalityEligibility.PatientModalityOptout;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PatientModalityOptoutTest {

	@Test
	public void checkPatientModalityOptoutTest() throws Exception {
		String absolutePathForCommunication=Paths.get("src","test","inputPayloads","patientCommOptOut.json").toFile().getAbsolutePath();
		ObjectMapper objectMapper=new ObjectMapper();
		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommunication).toFile(), JsonNode.class);
		String patientPreference = "email";
		PatientModalityOptout patientOptOutCheck = new PatientModalityOptout();
		String patientCareGapOptOutStatusReason = patientOptOutCheck.checkPatientModalityOptout(patientPreference,
				communicationData);
		assertNotNull(patientCareGapOptOutStatusReason);
		
	}
	
	
	@Test
	public void checkPatientModalityOptoutFailureTest() throws Exception {
		String absolutePathForCommunication=Paths.get("src","test","inputPayloads","patientCommOptOut.json").toFile().getAbsolutePath();
		ObjectMapper objectMapper=new ObjectMapper();
		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommunication).toFile(), JsonNode.class);
		String patientPreference = "text";
		PatientModalityOptout patientOptOutCheck = new PatientModalityOptout();
		String patientCareGapOptOutStatusReason = patientOptOutCheck.checkPatientModalityOptout(patientPreference,
				communicationData);
		assertNotNull(patientCareGapOptOutStatusReason);
		
	}
	
	
}
