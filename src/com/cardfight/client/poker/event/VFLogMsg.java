package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFLogMsg extends VFEvent {
	private String msg;
	
	public VFLogMsg() {
		this.msg  = "";
		eventName = "VFLogMsg";
	}
	
	public VFLogMsg(String msg) {
		this.msg = msg;
		eventName = "VFLogMsg";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//
	}
	
	public String getMsg() {
		return msg;
	}

}
