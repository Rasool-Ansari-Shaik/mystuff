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

public class CadfAction {
    /**
     * This class represents the CADF-like ACTION taxonomy as defined by the
     * IBM Cloud Activity Tracker (https://test.cloud.ibm.com/docs/services/Activity-Tracker-with-LogDNA?topic=logdnaat-ibm_event_fields).
     * <p>
     * Action strings must follow the pattern: <service>.<object-type>.<action-string>
     */

    public enum Action {
        /**
         * Event type: activity. An attempt to add (create) the target resource.
         */
        add("add"),
        /**
         * Event type: control. Indicates that the initiating resource has
         * allowed access to the target resource.
         */
        allow("allow"),
        /**
         * Event type: activity. E.g. attach a tag to a resource.
         */
        attach("attach"),
        /**
         * Event type: activity. The target resource is being persisted to
         * long-term storage. No environment, context, or resource state is
         * saved.
         */
        authenticate("authenticate"),
        /**
         * Logon is a specialized authentication action, typically used to
         * establish a resource’s identity or credentials for the resource to be
         * authorized to perform subsequent actions.
         * <p>
         * This is an extension for the "authenticate" action.
         */
        login("authenticate/login"),
        /**
         * 
         */
        backup("backup"),
        /**
         * Event type: activity. The target resource is being persisted to
         * long-term storage including relevant environment, context, and state
         * information (snapshot).
         */
        capture("capture"),
        /**
         * Event type: activity. Target resource configuration is being set up
         * in a particular environment or for a particular purpose.
         */
        configure("configure"),
        /**
         * Event type: activity. 
         */
        connect("connect"),
        /**
         * Event type: activity. Signifies an attempt (successful or not) to
         * create the target resource.
         */
        create("create"),
        /**
         * Event type: activity. Attempt to delete the target resource.
         */
        delete("delete"),
        /**
         * Event type: control. Indicates that the initiating resource has
         * denied access to the target resource.
         */
        deny("deny"),
        /**
         * Event type: activity. The target resource is being provisioned for
         * use by the initiator, but not yet ready.
         */
        deploy("deploy"),
        /**
         * Event type: activity. E.g. detach a tag from a resource.
         */
        detach("detach"),
        /**
         * Event type: activity. The initiator causes some or all functions of
         * the target resource to be stopped or blocked.
         */
        disable("disable"),
        /**
         * Event type: activity. The initiator causes some or all functions of
         * the target resource to be allowed or permitted.
         */
        enable("enable"),
        /**
         * Event type: control. Indicates the evaluation or application of a
         * policy, rule, or algorithm to a set of inputs.
         */
        evaluate("evaluate"),
        /**
         * Event type: activity. 
         */
        export_data("export"),
        /**
         * Event type: activity. 
         */
        import_data("import"),
        /**
         * Event type: activity. 
         */
        inspect("inspect"),
        /**
         * Event type: activity. List contents of the target resource.
         */
        list("list"),
        /**
         * Event type: monitor. The only allowed action for this event type.
         */
        monitor("monitor"),
        /**
         * Event type: control. Indicates that the initiating resource has sent
         * a notification based on some policy or algorithm application –
         * perhaps it has generated an alert to indicate a system problem.
         */
        notify("notify"),
        /**
         * Event type: activity. 
         */
        pull("pull"),
        /**
         * Event type: activity. 
         */
        push("push"),
        /**
         * Event type: activity. Attempt to read from the target resource.
         */
        read("read"),

        /**
         *
         */
        receive("receive"),
        /**
         *
         */
        reimport("reimport"),
        /**
         *
         */
        remove("remove"),
        /**
         *
         */
        renew("renew"),
        /**
         * Event type: activity. The target resource is being re-created from
         * persistent storage.
         */
        restore("restore"),
        /**
         *
         */
        revoke("revoke"),
        /**
         * Event type: activity. E.g. rewrap a Key Protect key
         */
        rewrap("rewrap"),
        /**
         *
         */
        scale("scale"),
        /**
         *
         */
        search("search"),
        /**
         *
         */
        set_on("set-on"),
        /**
         *
         */
        set_off("set-off"),
        /**
         *
         */
        send("send"),
        /**
         *
         */
        start("start"),
        /**
         *
         */
        stop("stop"),
        /**
         *
         */
        test("test"),
        /**
         *
         */
        undeploy("undeploy"),
        /**
         * Event type: activity. Attempt to modify (change) one or more
         * properties of the target resource.
         */
        update("update"),
        write("write"),
        ;

        private String uri;

        Action(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return uri;
        }
    }

    private String action;

    /**
     * Activity Tracker, in contradiction to the CADF standard, prescribes a
     * different format for the action URI: <service>.<target-type>.<action verb>
     * 
     * @param svc The type of service performing the action
     * @param target The class of the target resource
     * @param action Verb of the action, as an enum with standardized actions
     */
    public CadfAction(String svc, String target, Action action) {
        this.action = String.format("%s.%s.%s", svc, target, action.toString());
    }

    /**
     * Activity Tracker, in contradiction to the CADF standard, prescribes a
     * different format for the action URI: <service>.<target-type>.<action verb>
     * 
     * @param svc The type of service performing the action
     * @param target The class of the target resource
     * @param action Verb of the action, as an arbitrary string
     */
    public CadfAction(String svc, String target, String action) {
        this.action = String.format("%s.%s.%s", svc, target, action);
    }
    
    /**
     * Activity Tracker, in contradiction to the CADF standard, prescribes a
     * different format for the action URI: <service>.<target-type>.<action verb>
     * 
     * @param svc The type of service performing the action
     * @param target The class of the target resource
     * @param action Verb of the action, as an arbitrary string
     */
    public CadfAction(String action) {
        this.action = String.format("%s", action);
    }


    @Override
    public String toString() {
        return action;
    }

}
