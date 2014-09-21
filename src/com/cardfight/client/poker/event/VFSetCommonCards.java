package com.cardfight.client.poker.event;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetCommonCards extends VFEvent {
	private CardCollection cards;
	
	public VFSetCommonCards() {
		this.cards = null;
		eventName = "VFSetCommonCards";
	}
	
	public VFSetCommonCards(CardCollection cards) {
		this.cards = cards;
		eventName = "VFSetCommonCards";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setCommonCards(cards);
	}

}
