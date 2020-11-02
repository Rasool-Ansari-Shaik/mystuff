package com.phytel.patient.match.exceptions;

public class AttributeValueNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public AttributeValueNotFoundException() {
        super();
    }
    public AttributeValueNotFoundException(String message) {
        super(message);
    }
}
