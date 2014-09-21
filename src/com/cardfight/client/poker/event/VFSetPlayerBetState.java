package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetPlayerBetState extends VFEvent  {
	private int playerNum;
	private String state;
	
	public VFSetPlayerBetState() {
		this.playerNum = -1;
		this.state = "";
		eventName = "VFSetPlayerBetState";
	}
	
	public VFSetPlayerBetState(int playerNum, String state) {
		this.playerNum = playerNum;
		this.state = state;
		eventName = "VFSetPlayerBetState";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setPlayerBetState(playerNum, state);
	}

}
