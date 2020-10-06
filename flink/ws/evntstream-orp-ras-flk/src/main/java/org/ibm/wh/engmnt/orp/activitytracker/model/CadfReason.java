/*******************************************************************************
 *  * Watson Health Imaging Analytics
 *  *
 *  * IBM Confidential
 *  *
 *  * OCO Source Materials
 *  *
 *  * (C) Copyright IBM Corp. 2020
 *  *
 *  * The source code for this program is not published or otherwise
 *  * divested of its trade secrets, irrespective of what has been
 *  * deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.activitytracker.model;


/**
* Outcome reason. Provides additional information to describe the event
* outcome
*/
public final class CadfReason {
    private String reasonType;
    private String reasonCode;

    /**
     * Create a CADF Reason object
     * @param reasonType - String. The reason code domain URI. Must be present 
     * if reasonCode is present.
     * @param reasonCode - String. Detailed result code as described by the domain 
     * identifier (reason type). Must be specified if policyId is not specified.
     */
    public CadfReason(String reasonType, String reasonCode) {
        this.reasonType = reasonType;
        this.reasonCode = reasonCode;
    }




    public String getReasonType() {
        return reasonType;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    
}
