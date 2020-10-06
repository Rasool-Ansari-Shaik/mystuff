package org.ibm.wh.engmnt.orp.utility;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtil {
	private static ObjectMapper jsonParser;

	/* This Method checks whether a Practitioner is enabled or not. */
	public boolean isPractitionerEnabled(String practitionerNpi, String practitionerConfigInfo)
			throws SQLException, JsonMappingException, JsonProcessingException {
		jsonParser = new ObjectMapper();
		JsonNode practionerConfigNode = jsonParser.readValue(practitionerConfigInfo, JsonNode.class);
		List<JsonNode> practitionersList = Arrays
				.asList(jsonParser.readValue(practionerConfigNode.get("practitioners").toString(), JsonNode[].class));

		boolean isPractionerNpiEnabled = practitionersList.stream().anyMatch(
				ele -> ele.get("id").asText().equalsIgnoreCase(practitionerNpi) && ele.get("enabled").asBoolean());

		return isPractionerNpiEnabled;
	}

	/* This Method checks whether a Location is enabled or not. */
	public boolean isLocationEnabled(String locationTin, String locationConfigInfo)
			throws SQLException, JsonMappingException, JsonProcessingException {
		jsonParser = new ObjectMapper();
		JsonNode locationConfigNode = jsonParser.readValue(locationConfigInfo, JsonNode.class);
		List<JsonNode> locationsList = Arrays
				.asList(jsonParser.readValue(locationConfigNode.get("locations").toString(), JsonNode[].class));

		boolean isLocationTinEnabled = locationsList.stream().anyMatch(
				ele -> ele.get("id").asText().equalsIgnoreCase(locationTin) && ele.get("enabled").asBoolean());

		return isLocationTinEnabled;

	}

}
