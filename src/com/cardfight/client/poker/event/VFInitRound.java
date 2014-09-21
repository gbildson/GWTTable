package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFInitRound extends VFEvent  {

	public VFInitRound() {
		eventName = "VFInitRound";
	}
	
	public void handleEvent(VisualFeedback vf) {
		vf.initRound();
	}
}
