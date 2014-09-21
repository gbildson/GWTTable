package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetDealer extends VFEvent  {
	private int dealerLoc;

	public VFSetDealer() {
		this.dealerLoc = -1;
		eventName = "VFSetDealer";
	}
	
	public VFSetDealer(int dealerLoc) {
		this.dealerLoc = dealerLoc;
		eventName = "VFSetDealer";
	}

	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setDealer(dealerLoc);
	}
}
