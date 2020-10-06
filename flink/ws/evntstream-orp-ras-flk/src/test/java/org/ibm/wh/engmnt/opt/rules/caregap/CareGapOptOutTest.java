/*******************************************************************************
 * * Watson Health Imaging Analytics * * IBM Confidential * * OCO Source
 * Materials * * (C) Copyright IBM Corp. 2020 * * The source code for this
 * program is not published or otherwise * divested of its trade secrets,
 * irrespective of what has been * deposited with the U.S. Copyright Office.
 *******************************************************************************/

/*
 * package org.ibm.wh.engmnt.opt.rules.caregap;
 * 
 * import static org.junit.Assert.assertNotNull;
 * 
 * import java.nio.file.Paths;
 * 
 * import org.ibm.wh.engmnt.orp.rules.caregap.CaregapOptout; import
 * org.junit.Test;
 * 
 * import com.fasterxml.jackson.databind.JsonNode; import
 * com.fasterxml.jackson.databind.ObjectMapper;
 * 
 * public class CareGapOptOutTest {
 * 
 * @Test public void checkCareGapOptOut() throws Exception { String
 * absolutePathForCommReq = Paths.get("src", "test", "inputPayloads",
 * "CommunicationRequest.json").toFile() .getAbsolutePath(); CaregapOptout
 * careGapOptOutCheck = new CaregapOptout(); ObjectMapper objectMapper = new
 * ObjectMapper(); JsonNode communicationData =
 * objectMapper.readValue(Paths.get(absolutePathForCommReq).toFile(),
 * JsonNode.class); String patientCareGapOptOutStatusReason =
 * careGapOptOutCheck.checkForPatientCaregapOptout(communicationData);
 * assertNotNull(patientCareGapOptOutStatusReason); } }
 */