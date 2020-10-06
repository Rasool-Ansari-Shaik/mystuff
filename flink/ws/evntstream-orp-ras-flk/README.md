# Outreach Rule Processor 
​
## Description
​
```
Outreach Rule Processor processes the MeasureRport and generates CommunicationRequest and CarePlan FHIR resources.
```
​## Flow of Execution:

```
          1. NiFi publishes the CDM MeasureReport to the EventStream's input topic (atc.117.clinical.in), 117 is the tenant-id here.
          2. Flink Job pulls the MeasureReport from EventStream topic and reads the Patient id from it.
          3. Flink Job makes REST call to fetch all FHIR resources like Patient, PractitionerRole, Practitioner, Location which are required to generate CarePlan and CommunicationRequest 
          4. Flink Job generates the CommunicationRequest FHIR resource by considering the Communication preference on Product level and performs necessary validations on modality.
             CommunicationRequest also sends the Payload data which is an input for Communication Engine.
          5. Flink Job generates the CarePlan FHIR resource by passing CommunicatoinRequest as a reference.
          6. Flink Job also pushes the CommunicationRequest to EventStream's output topic (atc.117.clinical.out) after inserting CommunicationRequest and CarePlan into FHIR Server.
          7. From the EventStream output topic, Scheduler reads the Communication Window and continues the flow.
```

