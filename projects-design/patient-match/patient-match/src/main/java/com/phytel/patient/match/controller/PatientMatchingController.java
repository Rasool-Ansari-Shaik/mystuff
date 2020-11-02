package com.phytel.patient.match.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.phytel.patient.match.model.Bundle;
import com.phytel.patient.match.model.PatientResource;
import com.phytel.patient.match.service.PatientMatchService;

/**
 * This Controller contains POST URI returns the Bundle
 *
 */
@RestController
public class PatientMatchingController {
	
	@Autowired
	private PatientMatchService patientMatchService;
	
	private static Logger logger = LoggerFactory.getLogger(PatientMatchingController.class);

	/**
	 * This method does the patient match and returns the Bundle
	 * 
	 */
	@PostMapping("/patient/match/{contractNumber}/{dataStore}")
	public Bundle doPatientMatch(@RequestBody PatientResource patientInfo, 
			@PathVariable String contractNumber,
			@PathVariable String dataStore,
			@RequestParam String algorithmName) {
		
		logger.info("Request processing started");
		Bundle bundle = patientMatchService.validateAndExecuteCriteria(patientInfo, contractNumber, dataStore, algorithmName);
		logger.info("Request processing completed");
	   
	    
		return bundle;
		
	}
	

}
