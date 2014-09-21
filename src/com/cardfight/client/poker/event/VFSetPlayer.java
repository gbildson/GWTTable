package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFSetPlayer extends VFEvent {
	private int playerNum;
	private String nickname;
	
	public VFSetPlayer() {
		this.playerNum = -1;
		this.nickname = "";
		eventName = "VFSetPlayer";
	}
	
	public VFSetPlayer(int playerNum, String nickname) {
		this.playerNum = playerNum;
		this.nickname = nickname;
		eventName = "VFSetPlayer";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.setPlayer(playerNum, nickname);
	}
	
	public int getPlayerNum() {
		return playerNum;
	}
	
	public String getNick() {
		return nickname;
	}

}
