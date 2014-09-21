package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFQueueName extends VFEvent  {
	private String queueID;

	
	public VFQueueName() {
		this.queueID = "";
		setMsgID(-4);
		eventName = "VFQueueName";
	}
	
	public VFQueueName(String queueID) {
		this.queueID = queueID;
		setMsgID(-4);
		eventName = "VFQueueName";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//
	}
	
	public String getQueueID() {
		return queueID;
	}

}
