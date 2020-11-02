package com.phytel.patient.match.service;
import java.util.List;
import java.util.Map;

import com.phytel.patient.match.model.Bundle;
import com.phytel.patient.match.model.Criteria;
import com.phytel.patient.match.model.MatchResource;
import com.phytel.patient.match.model.PatientMatchAlgorithms;
import com.phytel.patient.match.model.PatientMatchResult;
import com.phytel.patient.match.model.PatientResource;

public interface PatientMatchService {
	
	public List<Criteria> loadCriteria(String algorithmName);
	public Bundle validateAndExecuteCriteria(PatientResource patientData, String contractNumber, String dataSource, String algName );
	List<MatchResource> executeCriteria(String algorithmName, PatientResource patient, String contractNumber,
			String dataStore);
	public PatientMatchAlgorithms initAlgorithms();
	public Map<Integer, PatientMatchResult> recordPatientMatchResults(Bundle bundle);
		
}
