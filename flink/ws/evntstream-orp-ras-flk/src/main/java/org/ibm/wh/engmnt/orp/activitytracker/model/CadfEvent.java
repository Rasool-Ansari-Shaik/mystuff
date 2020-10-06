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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.ibm.wh.engmnt.orp.activitytracker.util.JsonEncoder;

/**
 * This class represent a CADF-like audit event as implemented by IBM Cloud
 * Activity Tracker service (https://test.cloud.ibm.com/docs/services/Activity-Tracker-with-LogDNA?topic=logdnaat-ibm_event_fields)
 * It deviates from the standard defined by the DMTF. \
 * <p>
 * A CADF event generally represents a "subject-verb-object" tuple to indicate
 * what action a particular subject performed over what object.
 * <p>
 * The class is intended to be serialized using JSON Binding (Jsonb). It is
 * implemented using the Builder pattern.
 */
public class CadfEvent {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * CADF event types:
     * <p>
     * "monitor" -- events that provide information about the status of a
     * resource or of its attributes or properties. Such events typically report
     * on measurements or periodic probes on cloud resources, and may produce
     * aggregate data such as statistical or summary metrics.
     * <p>
     * "activity" -- events that provide information about actions having
     * occurred or intended to occur, and initiated by some resource or done
     * against some resource.
     * <p>
     * "control" -- events that reflect on or provide information about the
     * application of a policy or business rule, or more generally express the
     * outcome of a decision making process. Such events typically report on how
     * these policies or rules manifest in concrete situations such as attempted
     * resource access, evaluation of resource states, notifications,
     * prioritization of tasks, or other automated administrative action.
     * 
     * Activity Tracker flavour of CADF only issues/expects activity events.
     */
    public enum EventType {
        monitor, activity, control
    }

    /**
     * "Root" outcome classification.
     * <p>
     * "success" -- The attempted action completed successfully with the
     * expected results.
     * <p>
     * "failure" -- The attempted action failed due to some form of operational
     * system failure or because the action was denied, blocked, or refused in
     * some way.
     * <p>
     * "unknown" -- The outcome of the attempted action is unknown and it is not
     * expected that it will ever be known. Example: data sent to a third party
     * via an unreliable protocol without acknowledgment.
     * <p>
     * "pending" -- The outcome of the attempted action is unknown, but it is
     * expected that it will be known at some point in the future. A separate,
     * future, correlated event may provide additional detail. Example: a long-
     * running activity has started; the Observer will follow up with a nearly
     * identical event that includes the final outcome.
     */
    public enum Outcome {
        success, failure, unknown, pending
    }


    // required properties
    private CadfResource initiator;
    private CadfResource target;
    private String action;
    private CadfEvent.Outcome outcome;
    private String eventTime;
    private CadfReason reason;
    private CadfSeverity severity;
    private CadfResource observer;
    
    // optional and automatically populated properties
    private String id;
    private EventType eventType;
    private Boolean dataEvent; // IBM extension
    private String message; // IBM extension
    private String typeURI;
    
    // optional
    private Map<String, String> requestData, responseData;
    private String correlationId;

    private CadfEvent(Builder builder) {
        this.id = builder.id;
        this.eventType = builder.eventType;
        this.eventTime = dateTimeFormatter.format(builder.eventTime);
        this.action = builder.action.toString();
        this.outcome = builder.outcome;
        this.typeURI = builder.typeURI;
        this.reason = builder.reason;
        this.initiator = builder.initiator;
        this.target = builder.target;
        this.observer = builder.observer;
        this.severity = builder.severity;
        this.message = builder.message;
        this.dataEvent = builder.dataEvent;
        this.requestData = builder.requestData;
        this.responseData = builder.responseData;
        this.correlationId = builder.correlationId;
    }

    public String getId() {return this.id;}
    /**
     * Validate contents of the CADF event so far. 
     * 
     * The logic is determined by the CADF specification.
     * 
     * @throws IllegalStateException when the event does not meet the specification.
     */
    private void validate() throws IllegalStateException {
        // any event type must have these components:
        if (
            this.id == null || this.eventType == null || this.eventTime == null ||
            this.action == null || this.outcome == null
        ) {
          throw new IllegalStateException("mandatory properties are missing");
        }
        if ( (this.initiator == null ) || (this.target == null ) || (this.observer == null) ) {
          throw new IllegalStateException("at least one of the required resource references is missing");
        }
        if (this.typeURI == null) {
            throw new IllegalStateException("typeURI is required for JSON serialization");
        }

        
        // if we are here, everything seems to be ok
    }

    @Override
    public String toString() {
        return JsonEncoder.encode(this);
    }

    public static class Builder {

        // required properties
      private String id;
      private CadfEvent.EventType eventType;
      private OffsetDateTime eventTime;
      private CadfAction action;
      private CadfEvent.Outcome outcome;
      private Boolean dataEvent = false;
  
      // optional properties
      private final String typeURI = "ibm/cloud/activitytracker/event"; // This is not a real CADF event, use made up URI
      private CadfReason reason;
      private CadfResource initiator;
      private CadfResource target;
      private CadfResource observer;
      private CadfSeverity severity;
      private Map<String, String> requestData, responseData;
      private String message = "";
      private String correlationId;
      
      /**
       * CadfEvent builder constructor. Supply initial values for the event
       * definition.
       * 
       * @param id -- String. Globally unique identifier for the event. If null 
       * value is provided, the identifier is generated automatically.
       * @param eventType -- CadfEvent.EventType. See {@link EventType} for guidance. Defaults to "activity".
       * @param eventTime -- Date. Timestamp of the event occurrence. If null value
       * is provided, this property is set to the current timestamp.
       * @param action -- CadfAction. Indicates the action that created this event.
       * @param outcome -- CadfEvent.Outcome. Indicate the action's outcome.
       */
      public Builder(
          String id,
          CadfEvent.EventType eventType,
          OffsetDateTime eventTime,
          CadfAction action,
          CadfEvent.Outcome outcome
      ) {
        if (id != null) {
            this.id = id;
        }
        else {
            this.id = UUID.randomUUID().toString();
        }
        this.eventType = eventType;
        if (eventTime != null) {
            this.eventTime = eventTime;
        }
        else {
            this.eventTime = OffsetDateTime.now(ZoneOffset.UTC);
        }
        this.action = action;
        this.outcome = outcome;
      }
  
      /**
       * A convenience method to set the event reason. Instantiates a new {@link CadfReason}
       * object using the supplied values.  Reason is a 
       * required property if the event type is "control", otherwise
       * it is optional.
       * @param reasonType - type or category of the reason code
       * @param reasonVofre - reason code
       * @return Builder
       * @see CadfReason
       */
      public Builder withReason( String reasonType, String reasonCode) {
  
        this.reason = new CadfReason(reasonType, reasonCode);
        return this;
      }
  
      /**
       * Sets the event reason. This property contains domain-specific reason code 
       * and policy data that provides an additional level of detail to the outcome 
       * value. Reason is a required property if the event type is "control", otherwise
       * it is optional.
       * @param reason 
       * @return CadfEventBuilder
       * @see CadfReason
       */
      public Builder withReason(CadfReason reason){
        this.reason = reason;
        return this;
      }
  
  
      /**
       * This optional property describes domain-relative severity assigned to the
       * event by OBSERVER.
       * @param severity -- Severity descriptor. 
       * @return CadfEventBuilder
       */
      public Builder withSeverity(CadfSeverity severity){
        this.severity = severity;
        return this;
      }
  

      /** 
       * Property that represents the event INITIATOR. 
       * It is required when initiatorId is not supplied.
       */
      public Builder withInitiator(CadfResource initiator) {
          this.initiator = initiator;
          return this;
      }
  
      /** 
       * Property that represents the event TARGET. 
       * It is required when targetId is not supplied.
       */
      public Builder withTarget(CadfResource target) {
          this.target = target;
          return this;
      }
  

      /** 
       * Property that represents the event OBSERVER. 
       * It is required when observerId is not supplied.
       */
      public Builder withObserver(CadfResource observer) {
          this.observer = observer;
          return this;
      }
  
      public Builder withRequest(Map<String,String> request) {
          this.requestData = request;
          return this;
        }
        public Builder withResponse(Map<String,String> response) {
            this.responseData = response;
            return this;
        }
        public Builder withMessage(String msg) {
            this.message = msg;
            return this;
        }
        public Builder withCorrelationId(String cid) {
            this.correlationId = cid;
            return this;
        }
        
        /**
         * Mark this as "data event".
         * 
         * @return self
         */
        public Builder setDataEvent() {
            this.dataEvent = true;
            return this;
        }
    
      /**
       * 
       * @return {@link CadfEvent}
       * @throws IllegalStateException when the combination of event properties is
       *         not valid.
       */
      public CadfEvent build() throws IllegalStateException {
          CadfEvent evt = new CadfEvent(this);
          evt.validate();
          return evt;
      }
    }

}
