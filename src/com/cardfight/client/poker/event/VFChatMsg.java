package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFChatMsg extends VFEvent {
	private String chatMsg;
	private String nickname;
	
	public VFChatMsg() {
		this.chatMsg  = "";
		this.nickname = "";
		eventName = "VFChatMsg";
	}
	
	public VFChatMsg(String nickname, String chatMsg) {
		this.chatMsg = chatMsg;
		this.nickname = nickname;
		eventName = "VFChatMsg";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//
	}
	
	public String getNick() {
		return nickname;
	}
	
	public String getMsg() {
		return chatMsg;
	}

}
