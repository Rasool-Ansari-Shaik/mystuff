/*******************************************************************************
 * Watson Health Imaging Analytics
 *
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2020
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);
	
	/**
	 * 
	 * @param tableName
	 * @return configData
	 * @throws SQLException
	 */
	public String getConfigData(String tableName) throws SQLException {

		LOGGER.debug("getConfigData() method started");
		Connection con = null;
		ResultSet rs = null;
		Statement stmt = null;
		String selectQuery = "";
		String driverName = PropertyUtil.getProperty("db2.drivername");
		String dbURL = PropertyUtil.getProperty("db2.driverurl");
		String dbUser = PropertyUtil.getProperty("db2.username");
		String dbPassword = PropertyUtil.getProperty("db2.password");
		String configData = null;
		//If tableName is tenant get the selectQuery of contract.
		if (tableName.equalsIgnoreCase("tenant")) {
			selectQuery = PropertyUtil.getProperty("contractconfig.selectquery");

		}
		//If tableName is location get the selectQuery of locationConfig.
		else if (tableName.equalsIgnoreCase("location")) {
			selectQuery = PropertyUtil.getProperty("locationconfig.selectquery");

		}
		//If tableName is location get the selectQuery of practitionerConfig.
		else if (tableName.equalsIgnoreCase("practitioner")) {
			selectQuery = PropertyUtil.getProperty("practitionerconfig.selectquery");

		}
		//If tableName is location get the selectQuery of careGapConfig.
		else if (tableName.equalsIgnoreCase("caregap")) {
			selectQuery = PropertyUtil.getProperty("caregapconfig.selectquery");
		}

		if (selectQuery != null && !selectQuery.isEmpty()) {

			try {
				Class.forName(driverName);
				LOGGER.info("Loaded the JDBC driver");
				con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
				con.setAutoCommit(false);
				stmt = con.createStatement();
				rs = stmt.executeQuery(selectQuery);
				while (rs.next() && rs.getString(1) != null && !rs.getString(1).isEmpty()) {
					configData = rs.getString(1);
				}
				con.commit();
				LOGGER.info("Transaction committed");
			} catch (Exception ex) {
				LOGGER.error("Exception: " + ex.getMessage());
			} 

		}
		LOGGER.debug("getConfigData() method completed");
		return configData;
	}
}
