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

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Implementation of the CADF-like Resource type as defined by the IBM Cloud
 * Activity Tracker service.
 * 
 * Resource represents an actor in a CADF event: OBSERVER that reports the
 * event, INITIATOR that performs the action that generates the event, or TARGET
 * upon which the action is performed.
 */
public class CadfResource {

    /**
     * This class represents the CADF Resource type taxonomy.
     * <p>
     * Properties have string values, because they must be serialized as URIs,
     * per CADF specifications.
     */
    public enum ResourceType {
        /**
         * compute/node
         */
        compute_node("compute/node"),
        /**
         * compute/cpu
         */
        compute_cpu("compute/cpu"),
        /**
         * compute/machine
         */
        compute_machine("compute/machine"),
        /**
         * compute/process
         */
        compute_process("compute/process"),
        /**
         * compute/thread
         */
        compute_thread("compute/thread")

        ,security_account("security/account")      
        ,security_account_user("security/account/user") 
        ,security_account_admin("security/account/admin") 
        ,security_credential("security/credential") 
        ,security_group("security/group")
        ,security_identity("security/identity") 
        ,security_key("security/key") 
        ,security_license("security/license") 
        ,security_node("security/node")
        ,security_policy("security/policy") 
        ,security_profile("security/profile") 
        ,security_role("security/role") 
        ,
        /**
         * service/bss (business support services)
         */
        service_bss("service/bss"),
        /**
         * service/bss/metering
         */
        service_bss_metering("service/bss/metering"),
        /**
         * service/composition The logical classification grouping for services
         * that supports the compositing of independent services into a new
         * service offering
         */
        service_composition("service/composition"),
        /**
         * service/compute: Infrastructure services for managing computing
         * (fabric).
         */
        service_compute("service/compute"),
        /**
         * service/database (DBaaS)
         */
        service_database("service/database"),
        /**
         * service/image: Infrastructure services for managing virtual machine
         * images and associated metadata.
         */
        service_image("service/image"),
        /**
         * service/network: Infrastructure services for managing networking
         * (fabric).
         */
        service_network("service/network"),
        /**
         * service/oss (Operational support services)
         */
        service_oss("service/oss"),
        /**
         * service/oss/monitoring
         */
        service_oss_monitoring("service/oss/monitoring"),
        /**
         * service/oss/logging
         */
        service_oss_logging("service/oss/logging"),
        /**
         * service/security: The logical classification grouping for security
         * services including Identity Mgmt., Policy Mgmt., Authentication,
         * Authorization, Access Mgmt., etc. (a.k.a. “Security- as-a-Service”)
         */
        service_security("service/security"),
        /**
         * service/storage
         */
        service_storage("service/storage"),
        /**
         * service/storage/block
         */
        service_storage_block("service/storage/block"),
        /**
         * service/storage/object
         */
        service_storage_object("service/storage/object"),
        /**
         * data/catalog
         */
        data_catalog("data/catalog"),
        /**
         * data/config
         */
        data_config("data/config"),
        /**
         * data/directory
         */
        data_directory("data/directory"),
        /**
         * data/file
         */
        data_file("data/file"),
        /**
         * data/image
         */
        data_image("data/image"),
        /**
         * data/log
         */
        data_log("data/log"),
        /**
         * data/message
         */
        data_message("data/message"),
        /**
         * data/message/stream
         */
        data_message_stream("data/message/stream"),
        /**
         * data/module
         */
        data_module("data/module"),
        /**
         * data/package
         */
        data_package("data/package"),
        /**
         * data/report
         */
        data_report("data/report"),
        /**
         * data/template
         */
        data_template("data/template"),
        /**
         * data/workload
         */
        data_workload("data/workload"),
        /**
         * data/database
         */
        data_database("data/database"),
        /**
         * data/security
         */
        data_security("data/security"),


        /**
         * Target types
         */
        file_cos("ibm/cloud-storage/file"),
        watson_studio("ibm/report/watson-studio"),
        db2("ibm/database/db2"),
        cognos("ibm/report/cognos"),
        uap("ibm/wh/uap"),
        wh_offering("ibm/wh/offering"),
        unknown("unknown");

        private String uri;

        ResourceType(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return uri;
        }

        @JsonCreator
        public static ResourceType forValue(String value) {
            try {
                return ResourceType.valueOf(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        @JsonValue
        public String toValue() {
            for (ResourceType v : ResourceType.values()) {
                if (v == this) {
                    return this.uri;
                }
            }
            return null;
        }
    }
    private final String id;
    private final ResourceType typeURI;
    private final String name;
    // the next two are only required for Initiator
    private final CadfCredential credential; 
    private final CadfHost host;

    private CadfResource(Builder builder) {
        this.id = builder.id;
        this.typeURI = builder.typeURI;
        this.name = builder.name;
        this.credential = builder.credential;
        this.host = builder.host;
    }

    /**
     * Validate contents of the resource. 
     * 
     * The logic is determined by the Activity Tracker. It requires id, typeURI,
     * and name (and host and credential for Initiator)
     * 
     * @throws IllegalStateException when the properties do not meet the specification.
     */
    private void validate() throws IllegalStateException {
        if (this.typeURI == null || this.id == null || this.id.isEmpty() || this.name == null || this.name.isEmpty())
        {
            throw new IllegalStateException("missing required properties");
        }
    }

    /**
     * Builder for immutable CadfResource objects
     */
    public static class Builder {
        private String id;
        private ResourceType typeURI;
        private String name;
        private CadfCredential credential;
        private CadfHost host;

        /**
         * Creates an instance of the CadfResource builder.
         * @param id - String. Resource identifier.
         * @param typeURI - CadfEvent.ResourceType. Resource classification in the CADF taxonomy.
         * @see CadfEvent#ResourceType
         */
        public Builder(String id, ResourceType typeURI, String name) {
            if (id == null) {
                this.id = UUID.randomUUID().toString();
            }
            else {
                this.id = id;
            }
            this.typeURI = typeURI;
            this.name = name;
        }

        /**
         * Set the optional information about the (network) host of the resource 
         * @param host 
         * @return Builder
         */
        public Builder withHost(String host) {
            this.host = new CadfHost(host);
            return this;
        }
    
        /**
         * Set the optional optional security credentials associated with the 
         * resource’s identity.
         * @see {@link CadfCredential}
         * @param cred 
         * @return Builder
         */
        public Builder withCredential(CadfCredential cred) {
            this.credential = cred;
            return this;
        }
        
        /**
         * Build an immutable ReporterStep instance.
         * @return ReporterStep
         * @throws IllegalStateException when the properties do not meet the specification.
         */
        public CadfResource build() throws IllegalStateException {
            CadfResource res = new CadfResource(this);
            res.validate();
            return res;
        }
    }    
}
