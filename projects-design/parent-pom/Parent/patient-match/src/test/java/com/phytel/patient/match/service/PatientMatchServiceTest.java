package com.phytel.patient.match.service;

import java.io.IOException;
import java.util.List;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.phytel.patient.match.model.Bundle;
import com.phytel.patient.match.model.Entry;
import com.phytel.patient.match.model.MatchResource;
import com.phytel.patient.match.model.PatientResource;
//import com.phytel.patient.match.model.Response;
import com.phytel.patient.match.model.Response;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientMatchServiceTest {

	@Autowired
	private PatientMatchServiceImpl patientService;

	@Test
	public void testExecuteCriteria() throws ScriptException, IOException {

		PatientResource patientData = new PatientResource();
		patientData.setIdentifier("2A83D826-AB97-4249-A466-376C424E3061");
		patientData.setSubscriber("abcd");
		patientData.setGiven("String");
		patientData.setFamily("Date");
		patientData.setFinitial("Norman");
		patientData.setSsn("Wiegmann");
		patientData.setPostalCode("000380078");
		patientData.setPhoneNumber("String");
		patientData.setBirthDate("1955-10-21");
		patientData.setSourceId("NGEN");
		patientData.setApplicationName("CI");
		patientData.setPayerName("JLT");
		Bundle bundle = new Bundle();
		bundle = patientService.validateAndExecuteCriteria(patientData, "Apollo", "contactEntities", "discharge");
		Assert.assertEquals("NGEN", bundle.getEntry().get(0).getResourceList().get(0).getSourceName().toString());
	}

	@Test
	public void testExecuteCriteriaForMasterId() throws ScriptException, IOException {

		PatientResource patientData = new PatientResource();
		patientData.setIdentifier("2A83D826-AB97-4249-A466-376C424E3061");
		patientData.setSubscriber("abcd");
		patientData.setGiven("String");
		patientData.setFamily("Date");
		patientData.setFinitial("Norman");
		patientData.setSsn("Wiegmann");
		patientData.setPostalCode("000380078");
		patientData.setPhoneNumber("String");
		patientData.setBirthDate("1955-10-21");
		patientData.setSourceId("NGEN");
		patientData.setApplicationName("CI");
		patientData.setPayerName("JLT");
		Bundle bundle = new Bundle();
		bundle = patientService.validateAndExecuteCriteria(patientData, "Apollo", "contactEntities", "discharge");
		Assert.assertNotEquals("CI", bundle.getEntry().get(0).getResourceList().get(0).getSourceName().toString());
	}
	
	@Test
	public void testExecuteCriteriaForMultipleAlgs() throws ScriptException, IOException {

		PatientResource patientData = new PatientResource();
		patientData.setIdentifier("2A83D826-AB97-4249-A466-376C424E3061");
		patientData.setSubscriber("abcd");
		patientData.setGiven("Elnora");
		patientData.setFamily("Ogwin");
		patientData.setFinitial("E");
		patientData.setSsn("000884021");
		patientData.setPostalCode("92064");
		patientData.setPhoneNumber("String");
		patientData.setBirthDate("1955-10-21");
		patientData.setSourceId("NGEN");
		patientData.setApplicationName("CI");
		patientData.setPayerName("JLT");
		Bundle bundle = new Bundle();
		bundle = patientService.validateAndExecuteCriteria(patientData, "Apollo", "contactEntities", "*");
		Assert.assertEquals("NGEN", bundle.getEntry().get(0).getResourceList().get(0).getSourceName().toString());
	}

	@Test
	public void testForNoMatch() {
		PatientResource patientData = new PatientResource();
		patientData.setIdentifier("2A83D826-AB97-4249-A4");
		patientData.setSubscriber("abcdfg");
		patientData.setGiven("Strings");
		patientData.setFamily("Dat");
		patientData.setFinitial("Normannn");
		patientData.setSsn("948AD721-78D3-4E9C-A579-8");
		patientData.setPostalCode("003456735");
		patientData.setPhoneNumber("123456789");
		patientData.setBirthDate("1957-08-08 00:00:00.000");
		patientData.setSourceId("NG");
		patientData.setApplicationName("CI");
		patientData.setPayerName("JLT");
		Bundle bundle = new Bundle();
		bundle = patientService.validateAndExecuteCriteria(patientData, "Apollo", "contactEntities", "claims");
		List<Entry> entries = bundle.getEntry();
		for (Entry entry : entries) {
			List<MatchResource> matchedResource = entry.getResourceList();
			Response response = entry.getResponse();
			String status = response.getStatus();
			if (matchedResource.isEmpty() || matchedResource == null) {
				Assert.assertEquals("404", status);
			}
		}
	}
	
	@Test
	public void testNoMatch() {
		PatientResource patientData = new PatientResource();
		patientData.setIdentifier("2A83D826-AB97-4249-A4");
		patientData.setSubscriber("abcdfg");
		patientData.setGiven("Strings");
		patientData.setFamily("Dat");
		patientData.setFinitial("Normannn");
		patientData.setSsn("948AD721-78D3-4E9C-A579-8");
		patientData.setPostalCode("003456735");
		patientData.setPhoneNumber("123456789");
		patientData.setBirthDate("1957-08-08 00:00:00.000");
		patientData.setSourceId("NG");
		patientData.setApplicationName("CI");
		patientData.setPayerName("JLT");
		Bundle bundle = new Bundle();
		bundle = patientService.validateAndExecuteCriteria(patientData, "Apollo", "contactEntities", "claims");
		List<Entry> entries = bundle.getEntry();
		for (Entry entry : entries) {
			List<MatchResource> matchedResource = entry.getResourceList();
			Response response = entry.getResponse();
			String status = response.getStatus();
			if (matchedResource.isEmpty() || matchedResource == null) {
				Assert.assertNotEquals("200", status);
			}
		}
	}
	
	/*
	@Test
	public void testForRecordPatientMatchResultsForNoMatch() {
		Entry entry = new Entry();
		Bundle bundle = new Bundle();
		MatchResource resource = new MatchResource();
		Search search = new Search();
		Response response = new Response();
		List<MatchResource> matchedResourceList = new ArrayList<>();
		List<Entry> patientEntryList = new ArrayList<>();
		Map<Integer, PatientMatchResult> recordMap = new HashMap<>();
		
		resource.setIdentifier(null);
		resource.setMasterId(null);
		resource.setScore(30);
		resource.setSourceName(null);
		resource.setAttributeList(null);
		resource.setDateTime(new Date().toString());
		
		matchedResourceList.add(resource);
		entry.setResourceList(matchedResourceList);
		search.setAlgorithmName("discharge");
		search.setDataStore("contactEntities");
		search.setApplicationName("DS");
		entry.setSearch(search);
		response.setStatus("404");
		response.setMessage("NOT_FOUND");
		entry.setResponse(response);
		patientEntryList.add(entry);
		bundle.setEntry(patientEntryList);
		
		recordMap = patienService.recordPatientMatchResults(bundle);
		Assert.assertTrue(recordMap.containsKey(54));
		Assert.assertEquals(1, recordMap.size());
	}
	
	@Test
	public void testForRecordPatientMatchResultsForMultipleEntryObjects() {
		Entry entry = new Entry();
		Bundle bundle = new Bundle();
		MatchResource resource = new MatchResource();
		Map<String, String> attributeMap = new HashMap<String, String>();
		Search search = new Search();
		Response response = new Response();
		List<MatchResource> matchedResourceList = new ArrayList<>();
		List<Entry> patientEntryList = new ArrayList<>();
		
		Map<Integer, PatientMatchResult> recordMap = new HashMap<>();
		
		resource.setIdentifier(20912);
		resource.setMasterId(null);
		resource.setScore(100);
		resource.setSourceName("NGEN");
		
		attributeMap.put("patient.identifier", "2A83D826-AB97-4249-A466-376C424E3061");
		attributeMap.put("patient.sourceId", "NGEN");
		
		resource.setAttributeList(attributeMap);
		resource.setDateTime(new Date().toString());
		
		matchedResourceList.add(resource);
		
		entry.setResourceList(matchedResourceList);
		
		search.setAlgorithmName("discharge");
		search.setDataStore("contactEntities");
		search.setApplicationName("CI");
		
		entry.setSearch(search);
		response.setStatus("200");
		response.setMessage("OK");
		
		entry.setResponse(response);
		
		patientEntryList.add(entry);
		
		resource = new MatchResource();
		
		resource.setIdentifier(230610);
		resource.setMasterId(null);
		resource.setScore(80);
		resource.setSourceName("NGEN");
		
		attributeMap = new HashMap<String, String>();
		
		attributeMap.put("patient.birthDate", "1955-10-21");
		attributeMap.put("patient.given", "Elnora");
		attributeMap.put("patient.ssn", "000884021");
		attributeMap.put("patient.family", "Ogwin");
		attributeMap.put("patient.postalCode", "92064");
		
		resource.setAttributeList(attributeMap);
		resource.setDateTime(new Date().toString());
		
		matchedResourceList = new ArrayList<>();
		matchedResourceList.add(resource);
		
		entry = new Entry();
		entry.setResourceList(matchedResourceList);
		
		search = new Search();
		search.setAlgorithmName("claims");
		search.setDataStore("contactEntities");
		search.setApplicationName("CI");
		
		entry.setSearch(search);
		
		response = new Response();
		
		response.setStatus("200");
		response.setMessage("OK");
		
		entry.setResponse(response);
		
		patientEntryList.add(entry);
		
		bundle.setEntry(patientEntryList);
		
		recordMap = patienService.recordPatientMatchResults(bundle);
		
		Assert.assertTrue(recordMap.containsKey(63));
		Assert.assertEquals(2, recordMap.size());
		
	}
	*/

}
