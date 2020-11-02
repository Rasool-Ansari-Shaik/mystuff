package com.phytel.patient.match.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * This model class contains response status and  message 
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(value = {"status","message"})
public class Response {
	
	@JsonProperty(value = "status")
	private String status;
	@JsonProperty(value = "message")
	private String message;
}
