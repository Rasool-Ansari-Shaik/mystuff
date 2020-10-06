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

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CadfSeverity {

    @JsonProperty("normal")
    NORMAL,

    @JsonProperty("warning")
    WARNING,

    @JsonProperty("critical")
    CRITICAL

}
