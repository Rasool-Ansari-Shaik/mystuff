package com.phytel.patient.match.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * This model class holds all the patient information
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(value = {"identifier","subscriber","given","family","finitial","ssn","postalCode","phoneNumber","birthDate",
		"sourceId","payerName","applicationName"},alphabetic = false)
public class PatientResource {
	
	@JsonProperty(value = "patient.identifier")
	private String identifier;
	@JsonProperty(value = "patient.subscriber")
	private  String subscriber;
	@JsonProperty(value = "patient.given")
	private String given;
	@JsonProperty(value = "patient.family")
	private String family;
	@JsonProperty(value = "patient.finitial")
	private String finitial;
	@JsonProperty(value = "patient.ssn")
	private String ssn;
	@JsonProperty(value = "patient.postalCode")
	private String postalCode;
	@JsonProperty(value = "patient.phoneNumber")
	private String phoneNumber;
	@JsonProperty(value = "patient.birthDate")
	private String birthDate;
	@JsonProperty(value = "patient.sourceId")
	private String sourceId;
	@JsonProperty(value = "payer.name")
	private String payerName;
	@JsonProperty(value = "application.name")
	private String applicationName;
}
