package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

public abstract class VFEvent implements IsSerializable {
	
	private int msgID;
	protected String eventName = "undefined";
	//public long time;
	
	public abstract void handleEvent(VisualFeedback vf);
	
	public int  getMsgID() {
		return msgID;
	}
	
	public void setMsgID(int id) {
		msgID = id;
	}
	
	public String getEventName() {
		return eventName;
	}
}
