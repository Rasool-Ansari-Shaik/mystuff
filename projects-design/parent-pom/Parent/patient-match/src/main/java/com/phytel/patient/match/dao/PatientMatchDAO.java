package com.phytel.patient.match.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.phytel.patient.match.exceptions.SqlQueryExecutionException;
import com.phytel.patient.match.model.PatientMatchResult;
import com.phytel.patient.match.model.QueryResponse;
import com.phytel.patient.match.util.ApplicationConstants;

/**
 * This is DAO Class, which interacts with Database
 *
 */
@Repository
public class PatientMatchDAO {
	
	private static Logger logger = LoggerFactory.getLogger(PatientMatchDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * This method will query the Database
	 *
	 */
	public List<QueryResponse> executeQuery(String sqlQuery, String contractNumber) {
		logger.info("Execute query started");
		List<QueryResponse> finalQueryResponsesList = new ArrayList<>();
		logger.info("Connecting with specific database for do patient match with sql query");
		finalQueryResponsesList = jdbcTemplate.query(sqlQuery, (ResultSet rs) -> setQueryResponse(rs));
		logger.info("Query executed successfully");
		return finalQueryResponsesList;
	}
	
	
	private List<QueryResponse> setQueryResponse(ResultSet rs) {

		List<QueryResponse> queryResponseList = new ArrayList<>();
		try {
			while (rs.next()) {
				QueryResponse queryResponse = new QueryResponse();
				logger.info("Setting Query Response");
				queryResponse.setPatientID(rs.getInt(ApplicationConstants.PATIENTID));
				queryResponse.setSourceName(rs.getString(ApplicationConstants.SOURCENAME));
				String masterId = rs.getString(ApplicationConstants.MASTERID);
				if (masterId == null) {
					queryResponse.setMasterID(null);
				} else {
					queryResponse.setMasterID(Integer.parseInt(rs.getString(ApplicationConstants.MASTERID)));
				}
				queryResponseList.add(queryResponse);
				logger.info("Successfully added query Response");
			}
		} catch (NumberFormatException | SQLException nfe) {
			logger.error(nfe.getMessage());
			throw new SqlQueryExecutionException(nfe.getMessage());
		}
		return queryResponseList;
	}

	/**
	 * This method will insert the values into the Database
	 *
	 */
	public PatientMatchResult savePatientMatchRecords(String sqlQuery, PatientMatchResult patientMatch) {
		logger.info("savePatientMatchRecords method execution started");

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update((Connection con) -> {
			PreparedStatement statement = null;
			try {
				statement = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
				if (patientMatch.getPatientID() != null)
					statement.setInt(1, patientMatch.getPatientID());
				else
					statement.setNull(1, Types.INTEGER);

				if (patientMatch.getMasterPatientID() != null)
					statement.setInt(2, patientMatch.getMasterPatientID());
				else
					statement.setNull(2, Types.INTEGER);

				if (patientMatch.getScore() != null)
					statement.setInt(3, patientMatch.getScore());
				else
					statement.setNull(3, Types.NULL);

				if (patientMatch.getSourceName() != null)
					statement.setString(4, patientMatch.getSourceName());
				else
					statement.setNull(4, Types.NULL);

				if (patientMatch.getPatientAttribute() != null)
					statement.setString(5, patientMatch.getPatientAttribute());
				else
					statement.setNull(5, Types.NULL);

				if (patientMatch.getAlgorithmName() != null)
					statement.setString(6, patientMatch.getAlgorithmName());
				else
					statement.setNull(6, Types.NULL);

				if (patientMatch.getDataStore() != null)
					statement.setString(7, patientMatch.getDataStore());
				else
					statement.setNull(7, Types.NULL);

				if (patientMatch.getDataStore() != null)
					statement.setString(8, patientMatch.getApplicationName());
				else
					statement.setNull(8, Types.NULL);

				statement.setString(9, patientMatch.getCreateDateTime().toString());
			} catch (SQLException e) {
				logger.error(e.getMessage());
				throw new SqlQueryExecutionException(e.getMessage());
			}
			return statement;
		}, keyHolder);

		int insertedPatientId = keyHolder.getKey().intValue();
		patientMatch.setId(insertedPatientId);

		logger.info("savePatientMatchRecords method execution completed");
		return patientMatch;
	}
}
