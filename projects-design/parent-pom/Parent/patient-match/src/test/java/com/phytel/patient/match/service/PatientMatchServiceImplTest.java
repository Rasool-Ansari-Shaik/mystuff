package com.phytel.patient.match.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.phytel.patient.match.dao.PatientMatchDAO;
import com.phytel.patient.match.model.MatchResource;
import com.phytel.patient.match.model.PatientResource;
import com.phytel.patient.match.model.QueryResponse;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PatientMatchServiceImplTest {

	@Autowired
	private PatientMatchServiceImpl patientService;

	@MockBean
	private PatientMatchDAO patientDao;

	private PatientResource patientData;

	@Before
	public void setUp() {
		patientService.initAlgorithms();
	}

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	/* Mock response for executeQuery DAO */
	public void mockPatientDao(List<QueryResponse> queryResponseList) {

		Mockito.when(patientDao.executeQuery(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(queryResponseList);
	}

	@Test
	public void testMaxPlusOne_ExecuteCriteria_Success() {
		List<MatchResource> matchedResourceList = new ArrayList<>();

		/* Generate queryResponse for patientDao.executeCriteria */
		QueryResponse queryResponse = new QueryResponse();
		queryResponse.setPatientID(12345);
		queryResponse.setMasterID(1);
		queryResponse.setSourceName("NGEN");
		List<QueryResponse> queryResponseList = new ArrayList<>();
		queryResponseList.add(queryResponse);

		/* Add response to PatientDao mock bean */
		mockPatientDao(queryResponseList);

		patientData = new PatientResource();
		patientData.setIdentifier("26CED113-E3E3-45D5-85E7-1DF70628DF81");
		patientData.setSourceId("NGEN");

		/* Call patientService executeCriteria method */
		matchedResourceList = patientService.executeCriteria("discharge", patientData, "Apollo", "contactEntities");

		Assert.assertEquals("NGEN", matchedResourceList.get(0).getSourceName().toString());
		Assert.assertEquals(12345, matchedResourceList.get(0).getIdentifier().intValue());
	}

	@Test
	public void testMaxPlusOne_ValidateCriteria() {

		patientData = new PatientResource();
		patientData.setIdentifier("26CED113-E3E3-45D5-85E7-1DF70628DF81");
		patientData.setSourceId("NGEN");
		/* Call patientService validateCriteria method */
		patientService.validateCriteria("discharge", patientData);

	}

	@Test
	public void testMaxPlusOne_LoadCriteria_AlgDefException() {
		exceptionRule.expect(Exception.class);
		exceptionRule.expectMessage("Please define the criteria sequence for algorithm nocriteria");
		/* Pass Algorithm with no criteria sequence defined */
		patientService.loadCriteria("nocriteria");

	}
}
