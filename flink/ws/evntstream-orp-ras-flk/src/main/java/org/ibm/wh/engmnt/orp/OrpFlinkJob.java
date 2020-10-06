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
package org.ibm.wh.engmnt.orp;

import java.util.List;
import java.util.Properties;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.ibm.wh.engmnt.orp.activitytracker.model.CadfEvent;
import org.ibm.wh.engmnt.orp.activitytracker.util.CadfEventGen;
import org.ibm.wh.engmnt.orp.activitytracker.util.FileWriterUtil;
import org.ibm.wh.engmnt.orp.ops.EventStreamOps;
import org.ibm.wh.engmnt.orp.service.ObservationService;
import org.ibm.wh.engmnt.orp.service.impl.ObservationServiceImpl;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OrpFlinkJob
 *
 */
public class OrpFlinkJob {
	private final static Logger LOGGER = LoggerFactory.getLogger(OrpFlinkJob.class);

	public static void main(String[] args) throws Exception {
		LOGGER.debug("Main method started");
		String eventStreamspwd = args[0];
		String inputTopic = PropertyUtil.getProperty("cohort.eventstream.topic.notification");

		EventStreamOps eventStreamOps = new EventStreamOps();
		Properties props = eventStreamOps.eventStreamProperties(eventStreamspwd);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		Long startTime = System.currentTimeMillis();
		List<CadfEvent> listCadf = CadfEventGen.gen("OrpFlinkJobStartTime", startTime.toString());
		FileWriterUtil.writeToFile(listCadf, "./var/wffh/at");
		LOGGER.info("Flink Consumer (input-topic) Start Time: " + startTime);
		FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(inputTopic, new SimpleStringSchema(), props);
		consumer.setStartFromEarliest();
		LOGGER.info("Reading the Observation Msg from the Cohort input topic ");
		DataStream<String> stream = env.addSource(consumer);
		stream.map(new OrpMapFunction());
		LOGGER.debug("Main method execution completed");
		env.execute();
	}
}

class OrpMapFunction extends RichMapFunction<String, Void> {

	private static final long serialVersionUID = 1L;

	@Override
	public Void map(String value) throws Exception {

		ObservationService observationService = new ObservationServiceImpl();

		observationService.processObservation(value);

		return null;
	}

}
