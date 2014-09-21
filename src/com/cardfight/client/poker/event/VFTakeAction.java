package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.cardfight.client.poker.ActionCallback;
import com.cardfight.client.poker.ActionCallbackImpl;


public class VFTakeAction extends VFEvent  {
	private int bettingMode;
	private String nick;
	private double playerBet;
	private double toCall;
	private double callable;
	private double bigBlind;
	private double maxRaiseIfNoLimit;
	private double raiseIfLimit;
	private double maxRaiseIfPotLimit;
	private double minNonLimitRaise; 
	
	public VFTakeAction() {
		this.bettingMode = -1;
		this.nick = null;
		this.playerBet = -1;
		this.toCall = -1;
		this.callable = -1;
		this.bigBlind = -1;
		this.maxRaiseIfNoLimit = -1;
		this.raiseIfLimit = -1;
		this.maxRaiseIfPotLimit = -1;
		this.minNonLimitRaise = -1;
		eventName = "VFTakeAction";
	}
	
	public VFTakeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
			  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit, double minNonLimitRaise) {
		this.bettingMode = bettingMode;
		this.nick = nick;
		this.playerBet = playerBet;
		this.toCall = toCall;
		this.callable = callable;
		this.bigBlind = bigBlind;
		this.maxRaiseIfNoLimit = maxRaiseIfNoLimit;
		this.raiseIfLimit = raiseIfLimit;
		this.maxRaiseIfPotLimit = maxRaiseIfPotLimit;
		this.minNonLimitRaise = minNonLimitRaise;
		eventName = "VFTakeAction";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		//System.out.println("takeAction");
		ActionCallback ac = new ActionCallbackImpl(vf);
		vf.takeAction(bettingMode, nick, playerBet, toCall, callable, bigBlind, maxRaiseIfNoLimit, raiseIfLimit, 
		  maxRaiseIfPotLimit, minNonLimitRaise, ac);
		//System.out.println("done takeAction");
	}
	
	public double getCallable() {
		return callable;
	}
	
	public double getPlayerBet() {
		return playerBet;
	}
	
	public String getNick() {
		return nick;
	}

}
