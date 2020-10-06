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

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PatientDeceaseCheckTest {
	@Test
	public void checkForPatientDeceae() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		String absolutePathForPatient = Paths.get("src", "test", "inputPayloads", "cdm-patient.json").toFile()
				.getAbsolutePath();
		
		JsonNode patientJsonData = objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(), JsonNode.class);
		PatientDeceaseStatus patientDeceaseStatus=new PatientDeceaseStatus();
		String patientDecease= patientDeceaseStatus.checkForPatientDecease(patientJsonData);
		assertNotNull(patientDecease);
	}
}
