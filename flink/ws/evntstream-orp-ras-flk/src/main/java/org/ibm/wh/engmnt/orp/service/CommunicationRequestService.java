/*******************************************************************************
 * Watson Health Imaging Analytics
 *
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2020
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.service;

import java.text.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;

public interface CommunicationRequestService {
	public String generateFhirCommunicationRequestModel(String contractConfigInfo,String tenantId, String practitionerId,
			String practitionerRoleId, String patientRecord,String practitionerInfo,String careGapName,String facilityAddress,String locationConfigInfo) throws JsonMappingException, JsonProcessingException, FHIRGeneratorException, ParseException;
}
