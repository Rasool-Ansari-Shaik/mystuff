package com.phytel.patient.match.model;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * this model class holds match resource attributes
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(value = {"identifier","masterId","score","dateTime","sourceName","attributeList"})
public class MatchResource {
	
	@JsonProperty(value = "patient.id")
	private Integer identifier;
	@JsonProperty(value = "patient.masterId")
	private Integer masterId;
	@JsonProperty(value = "match.score")
	private Integer score;
	@JsonProperty(value = "match.dateTime")
	private LocalDateTime dateTime;
	@JsonProperty(value = "source.name")
	private String sourceName;
	@JsonProperty(value = "patient.attributes")
	private Map<String, String> attributeList = new HashMap<>();
}
