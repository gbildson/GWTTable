package com.cardfight.client.poker.event;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;


public class VFSetPlayerCards extends VFEvent {
	private int playerNum;
	private CardCollection cards;
	
	public VFSetPlayerCards(){
		this.playerNum = -1;
		this.cards = null;
		eventName = "VFSetPlayerCards";
	}
	
	public VFSetPlayerCards(int playerNum, CardCollection cards){
		this.playerNum = playerNum;
		this.cards = cards;
		eventName = "VFSetPlayerCards";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setPlayerCards(playerNum, cards);
	}

}
