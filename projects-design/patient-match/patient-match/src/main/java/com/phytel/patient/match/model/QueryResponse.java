package com.phytel.patient.match.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * This model class holds all the patient response for database query
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class QueryResponse {
	
	private Integer patientID;
	private Integer masterID;
	private String sourceName;

}
