package com.phytel.patient.match.exceptions;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.phytel.patient.match.model.Criteria;
import com.phytel.patient.match.model.PatientMatchAlgorithms;
import com.phytel.patient.match.model.PatientResource;
import com.phytel.patient.match.service.PatientMatchServiceImpl;
import com.phytel.patient.match.util.ApplicationConstants;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientMatchExceptionTest {
	
	@Autowired
	private PatientMatchServiceImpl patientService;
	@Autowired
	private Environment environment;
	
	@Test
	public void testForAlgorithmDefinitionException() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String keyForSequence = ApplicationConstants.KEY_FOR_ALGORITHM + ApplicationConstants.DOT + "discharge"
				+ ApplicationConstants.DOT + ApplicationConstants.SEQUENCE_KEY;
		String criteriaSequence = environment.getProperty(keyForSequence);
		Method method = PatientMatchServiceImpl.class.getDeclaredMethod("doNullCheck", String.class);
		method.setAccessible(true);
		Boolean result = (java.lang.Boolean) method.invoke(patientService, criteriaSequence);
		if (!result) {
			Assertions.assertThatExceptionOfType(AlgorithmDefinitionException.class).isThrownBy(() -> {
				patientService.loadCriteria("discharge");
			});
		} else {
			Assert.assertEquals("1,3", criteriaSequence);
		}
	}

	@Test
	public void testForAttributesCriteriaDefinationException() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String keyForAttributes = ApplicationConstants.CRITERIA + ApplicationConstants.DOT + "1"
				+ ApplicationConstants.DOT + ApplicationConstants.ATTRIBUTES;
		String criteriaAttributes = environment.getProperty(keyForAttributes);
		Method method = PatientMatchServiceImpl.class.getDeclaredMethod("doNullCheck", String.class);
		method.setAccessible(true);
		Boolean result = (java.lang.Boolean) method.invoke(patientService, criteriaAttributes);
		if (!result) {
			Assertions.assertThatExceptionOfType(CriteriaDefinitionException.class).isThrownBy(() -> {
				patientService.loadCriteria("discharge");
			});
		} else {
			Assert.assertEquals("patient.sourceId,patient.identifier", criteriaAttributes);
		}
	}

	@Test
	public void testForConfidenceCriteriaDefinationException() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String keyForConfidence = ApplicationConstants.CRITERIA + ApplicationConstants.DOT + "1"
				+ ApplicationConstants.DOT + ApplicationConstants.CONFIDENCE;
		String confidence = environment.getProperty(keyForConfidence);
		Method method = PatientMatchServiceImpl.class.getDeclaredMethod("doNullCheck", String.class);
		method.setAccessible(true);
		Boolean result = (java.lang.Boolean) method.invoke(patientService, confidence);
		if (!result) {
			Assertions.assertThatExceptionOfType(CriteriaDefinitionException.class).isThrownBy(() -> {
				patientService.loadCriteria("discharge");
			});
		} else {
			Assert.assertEquals("90+10", confidence);
		}
	}

	@Test
	public void testForSqlQueryCriteriaDefinationException() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String keyForQuery = ApplicationConstants.CRITERIA + ApplicationConstants.DOT + "1" + ApplicationConstants.DOT
				+ "contactEntities" + ApplicationConstants.DOT + ApplicationConstants.SQLQUERY;
		String sqlQuery = environment.getProperty(keyForQuery);
		String expected = "SELECT [ID] as PatientId,[MasterID] as MasterId,[SendingApplication] as SourceName FROM [dbo].[vw_PatientMatchMaxPlusOne] WHERE ExternalPatientId=<patient.identifier> and SendingApplication=<patient.sourceId>";
		Method method = PatientMatchServiceImpl.class.getDeclaredMethod("doNullCheck", String.class);
		method.setAccessible(true);
		Boolean result = (java.lang.Boolean) method.invoke(patientService, sqlQuery);
		if (!result) {
			Assertions.assertThatExceptionOfType(CriteriaDefinitionException.class).isThrownBy(() -> {
				patientService.loadCriteria("discharge");
			});
		} else {
			Assert.assertEquals(expected, sqlQuery);
		}
	}

	@Test
	public void testForAlgorithmExists() {
		boolean result = patientService.isAlgorithmExists("discharge");
		Assert.assertEquals(result, true);
	}

	@Test
	public void testForAlgorithmEnabled() {
		boolean result = patientService.isAlgorithmEnabled("discharge");
		Assert.assertEquals(result, true);
	}

	@Test
	public void testForDataStoreExists() {
		boolean result = patientService.isDataStoreExists("contactEntities");
		Assert.assertEquals(result, true);
	}
	
	@Test
	public void testForAttributeExists() {
		Criteria criteria = new Criteria();
		String keyForAttributes = ApplicationConstants.CRITERIA + ApplicationConstants.DOT + "1"
				+ ApplicationConstants.DOT + ApplicationConstants.ATTRIBUTES;
		String criteriaAttributes = environment.getProperty(keyForAttributes);
		List<String> attributes = Arrays.asList(criteriaAttributes.split(","));
		criteria.setAttributes(attributes);
		PatientResource patientData = new PatientResource();
		patientData.setIdentifier("2A83D826-AB97-4249-A466-376C424E3061");
		patientData.setSubscriber("abcd");
		patientData.setGiven("String");
		patientData.setFamily("Date");
		patientData.setFinitial("Norman");
		patientData.setSsn("Wiegmann");
		patientData.setPostalCode("000380078");
		patientData.setPhoneNumber("String");
		patientData.setBirthDate("Date");
		patientData.setSourceId("NGEN");
		patientData.setApplicationName("CI");
		patientData.setPayerName("JLT");
		JsonNode patientInput = patientService.jsonNodeData(patientData);
		boolean result = patientService.isAttributeExists(attributes, patientInput);
		Assert.assertEquals(result, true);
	}
	
	@Test
	public void testForValidateCriteria() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		patientService.initAlgorithms();
		PatientResource patientData = new PatientResource();
		patientData.setSubscriber("abcd");
		patientData.setGiven("String");
		patientData.setFamily("Date");
		patientData.setFinitial("Norman");
		patientData.setSsn("Wiegmann");
		patientData.setPostalCode("000380078");
		patientData.setPhoneNumber("String");
		patientData.setBirthDate("Date");
		patientData.setApplicationName("CI");
		patientData.setPayerName("JLT");
		patientService.validateCriteria("discharge", patientData);
		Field field = PatientMatchServiceImpl.class.getDeclaredField("patientMatchAlgorithms");
		field.setAccessible(true);
		PatientMatchAlgorithms pmasAlgorithms = (PatientMatchAlgorithms) field.get(patientService);
		Criteria criteria = pmasAlgorithms.getAlgorithms().get("discharge").get(0);
		Assert.assertEquals(false, criteria.isValid());
	}

	@Test
	public void testForNullCheck() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method method = PatientMatchServiceImpl.class.getDeclaredMethod("doNullCheck", String.class);
		method.setAccessible(true);
		Boolean result = (java.lang.Boolean) method.invoke(patientService, "null");
		Assert.assertEquals(result, false);
	}

}
