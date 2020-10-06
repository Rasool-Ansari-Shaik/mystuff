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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ibm.wh.engmnt.orp.activitytracker.model.*;
import org.ibm.wh.engmnt.orp.utility.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CadfEventGen {
	private final static Logger LOGGER = LoggerFactory.getLogger(CadfEventGen.class);
	public static List<CadfEvent> gen(String inputKey, String inputvalue) {
		CadfAction.Action[] cadfActions = CadfAction.Action.values();
		int numberOfActions = cadfActions.length;
		CadfEvent.Outcome[] outcomes = CadfEvent.Outcome.values();
		int numberOfOutcome = outcomes.length;
		LOGGER.info("outcomes : " + numberOfOutcome);
		List<CadfEvent> list = new ArrayList<>();
		final String serviceName = "OutReachRuleProcessor";
		CadfResource observer = new CadfResource.Builder(null, CadfResource.ResourceType.compute_node, PropertyUtil.getProperty("at.observer.name"))
				.build();	
		CadfResource initiator = new CadfResource.Builder(null, CadfResource.ResourceType.data_message_stream,
				PropertyUtil.getProperty("at.initiator.name")).build();  
		CadfResource target = new CadfResource.Builder(null, CadfResource.ResourceType.compute_node,
				String.format(PropertyUtil.getProperty("at.target.name"), UUID.randomUUID().toString())).build();
		Map<String, String> req = new HashMap<String, String>();
		req.put(inputKey, inputvalue);
		CadfEvent evt = new CadfEvent.Builder(null, CadfEvent.EventType.activity, null,
				new CadfAction(cadfActions[9 % numberOfActions].toString()), outcomes[0 % numberOfOutcome])
						.withInitiator(initiator).withObserver(observer).withTarget(target).withRequest(req)
						.withMessage(serviceName+": "+PropertyUtil.getProperty("at.eventtype.message")+outcomes[0 % numberOfOutcome]).build();
		list.add(evt);
		LOGGER.info("list is : " + list);

		return list;
	}
}
