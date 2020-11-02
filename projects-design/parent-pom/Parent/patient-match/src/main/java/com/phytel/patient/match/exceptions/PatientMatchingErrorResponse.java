package com.phytel.patient.match.exceptions;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
/**
 *This class holds timestamp, message and details for exceptions 
 *
 */
@Setter
@Getter
@AllArgsConstructor
public class PatientMatchingErrorResponse {

	private Date timestamp;
	private String message;
	private String details;
}
