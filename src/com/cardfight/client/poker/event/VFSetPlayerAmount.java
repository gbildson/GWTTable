package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetPlayerAmount extends VFEvent {
	private int playerNum;
	private String amount;
	
	public VFSetPlayerAmount() {
		this.playerNum = -1;
		this.amount = "";
		eventName = "VFSetPlayerAmount";
	}
	
	public VFSetPlayerAmount(int playerNum, String amount) {
		this.playerNum = playerNum;
		this.amount = amount;
		eventName = "VFSetPlayerAmount";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setPlayerAmount(playerNum, amount, 0);
	}

}
