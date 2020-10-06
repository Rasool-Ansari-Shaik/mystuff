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
 * Representation of the CADF-like Host object, as defined by Activity Tracker
 */
public class CadfHost {
    private String address;

    public CadfHost(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
