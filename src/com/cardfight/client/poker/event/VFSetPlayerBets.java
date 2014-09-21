package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetPlayerBets extends VFEvent  {
	private int playerNum;
	private double bets[];
	
	public VFSetPlayerBets() {
		this.playerNum = -1;
		this.bets = null;
		eventName = "VFSetPlayerBets";
	}
	
	public VFSetPlayerBets(int playerNum, double bets[]) {
		this.playerNum = playerNum;
		this.bets = bets;
		eventName = "VFSetPlayerBets";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setPlayerBets(playerNum, bets, 0);
	}

}
