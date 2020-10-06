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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
public class PatientModality {
	private final static Logger LOGGER = LoggerFactory.getLogger(PatientModality.class);
	
	/*
	 * Method to check for enabled Patient Modality
	 * @CommRequest is a Communication Request
	 */
		public String checkForPatientModality(JsonNode patientInfo) throws JsonProcessingException, ParseException {
				String statusReason="";
				List<String> list=new ArrayList<String>();
				if(patientInfo.get("telecom")!=null) {
					for(int i=0;i<patientInfo.get("telecom").size();i++) {
						list.add(patientInfo.get("telecom").get(i).get("system").asText());
					}
				}
				if(list.size() > 0) {
					LOGGER.info("Patient has Modalities :"+list);
					}else {
					statusReason = PropertyUtil.getProperty("commrequest.patientModalityCheck.value");
					LOGGER.info("Patient has no Modalities");
					LOGGER.info("Updating commRequest statusReason as "+statusReason);
				}
					
				return statusReason;
		}
}