package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetAllShowCards extends VFEvent  {
	private boolean showCards;
	
	public VFSetAllShowCards(){
		this.showCards = false;
		eventName = "VFSetAllShowCards";
	}
	
	public VFSetAllShowCards(boolean showCards){
		this.showCards = showCards;		
		eventName = "VFSetAllShowCards";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setAllShowCards(showCards);
	}

}
