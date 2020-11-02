package com.phytel.patient.match.exceptions;

public class SqlQueryExecutionException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SqlQueryExecutionException() {
		super();
	}
	
	public SqlQueryExecutionException(String message) {
		super(message);
	}
}
