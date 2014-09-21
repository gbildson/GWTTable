package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;


public class VFInitTable extends VFEvent  {
	private int numPlayers;
	
	public VFInitTable() {
		this.numPlayers = -1;
		eventName = "VFInitTable";
	}
	
	public VFInitTable(int numPlayers) {
		this.numPlayers = numPlayers;
		eventName = "VFInitTable";
	}
	
	public void handleEvent(VisualFeedback vf) {
		vf.initTable(numPlayers);
	}
}
