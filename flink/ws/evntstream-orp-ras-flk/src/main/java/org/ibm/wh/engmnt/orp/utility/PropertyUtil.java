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

import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
	private static Properties props;
	static {
		try {
			InputStream is = PropertyUtil.class.getClassLoader().getResourceAsStream("config.properties");
			props = new Properties();

			if (is != null) {
				props.load(is);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public static String getProperty(String property) {
		return props.getProperty(property);
	}

	public static String setProperty(String key, String value) {
		return (String) props.setProperty(key, value);
	}
}
