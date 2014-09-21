package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFBetResponse extends VFEvent  {
	private double bet;
	
	public VFBetResponse() {
		this.bet = -1;
		eventName = "VFBetResponse";
	}
	
	public VFBetResponse(double bet) {
		this.bet = bet;
		eventName = "VFBetResponse";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//
	}
	
	public double getBettingResponse() {
		return bet;
	}
	
}
