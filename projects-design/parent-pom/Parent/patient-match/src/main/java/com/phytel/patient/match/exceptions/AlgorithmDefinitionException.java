package com.phytel.patient.match.exceptions;
/**
 * This class handles sequence related exceptions
 *
 */
public class AlgorithmDefinitionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public AlgorithmDefinitionException() {
		super();
	}
	public AlgorithmDefinitionException(String message) {
		super(message);
	}
}
