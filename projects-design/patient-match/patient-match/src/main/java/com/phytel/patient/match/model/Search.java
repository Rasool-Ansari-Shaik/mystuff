package com.phytel.patient.match.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * This model class holds search criteria attributes 
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(value = {"algName","dataStore","applicationName"})
public class Search {
	
	@JsonProperty(value = "algorithm.name")
	private String algorithmName;
	@JsonProperty(value = "dataStore")
	private String dataStore;
	@JsonProperty(value = "application.name")
	private String applicationName;
}
