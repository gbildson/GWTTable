package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFActionTimeout extends VFEvent {

	public VFActionTimeout() {
		eventName = "VFActionTimeout";
	}
	
	public void handleEvent(VisualFeedback vf) {
		vf.actionTimeout();
	}
}
