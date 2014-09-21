package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFUserid extends VFEvent  {
	private String userid;

	
	public VFUserid() {
		this.userid = "";
		setMsgID(-3);
		eventName = "VFuserid";

	}
	
	public VFUserid(String userid) {
		this.userid = userid;
		setMsgID(-3);
		eventName = "VFUserid";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//
	}
	
	public String getUserid() {
		return userid;
	}

}
