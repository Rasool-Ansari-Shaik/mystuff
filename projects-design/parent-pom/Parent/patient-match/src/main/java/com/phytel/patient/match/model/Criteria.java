package com.phytel.patient.match.model;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * This model class contains criteria related attributes
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Criteria {
	
	private int sequenceId;
	private List<String> attributes;
	private String confidence;
	private Map<String,String> sqlQuery;
	private boolean valid = true;
}
