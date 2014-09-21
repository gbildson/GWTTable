package com.cardfight.client.poker.event;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;


public class VFDealPlayerCard extends VFEvent {
	private int playerNum;
	private CardCollection cards;
	
	public VFDealPlayerCard(){
		this.playerNum = -1;
		this.cards = null;
		eventName = "VFDealPlayerCard";
	}
	
	public VFDealPlayerCard(int playerNum, CardCollection cards){
		this.playerNum = playerNum;
		this.cards = cards;
		eventName = "VFDealPlayerCard";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.dealPlayerCard(playerNum, cards);
	}

}
