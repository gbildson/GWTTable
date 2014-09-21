package com.cardfight.client.poker.event;

import com.cardfight.client.poker.PotWinner;
import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFDeliverPot extends VFEvent  {
	private int potNum;
	private PotWinner potWinners[];
	
	public VFDeliverPot(){
		this.potNum = -1;
		this.potWinners = null;
		eventName = "VFDeliverPot";
	}

	public VFDeliverPot(int potNum, PotWinner potWinners[]){
		this.potNum = potNum;
		this.potWinners = potWinners;
		eventName = "VFDeliverPot";
	}

	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.deliverPot(potNum, potWinners);

	}

}
