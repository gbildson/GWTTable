package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFTakeSeat extends VFEvent  {
	private int seatNum;

	
	public VFTakeSeat() {
		this.seatNum = -1;
		eventName = "VFTakeSeat";
	}
	
	public VFTakeSeat(int seatNum) {
		this.seatNum = seatNum;
		eventName = "VFTakeSeat";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//
	}
	
	public int getSeatNum() {
		return seatNum;
	}

}
