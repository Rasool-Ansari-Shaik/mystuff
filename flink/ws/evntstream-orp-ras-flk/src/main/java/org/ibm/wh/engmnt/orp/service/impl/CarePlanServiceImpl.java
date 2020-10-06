/*******************************************************************************
 * Watson Health Imaging Analytics
 *
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2020
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.service.impl;

import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ibm.wh.engmnt.orp.service.CarePlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.generator.FHIRGenerator;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import com.ibm.fhir.model.resource.CarePlan;
import com.ibm.fhir.model.resource.CarePlan.Activity;
import com.ibm.fhir.model.type.Code;
import com.ibm.fhir.model.type.CodeableConcept;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.DateTime;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.Extension;
import com.ibm.fhir.model.type.Identifier;
import com.ibm.fhir.model.type.Period;
import com.ibm.fhir.model.type.Reference;
import com.ibm.fhir.model.type.Uri;
import com.ibm.fhir.model.type.code.CarePlanIntent;
import com.ibm.fhir.model.type.code.CarePlanStatus;

public class CarePlanServiceImpl implements CarePlanService {
	private final static Logger LOGGER = LoggerFactory.getLogger(ObservationServiceImpl.class);

	public String generateFhirCarePlanModel(String carePlanIdentifier, String communicationRequestId,
			String practitionerRoleId, String patientId, String careGapName)
			throws FHIRGeneratorException, JsonProcessingException {
		LOGGER.debug("generateFhirCarePlanModel method started");
		Identifier identifier = Identifier.builder().value(com.ibm.fhir.model.type.String.of(carePlanIdentifier))
				.build();
		Reference subject = Reference.builder().reference(com.ibm.fhir.model.type.String.of("Patient/" + patientId))
				.display(com.ibm.fhir.model.type.String.of("Peter James Chalmers")).build();
		Period period = Period.builder().end(DateTime.builder().value("2020-06-14").build()).build();
		DateTime created = DateTime.builder().value("2020-06-14").build();
		Reference author = Reference.builder()
				.reference(com.ibm.fhir.model.type.String.of("PractitionerRole/" + practitionerRoleId))
				.display(com.ibm.fhir.model.type.String.of("Dr Adam Careful")).build();
		Code code = Code.builder().value("161832001").build();
		Coding coding = Coding.builder().system(Uri.of("http://snomed.info/sct")).code(code)
				.display(com.ibm.fhir.model.type.String.of("Progressive weight loss")).build();
		CodeableConcept outcomeCodeableConcept = CodeableConcept.builder().coding(coding).build();
		Activity activity = Activity.builder().outcomeCodeableConcept(outcomeCodeableConcept)
				.reference(Reference.builder()
						.reference(com.ibm.fhir.model.type.String.of("CommunicationRequest/" + communicationRequestId))
						.build())
				.build();
		Reference behalfOfReference = Reference.builder()
				.reference(com.ibm.fhir.model.type.String.of("PractitionerRole/"+practitionerRoleId )).build();
		Reference behalfOfMeasure = Reference.builder().reference(com.ibm.fhir.model.type.String.of("Measure/example"))
				.build();
		Extension communicationonBehalfOf = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-on-behalf-of").value(behalfOfReference)
				.build();
		Extension communicationCareGap = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-care-gap")
				.value(com.ibm.fhir.model.type.String.of(careGapName)).build();
		Extension communicationPrioritySequence = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-priority-sequence").value(Decimal.of(1))
				.build();
		Extension communicationMeasure = Extension.builder()
				.url("http://ibm.com/fhir/cdm/StructureDefinition/communication-measure").value(behalfOfMeasure)
				.build();
		CarePlan carePlan = CarePlan.builder().id("example").identifier(identifier).status(CarePlanStatus.ACTIVE)
				.intent(CarePlanIntent.PLAN).title(com.ibm.fhir.model.type.String.of("CarePlan-Example"))
				.description(com.ibm.fhir.model.type.String.of("Manage obesity and weight loss")).subject(subject)
				.period(period).created(created).author(author).activity(activity).extension(communicationonBehalfOf)
				.extension(communicationCareGap).extension(communicationPrioritySequence)
				.extension(communicationMeasure).build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FHIRGenerator.generator(Format.JSON, true).generate(carePlan, baos);
		LOGGER.debug("generateFhirCarePlanModel method ended");
		return baos.toString();
	}
}
