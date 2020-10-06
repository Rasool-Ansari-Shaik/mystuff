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
package org.ibm.wh.engmnt.opt.rules.caregap;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Paths;

import org.ibm.wh.engmnt.orp.rules.caregap.CaregapPastCommunication;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;

public class CareGapPastCommunicationTest {
	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void checkForPatientImportance() throws JsonMappingException, JsonProcessingException, IOException {
		String absolutePathForCommReq = Paths.get("src", "test", "inputPayloads", "communication.json").toFile()
				.getAbsolutePath();
		String absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
				.getAbsolutePath();

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommReq).toFile(), JsonNode.class);
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
				JsonNode.class);
		CaregapPastCommunication caregapPastCommunication = new CaregapPastCommunication();

		String result = caregapPastCommunication.checkForPatientPastCommunication(communicationData,
				contractConfigData);
		assertNotNull(result);
	}

	@Test
	public void checkPatientCommunicationFailure()
			throws JsonMappingException, JsonProcessingException, IOException, ParseException {

		String absolutePathForCommunication = Paths
				.get("src", "test", "inputPayloads", "communication-unsuccessful.json").toFile().getAbsolutePath();
		String absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads", "contract.json").toFile()
				.getAbsolutePath();

		JsonNode communicationData = objectMapper.readValue(Paths.get(absolutePathForCommunication).toFile(),
				JsonNode.class);
		JsonNode contractConfigData = objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
				JsonNode.class);

		CaregapPastCommunication caregapPastCommunication = new CaregapPastCommunication();
		String patientPastCommunication = caregapPastCommunication.checkForPatientPastCommunication(communicationData,
				contractConfigData);
		assertNotNull(patientPastCommunication);

	}
}
