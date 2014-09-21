package com.cardfight.server.poker;

import java.util.ArrayList;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.PotWinner;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.poker.ActionCallback;
import com.cardfight.client.poker.event.*;

public class VisualFeedbackEventQueue implements VisualFeedback {
	private ArrayList<VFEvent> queue = new ArrayList<VFEvent>();
	private ActionCallback pendingAC;
	private int            curMsgID = 0;
	private static int     queueID = 555;
	private int            myQueueID;
	private String         tableID;
	private VisualFeedbackMultiplexor parent;
	
	public void setParent(VisualFeedbackMultiplexor parent) {
		this.parent = parent;
	}
	public VisualFeedbackMultiplexor getParent() {
		return parent;
	}
	public void setTableID(String tableID) {
		this.tableID = tableID;
	}
	public String getTableID() {
		return tableID;
	}
	
	public void initTable(int numPlayers) {
		addToQueue(new VFInitTable(numPlayers));
	}
	public void setDealer(int dealerLoc) {
		addToQueue(new VFSetDealer(dealerLoc)); 
	}
	public void setPots(double pots[]){
		addToQueue(new VFSetPots(pots)); 
	}
	public void deliverPot(int potNum, PotWinner potWinners[]){
		addToQueue(new VFDeliverPot(potNum, potWinners)); 
	}
	public void setCommonCards(CardCollection cards){
		addToQueue(new VFSetCommonCards(cards)); 
	}
	public void setPlayer(int playerNum, String nickname){
		addToQueue(new VFSetPlayer(playerNum, nickname));
	}
	public void setPlayerAmount(int playerNum, String amount, int delay){
		addToQueue(new VFSetPlayerAmount(playerNum, amount));
	}
	public void dealPlayerCard(int playerNum, CardCollection cards){
		addToQueue(new VFDealPlayerCard(playerNum, cards));
	}
	public void setPlayerCards(int playerNum, CardCollection cards){
		//System.out.println("************************************************");
		//System.out.println("************************************************");
		//System.out.println("************************************************");

		//System.out.println("server VFSetPlayerCards - "+ playerNum+ " cards: "+ cards);
		//Thread.dumpStack();
		//System.out.println("************************************************");
		//System.out.println("************************************************");
		//System.out.println("************************************************");

		//int i = 4 / 0;
		addToQueue(new VFSetPlayerCards(playerNum, cards));
	}
	public void setAllShowCards(boolean showCards){
		addToQueue(new VFSetAllShowCards(showCards));
	}
	public void setPlayerShowCards(int playerNum, boolean showCards){
		addToQueue(new VFSetPlayerShowCards(playerNum, showCards));
	}
	public void setPlayerBets(int playerNum, double bets[], int delay){
		addToQueue(new VFSetPlayerBets(playerNum, bets));
	}
	public void setPlayerBetState(int playerNum, String state){
		addToQueue(new VFSetPlayerBetState(playerNum, state));
	}
	public void collectBets(int potNum, boolean doAnim){
		addToQueue(new VFCollectBets(potNum, doAnim));
	}
	public void takeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
	  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit, double minNonLimitRaise, 
	  ActionCallback ac){
		pendingAC = ac;
		addToQueue(new VFTakeAction(bettingMode, nick, playerBet, toCall, callable,  bigBlind,
				  maxRaiseIfNoLimit, raiseIfLimit, maxRaiseIfPotLimit, minNonLimitRaise));
	}
	public void actionTimeout(){
		//System.out.println("action created");
		addToQueue(new VFActionTimeout());
	}
	public void initRound(){
		addToQueue(new VFInitRound());
	}
	public void update(){
		
	}
	public void sync(){
		
	}
	
	public void chatMsg(String nick, String msg) {
		addToQueue(new VFChatMsg(nick, msg));
	}
	
	//private boolean firstPass = true;
	boolean printedMsg = false;
	private void addToQueue(VFEvent evt){
		//if ( firstPass ) {
			//try { Thread.sleep(3000); } catch (InterruptedException ie) {}
			//firstPass = false;
		//}
		try { Thread.sleep(6); } catch (InterruptedException ie) {}

		int queueSize = 0;
		synchronized(queue) {
			evt.setMsgID(curMsgID++);
			queue.add(evt);
			queueSize = queue.size();
		}

		if ( queueSize > 100 ) {
			try { Thread.sleep(25); } catch (InterruptedException ie) {}
			if ( printedMsg == false ) {
				System.out.println("Slowing down queue: "+queueSize);
				printedMsg = true;
			}
		} else {
			if ( printedMsg == true ) 
				System.out.println("Speeding up queue: "+queueSize);
			printedMsg = false;
		}
	}
	
	public VFEvent takeFromQueue(){
		VFEvent result = null;
		synchronized(queue) {
			if ( queue.size() > 0 )
				result = queue.remove(0);
		}
		return result;
	}
	
	public int getSize(){
		synchronized(queue) {
			return queue.size();
		}
	}
	
	public ActionCallback getPendingAC() {
		return pendingAC;
	}
	
	//private static VisualFeedbackEventQueue equeue = null;
	public VisualFeedbackEventQueue() {
		synchronized(VisualFeedbackEventQueue.class) {
			myQueueID = queueID++;
		}
	}
	
	public String getQueueID() {
		return ""+myQueueID;
	}
	/*
	public static VFEvent takeFromQueue(String tableID) {
		if ( equeue != null )
			return equeue.takeFromQueue();
		else
			return null;
	}*/
}
