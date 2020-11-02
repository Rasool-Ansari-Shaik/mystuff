package com.phytel.patient.match.model;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This model class holds bundle object
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder(value = {"entry"})
public class Bundle {
	
	@JsonProperty(value = "entry")
	private List<Entry> entry;
}
