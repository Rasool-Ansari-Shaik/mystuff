package com.phytel.patient.match.exceptions;

/**
 * This class handles DataStore related exceptions
 *
 */
public class DataStoreNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataStoreNotFoundException() {
		super();
	}

	public DataStoreNotFoundException(String message) {
		super(message);
	}
	
}
