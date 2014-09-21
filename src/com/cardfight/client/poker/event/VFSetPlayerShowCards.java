package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetPlayerShowCards extends VFEvent  {
	private int playerNum;
	private boolean showCards;
	
	public VFSetPlayerShowCards() {
		this.playerNum = -1;
		this.showCards = false;
		eventName = "VFSetPlayerShowCards";
	}
	
	public VFSetPlayerShowCards(int playerNum, boolean showCards) {
		this.playerNum = playerNum;
		this.showCards = showCards;
		eventName = "VFSetPlayerShowCards";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setPlayerShowCards(playerNum, showCards);
	}

	public int getPlayerNum() {
		return playerNum;
	}
}
