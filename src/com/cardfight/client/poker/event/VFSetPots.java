package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetPots extends VFEvent  {
	private double pots[];

	public VFSetPots() {
		this.pots = null;
		eventName = "VFSetPots";
	}
	
	public VFSetPots(double pots[]) {
		this.pots = pots;
		eventName = "VFSetPots";
	}

	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setPots(pots);
	}
}
