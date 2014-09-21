package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFRedirect extends VFEvent  {
	private String tableID;

	
	public VFRedirect() {
		this.tableID = "";
		setMsgID(-2);
		eventName = "VFRedirect";
	}
	
	public VFRedirect(String tableID) {
		this.tableID = tableID;
		setMsgID(-2);
		eventName = "VFRedirect";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//
	}
	
	public String getTableID() {
		return tableID;
	}

}
