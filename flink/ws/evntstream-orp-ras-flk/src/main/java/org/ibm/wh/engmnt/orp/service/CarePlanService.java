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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;

public interface CarePlanService {
	public String generateFhirCarePlanModel(String carePlanIdentifier, String communicationRequestId,
			String practionerRoleId,String patientId, String careGapName) throws FHIRGeneratorException, JsonProcessingException;
}
