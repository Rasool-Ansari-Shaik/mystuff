package com.phytel.patient.match.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This model class holds all the variables used for insertion of PatientMatch
 * Response in Db
 *
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class PatientMatchResult {

	private Integer id;
	private Integer patientID;
	private Integer masterPatientID;
	private Integer score;
	private String sourceName;
	private String patientAttribute;
	private String algorithmName;
	private String dataStore;
	private String applicationName;
	private LocalDateTime createDateTime;

}
