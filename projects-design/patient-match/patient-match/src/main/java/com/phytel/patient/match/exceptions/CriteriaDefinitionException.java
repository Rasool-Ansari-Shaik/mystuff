package com.phytel.patient.match.exceptions;
/**
 * This class handles all criteria related exceptions
 *
 */
public class CriteriaDefinitionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public CriteriaDefinitionException() {
		super();
	}
	public CriteriaDefinitionException(String message) {
		super(message);
	}
}
