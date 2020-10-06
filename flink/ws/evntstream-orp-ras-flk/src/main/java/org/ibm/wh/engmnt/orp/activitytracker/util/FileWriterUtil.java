/*******************************************************************************
 *  * Watson Health Imaging Analytics
 *  *
 *  * IBM Confidential
 *  *
 *  * OCO Source Materials
 *  *
 *  * (C) Copyright IBM Corp. 2020
 *  *
 *  * The source code for this program is not published or otherwise
 *  * divested of its trade secrets, irrespective of what has been
 *  * deposited with the U.S. Copyright Office.
 *******************************************************************************/
package org.ibm.wh.engmnt.orp.activitytracker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ibm.wh.engmnt.orp.activitytracker.model.CadfEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileWriterUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(FileWriterUtil.class);

	public static void writeToFile(List<CadfEvent> cadfEvents, String path) {
		LOGGER.info("Enterd into writeToFile().....");
		File dir = new File(path);
		if (!dir.isDirectory()) {
			LOGGER.info("Directory is creating.......");
			dir.mkdirs();
			LOGGER.info("Directory has been created :: {}", dir.exists());
			LOGGER.info("Absolute path of directory :::: {}", dir.getAbsolutePath());
		}
		LOGGER.info("Creating file inside the directory :: {}", "cadf.log");
		File file = new File(dir, "cadf.log");
		LOGGER.info("File has been created :: {}", file.exists());
		LOGGER.info("Absolute path of file is :: {}", file.getAbsolutePath());
		FileWriter fr = null;
		BufferedWriter bw = null;
		try {
			LOGGER.info("check file ::: " + file.exists());
			LOGGER.info("***** Is Dir: " + file.isDirectory());
			fr = new FileWriter(file, true);
			bw = new BufferedWriter(fr);

			for (CadfEvent event : cadfEvents) {
				String data = JsonEncoder.encode(event);
				bw.write(data);
				bw.newLine();
			}

		} catch (IOException e) {
			LOGGER.error("", e);
		} finally {
			try {
				bw.close();
				fr.close();
			} catch (IOException e) {
				LOGGER.error("", e);
			}
		}
	}

}