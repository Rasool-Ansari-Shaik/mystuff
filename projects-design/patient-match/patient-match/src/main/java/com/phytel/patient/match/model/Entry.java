package com.phytel.patient.match.model;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * This model class contains resource, search and response objects
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(value = {"resource","search","response"})
public class Entry {
	
	@JsonProperty(value = "resource")
	private List<MatchResource> resourceList;
	@JsonProperty(value = "search")
	private Search search;
	@JsonProperty(value = "response")
	private Response response;
}
