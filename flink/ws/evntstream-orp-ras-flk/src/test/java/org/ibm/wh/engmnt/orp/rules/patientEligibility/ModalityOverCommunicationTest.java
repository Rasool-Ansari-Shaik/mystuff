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
/*
 * package org.ibm.wh.engmnt.orp.rules.patientEligibility;
 * 
 * import static org.junit.Assert.assertNotNull;
 * 
 * import java.io.IOException; import java.nio.file.Paths; import
 * java.text.ParseException;
 * 
 * import
 * org.ibm.wh.engmnt.orp.rules.modalityEligibility.ModalityOverCommunication;
 * import org.junit.Test;
 * 
 * import com.fasterxml.jackson.core.JsonProcessingException; import
 * com.fasterxml.jackson.databind.JsonMappingException; import
 * com.fasterxml.jackson.databind.JsonNode; import
 * com.fasterxml.jackson.databind.ObjectMapper;
 * 
 * import junit.framework.Assert;
 * 
 * public class ModalityOverCommunicationTest {
 * 
 * @Test public void checkMaodalityOverCommunictaionSucess() throws Exception{
 * String absolutePathForPatient = Paths.get("src", "test", "inputPayloads",
 * "patient-overModality.json").toFile() .getAbsolutePath(); String
 * absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads",
 * "contract.json").toFile() .getAbsolutePath(); ObjectMapper objectMapper = new
 * ObjectMapper(); ModalityOverCommunication modalityOverCommunicationTest=new
 * ModalityOverCommunication(); String patientPreference="email"; JsonNode
 * patientData =
 * objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(),
 * JsonNode.class); String patientId = patientData.get("id").asText(); JsonNode
 * contractData =
 * objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
 * JsonNode.class); String
 * patientModalityStatusReason=modalityOverCommunicationTest.
 * checkModalityOverCommunicationForPatientPreference(patientPreference,
 * patientId,contractData); assertNotNull(patientModalityStatusReason); }
 * 
 * @Test public void checkMaodalityOverCommunictaionFailure() throws Exception{
 * String absolutePathForPatient = Paths.get("src", "test", "inputPayloads",
 * "patient-overModality.json").toFile() .getAbsolutePath(); String
 * absolutePathForContractConfig = Paths.get("src", "test", "inputPayloads",
 * "contract-failure.json").toFile() .getAbsolutePath(); ObjectMapper
 * objectMapper = new ObjectMapper(); ModalityOverCommunication
 * modalityOverCommunicationTest=new ModalityOverCommunication(); String
 * patientPreference="email"; JsonNode patientData =
 * objectMapper.readValue(Paths.get(absolutePathForPatient).toFile(),
 * JsonNode.class); String patientId = patientData.get("id").asText(); JsonNode
 * contractData =
 * objectMapper.readValue(Paths.get(absolutePathForContractConfig).toFile(),
 * JsonNode.class); String
 * patientModalityStatusReason=modalityOverCommunicationTest.
 * checkModalityOverCommunicationForPatientPreference(patientPreference,
 * patientId,contractData); Assert.assertEquals(patientModalityStatusReason,
 * "np-over-comm-email"); }
 * 
 * }
 */