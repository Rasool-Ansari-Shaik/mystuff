package com.phytel.patient.match.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

/**
 * this model class holds algorithms which are enabled
 *
 */
@Getter
@ToString
public class PatientMatchAlgorithms {
	
	private Map<String,List<Criteria>> algorithms;

	public PatientMatchAlgorithms() {
		this.algorithms = new HashMap<>();
	}
	
	
	
}
