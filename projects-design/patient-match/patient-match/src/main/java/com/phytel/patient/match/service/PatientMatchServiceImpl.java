package com.phytel.patient.match.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.phytel.patient.match.dao.PatientMatchDAO;
import com.phytel.patient.match.exceptions.AlgorithmDefinitionException;
import com.phytel.patient.match.exceptions.ContractNumberNotFoundException;
import com.phytel.patient.match.exceptions.CriteriaDefinitionException;
import com.phytel.patient.match.exceptions.DataStoreNotFoundException;
import com.phytel.patient.match.model.Bundle;
import com.phytel.patient.match.model.Criteria;
import com.phytel.patient.match.model.DataStores;
import com.phytel.patient.match.model.Entry;
import com.phytel.patient.match.model.MatchResource;
import com.phytel.patient.match.model.PatientMatchAlgorithms;
import com.phytel.patient.match.model.PatientMatchResult;
import com.phytel.patient.match.model.PatientResource;
import com.phytel.patient.match.model.QueryResponse;
import com.phytel.patient.match.model.Response;
import com.phytel.patient.match.model.Search;
import com.phytel.patient.match.util.ApplicationConstants;

/**
 * This service class consists of loading and execution of criteria(s)
 *
 */
@Service
public class PatientMatchServiceImpl implements PatientMatchService {

	private static Logger logger = LoggerFactory.getLogger(PatientMatchServiceImpl.class);

	@Autowired
	private Environment environment;
	
	@Autowired
	private PatientMatchDAO patientDao;
	
	@Value("#{'${" + ApplicationConstants.KEY_FOR_ALGORITHMS + "}'.split(',')}")
	private List<String> algorithms;

	private PatientMatchAlgorithms patientMatchAlgorithms;

	private Bundle bundle;

	@Override
	public void initAlgorithms() {
		patientMatchAlgorithms = new PatientMatchAlgorithms();
		logger.info("Init method started");
		for (String algorithm : algorithms) {
			List<Criteria> criteriaList = loadCriteria(algorithm);
			patientMatchAlgorithms.getAlgorithms().put(algorithm, criteriaList);
		}
		logger.info("displaying loaded algorithms " + algorithms);
		logger.info("Patient match algorithms after initialization: "+patientMatchAlgorithms);
		logger.info("Init method completed");
	}
	
	@Override
	public Bundle validateAndExecuteCriteria(PatientResource patientData, String contractNumber, String dataStore,
			String inputAlgorithmNames) {

		logger.info("loadAndExecuteCriteria method execution started");
		initAlgorithms();
		bundle = new Bundle();
		List<Entry> patientEntryList = new ArrayList<>();
		List<String> inputAlgorithmList = new ArrayList<>();
		Map<Integer, PatientMatchResult> recordMap = new HashMap<>();

		if ("*".equals(inputAlgorithmNames)) {
			inputAlgorithmList = new ArrayList<>(patientMatchAlgorithms.getAlgorithms().keySet());
		} else {
			inputAlgorithmList = Arrays.asList(inputAlgorithmNames.split(","));
		}
		
		logger.info("do Null check for ContractNumber");
		if (! doNullCheck(contractNumber)) {
			String errorMsg = "Contract Number (" + contractNumber + ") is null or empty";
			logger.error(errorMsg);
			throw new ContractNumberNotFoundException(errorMsg);
		}
		
		logger.info("do Null check for Data Store");
		if (! doNullCheck(dataStore)) {
			String errorMsg = "Data Store (" + dataStore + ") is null or empty";
			logger.error(errorMsg);
			throw new DataStoreNotFoundException(errorMsg);
		}

		if (! isDataStoreExists(dataStore)) {
			String errorMsg = "DataStore " + dataStore + " does not exists";
			logger.error(errorMsg);
			throw new DataStoreNotFoundException(errorMsg);
		}

		for (String algorithmName : inputAlgorithmList) {
			validateCriteria(algorithmName, patientData);
			logger.info("Patient match algorithms after validation: "+patientMatchAlgorithms);
		}
		
		for (String algorithmName : inputAlgorithmList) {
			Entry patientEntry = new Entry();
			Response response = new Response();
			Search search = setSearchAttributes(algorithmName, patientData.getApplicationName(), dataStore);
			List<MatchResource> matchedResourceList = new ArrayList<>();

			if (isAlgorithmExists(algorithmName)) {
				if (isAlgorithmEnabled(algorithmName)) {
					
					matchedResourceList = executeCriteria(algorithmName, patientData, contractNumber, dataStore);
					if (matchedResourceList != null && matchedResourceList.size() > 0) {
						logger.info("Patient Match found");
						response = setResponseAttributes(String.valueOf(HttpStatus.OK.value()),
								String.valueOf(HttpStatus.OK.name()));
					} else {
						logger.info("Patient Match Not Found");
						response = setResponseAttributes(String.valueOf(HttpStatus.NOT_FOUND.value()),
								String.valueOf(HttpStatus.NOT_FOUND.name()));
					}
				} else {
					String message = "Algorithm ("+algorithmName+") is not enabled"; 
					logger.warn(message);
					response = setResponseAttributes(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),	message);
				}
			} else {
				String message = "Algorithm ("+algorithmName+") does not exists";
				logger.warn(message);
				response = setResponseAttributes(String.valueOf(HttpStatus.NOT_IMPLEMENTED.value()), message);
			}
			
			logger.info("Setting attributes for Entry");
			patientEntry.setResourceList(matchedResourceList);
			patientEntry.setSearch(search);
			patientEntry.setResponse(response);
			patientEntryList.add(patientEntry);
			logger.info("Load and execute criteria is completed for all algorithms");
		}
		logger.info("Added entry attributes for Bundle");
		bundle.setEntry(patientEntryList);

		/*
		 * Invoke recordPatientMatchResults method, to insert bundle object into Db
		 */
		recordMap = recordPatientMatchResults(bundle);
		recordMap.entrySet().forEach(recordEntry -> {
            logger.info("Patient Records Succesfully Inserted With ID: " + recordEntry.getKey());
        });
		logger.info("loadAndExecuteCriteria method execution completed");
		return bundle;
	}

	/**
	 * This method will load all the criteria(s) and returns criteria List
	 * 
	 */
	@Override
	public List<Criteria> loadCriteria(String algorithmName) {
		logger.info("Load criteria is started");
		List<Criteria> criteriaList = new ArrayList<>();
		if (isAlgorithmEnabled(algorithmName)) {
			logger.info("Loading criteria for algorithm " + algorithmName);
			String keyForSequence = ApplicationConstants.KEY_FOR_ALGORITHM + ApplicationConstants.DOT + algorithmName
					+ ApplicationConstants.DOT + ApplicationConstants.SEQUENCE_KEY;
			logger.info("Reading key for sequence " + keyForSequence);
			String criteriaSequence = environment.getProperty(keyForSequence);
			logger.info("Doing Null check for the criteria sequence " + criteriaSequence);
			if (doNullCheck(criteriaSequence))  {
				List<String> sequenceList = Arrays.asList(criteriaSequence.split(","));
				logger.info("Criteria Sequnce: " + sequenceList);
				for (String sequence : sequenceList) {
					Criteria criteria = new Criteria();
					logger.info("Reading properties for the sequence: " + sequence);
					logger.info("setting sequence id for criteria" + sequence);
					criteria.setSequenceId(Integer.parseInt(sequence));
					String keyForAttributes = ApplicationConstants.CRITERIA + ApplicationConstants.DOT + sequence
							+ ApplicationConstants.DOT + ApplicationConstants.ATTRIBUTES;
					String keyForConfidence = ApplicationConstants.CRITERIA + ApplicationConstants.DOT + sequence
							+ ApplicationConstants.DOT + ApplicationConstants.CONFIDENCE;
					String criteriaAttributes = environment.getProperty(keyForAttributes);
					logger.info("Doing Null check for the criteria attributes " + criteriaAttributes);
					if (doNullCheck(criteriaAttributes)) {
						List<String> attributes = Arrays.asList(criteriaAttributes.split(","));
						logger.info("Criteria attributes: " + attributes);
						criteria.setAttributes(attributes);
					} else {
						String attbErrorMessage = "Attributes are not defined for criteria sequence " + sequence;
						logger.error(attbErrorMessage);
						throw new CriteriaDefinitionException(attbErrorMessage);
					}
					String criteriaConfidence = environment.getProperty(keyForConfidence);
					logger.info("Doing Null check for the criteria confidence " + criteriaConfidence);
					if (doNullCheck(criteriaConfidence)) {
						ScriptEngineManager mgr = new ScriptEngineManager();
						ScriptEngine engine = mgr.getEngineByName("JavaScript");
						try {
							criteria.setConfidence(String.valueOf(engine.eval(criteriaConfidence)));
							logger.info("Criteria Confidence: " + criteria.getConfidence());
						} catch (ScriptException e) {
							String errMsg = e.getMessage();
							logger.error(errMsg);
							throw new CriteriaDefinitionException(errMsg);
						}
					} else {
						String errorMessage = "Confidence score is not defined for criteria sequence " + sequence;
						logger.error(errorMessage);
						throw new CriteriaDefinitionException(errorMessage);
					}
					DataStores[] dataStores = DataStores.values();
					Map<String, String> sqlQueryMap = new HashMap<String, String>();
					for (DataStores datastore : dataStores) {
						String keyForQuery = ApplicationConstants.CRITERIA + ApplicationConstants.DOT + sequence
								+ ApplicationConstants.DOT + datastore.toString() + ApplicationConstants.DOT
								+ ApplicationConstants.SQLQUERY;
						String criteriaSqlquery = environment.getProperty(keyForQuery);
						if (doNullCheck(criteriaSqlquery)) {
							logger.info("Doing Null check for the criteria Sqlquery " + criteriaSqlquery);
							sqlQueryMap.put(datastore.toString(), criteriaSqlquery);
						} else {
							String attbErrorMessage = "SqlQuery is not defined for datastore " + datastore;
							logger.error(attbErrorMessage);
							throw new CriteriaDefinitionException(attbErrorMessage);
						}

					}
					criteria.setSqlQuery(sqlQueryMap);
					logger.info("Criteria Sql Query: " + criteria.getSqlQuery());
					criteriaList.add(criteria);
					logger.info("Completed Reading properties for the sequence: " + sequence);
				}
			} else {
				String errMsg = "Please define the criteria sequence for algorithm " + algorithmName;
				logger.error(errMsg);
				throw new AlgorithmDefinitionException(errMsg);
			}
			logger.info("Loading criteria for algorithm " + algorithmName + " completed");
		}
		return criteriaList;
	}

	/**
	 * This method validates criteria with Input patient match attributes
	 */
	public void validateCriteria(String inputAlgorithm, PatientResource patientData) {
		logger.info("Validate Criteria method started");
		Map<String, List<Criteria>> allAlgMap = patientMatchAlgorithms.getAlgorithms();

		if (allAlgMap.containsKey(inputAlgorithm)) {
			JsonNode patientInput = jsonNodeData(patientData);
			for (Criteria criteria : allAlgMap.get(inputAlgorithm)) {
				if (!isAttributeExists(criteria.getAttributes(), patientInput)) {
					criteria.setValid(false);
					logger.info(
							"criteria " + criteria.getSequenceId() + "for algorithm " + inputAlgorithm + " is invalid");
				}
			}
		}
		logger.info("Validate Criteria method completed");
	}

	/**
	 * This method will executes all the criteria(s), returns matched resource List
	 * 
	 */
	@Override
	public List<MatchResource> executeCriteria(String algorithmName, PatientResource patient, String contractNumber,
			String dataStore) {
		logger.info("Execute Criteria started for algorithm: "+algorithmName);
		JsonNode patientInput = jsonNodeData(patient);
		List<QueryResponse> queryResponseList = new ArrayList<>();
		List<MatchResource> matchedResourceList = new ArrayList<>();
		
		List<Criteria> criteriaList = patientMatchAlgorithms.getAlgorithms().get(algorithmName);
		logger.info("Iterating all the criterias available in property file");
		for (Criteria criteria : criteriaList) {
			if (!criteria.isValid()) {
				logger.info("Criteria " + criteria.getSequenceId() + " is  not valid");
				continue;
			} else {
				List<String> criteriaAttributes = criteria.getAttributes();
				logger.info("Reading sql query for data store: "+dataStore);
				String sqlQuery = criteria.getSqlQuery().get(dataStore);
				logger.info("Replacing the query attributes in sql query");
				for (String attbKey : criteriaAttributes) {
					logger.info(attbKey);
					String queryAttribute = "<" + attbKey + ">";
					JsonNode jNode = patientInput.get(attbKey);
					String replaceableVal = jNode.asText();
					sqlQuery = sqlQuery.replace(queryAttribute, "'" + replaceableVal + "'");
				}
				logger.info(sqlQuery);
				logger.info("Calling Execute Query to interact with Database");
				queryResponseList = patientDao.executeQuery(sqlQuery, contractNumber);

				if (queryResponseList != null && queryResponseList.size() > 0) {
					logger.info("Patient Match Successful");
					logger.info(queryResponseList.toString());
					Map<String, String> mappedAttributes = new HashMap<>();
					for (String jsonKey : criteriaAttributes) {
						mappedAttributes.put(jsonKey, patientInput.get(jsonKey).asText());
					}
					MatchResource resource = null;
					for (QueryResponse queryResponse : queryResponseList) {
						logger.info("Setting attributes for MatchResource Model");
						resource = new MatchResource();
						resource.setIdentifier(queryResponse.getPatientID());
						resource.setMasterId(queryResponse.getMasterID());
						resource.setDateTime(LocalDateTime.now()); 
						resource.setScore(Integer.parseInt(criteria.getConfidence()));
						resource.setSourceName(queryResponse.getSourceName());
						resource.setAttributeList(mappedAttributes);
						logger.info("Query response data is  added to resource sucessfully");
						matchedResourceList.add(resource);
					}

					break;
				}
			}
		}

		logger.info("Execute criteria completed");
		return matchedResourceList;
	}

	/**
	 * This method will prepare SQL insert query with appropriate patientOutput
	 * response Object
	 * 
	 */
	@Override
	public Map<Integer, PatientMatchResult> recordPatientMatchResults(Bundle bundle) {
		logger.info("recordPatientMatchResults method execution started");

		String keyForSqlInsert = ApplicationConstants.PATIENT + ApplicationConstants.DOT + ApplicationConstants.MATCH
				+ ApplicationConstants.DOT + ApplicationConstants.RESPONSE + ApplicationConstants.DOT
				+ ApplicationConstants.SAVE + ApplicationConstants.DOT + ApplicationConstants.SQLQUERY;

		String sqlQuery = environment.getProperty(keyForSqlInsert);

		PatientMatchResult matchResult = null;
		ObjectMapper mapper = new ObjectMapper();
		String jsonMapInStr = null;
		Map<Integer, PatientMatchResult> recordMap = new HashMap<>();

		try {
			for (Entry entry : bundle.getEntry()) {

				matchResult = new PatientMatchResult();
				matchResult.setAlgorithmName(entry.getSearch().getAlgorithmName());
				matchResult.setDataStore(entry.getSearch().getDataStore());
				matchResult.setApplicationName(entry.getSearch().getApplicationName());

				String responseStatus = entry.getResponse().getStatus();
				int status = Integer.parseInt(responseStatus);
				if (status == HttpStatus.OK.value()) {
					for (MatchResource resource : entry.getResourceList()) {
						matchResult.setPatientID(resource.getIdentifier());
						matchResult.setMasterPatientID(resource.getMasterId());
						matchResult.setScore(resource.getScore());
						matchResult.setSourceName(resource.getSourceName());
						matchResult.setCreateDateTime(resource.getDateTime());
						jsonMapInStr = mapper.writeValueAsString(resource.getAttributeList());
						matchResult.setPatientAttribute(jsonMapInStr);
						logger.info("Result Set with Values : " + matchResult.toString());
						matchResult = patientDao.savePatientMatchRecords(sqlQuery, matchResult);
						recordMap.put(matchResult.getId(), matchResult);
						logger.info("New Patient Record Inserted with ID: " + matchResult.getId());
					}
				} else if (status == HttpStatus.NOT_FOUND.value()) {
					matchResult.setPatientID(null);
					matchResult.setMasterPatientID(null);
					matchResult.setScore(null); 
					matchResult.setSourceName(null);
					matchResult.setCreateDateTime(LocalDateTime.now()); 
					matchResult.setPatientAttribute(null);

					logger.info("Result Set with Values : " + matchResult.toString());
					matchResult = patientDao.savePatientMatchRecords(sqlQuery, matchResult);
					recordMap.put(matchResult.getId(), matchResult);
					logger.info("New Patient Record Inserted with ID: " + matchResult.getId());
				}
			}
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		logger.info("insertPatientMatchRecords method execution completed");
		return recordMap;
	}

	/**
	 * This method converts patient resource POJO to JSON data, returns JSON Object
	 * 
	 */
	public JsonNode jsonNodeData(PatientResource patient) {
		logger.info("JSON data binding started");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = null;
		try {
			String json = mapper.writeValueAsString(patient);
			jsonObj = mapper.readTree(json);
			logger.info("converted PatientData to JSON object Successfully");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		logger.info("JSON data binding completed");
		return jsonObj;
	}

	/**
	 * This method checks whether String is empty or null
	 */
	private boolean doNullCheck(String strObj) {
		logger.info("doNullCheck method started for " + strObj);
		boolean isValid = true;
		if (strObj == null || strObj.trim().isEmpty() || strObj.trim().equalsIgnoreCase("null")) {
			isValid = false;
		}
		logger.info("doNullCheck method completed for " + strObj + " and its result is " + isValid);
		return isValid;

	}

	/*
	 * This method validates whether an Algorithm is Enabled or not
	 */

	public boolean isAlgorithmEnabled(String algorithmName) {
		logger.info("Validating Algorithm Enabled for " + algorithmName);
		String algorithmEnabledKey = environment.getProperty(ApplicationConstants.KEY_FOR_ALGORITHM
				+ ApplicationConstants.DOT + algorithmName + ApplicationConstants.DOT + ApplicationConstants.ENABLED);
		boolean isEnabled = Boolean.valueOf(algorithmEnabledKey);
		logger.info("is Algorithm enabled: " + isEnabled);
		return isEnabled;
	}
	/*
	 * This method validates whether an Algorithm Exists or not
	 */

	public boolean isAlgorithmExists(String algorithmName) {
		boolean isExist = false;
		logger.info("Validating Algorithm Exists for " + algorithmName);
		String algorithmExistsKey = environment.getProperty(ApplicationConstants.KEY_FOR_ALGORITHMS);
		List<String> algorithmsList = Arrays.asList(algorithmExistsKey.split(","));
		isExist = algorithmsList.contains(algorithmName);
		logger.info("is Algorithm Exists: " + isExist);
		return isExist;
	}

	/*
	 * This method validates whether a datastore Exists or not
	 */
	public boolean isDataStoreExists(String dataStore) {
		boolean isValid = false;
		logger.info("Validating DataStore for " + dataStore);

		if (dataStore.equals(DataStores.CONTACTENTITIES.toString())) {
			isValid = true;
		} else if (dataStore.equals(DataStores.PATIENTMPI.toString())) {
			isValid = true;
		}
		logger.info("DataStore ("+dataStore+") is valid: "+ isValid);
		return isValid;
	}
	
	/**
	 * This method is for setting search attributes
	 */
	public Search setSearchAttributes(String algorithmData, String ApplicationName, String dataStore) {
		logger.info("Setting Attributes for Search");
		Search search = new Search();
		search.setAlgorithmName(algorithmData);
		search.setApplicationName(ApplicationName);
		search.setDataStore(dataStore);
		logger.info("Setting Attributes for search completed");
		return search;
	}

	/**
	 * This method is for setting response attributes
	 */
	public Response setResponseAttributes(String status, String message) {
		logger.info("Setting Attributes for Response");
		Response response = new Response();
		response.setStatus(status);
		response.setMessage(message);
		logger.info("Setting Attributes for Response completed");
		return response;
	}

	public boolean isAttributeExists(List<String> criteriaAttributes, JsonNode patientInput) {
		boolean isExists = true;
		logger.info("Validating Attributes Exists: "+ criteriaAttributes);
		for (String inputAttribute : criteriaAttributes) {
			if (! patientInput.has(inputAttribute)) {
				isExists = false;
				logger.info("Attribute " + inputAttribute + "does not exists");
				break;
			} else {
				JsonNode jNodeInputValue = patientInput.get(inputAttribute);
				String inputAttributeValue = jNodeInputValue.asText();
				if (jNodeInputValue instanceof NullNode || inputAttributeValue == null
						|| inputAttributeValue.isEmpty()) {
					isExists = false;
					logger.info("Attribute " + inputAttribute + "should not be null or empty");
					break;
				}
			}
		}
		logger.info("is Attribute Exists: " + isExists);
		return isExists;
	}

}
