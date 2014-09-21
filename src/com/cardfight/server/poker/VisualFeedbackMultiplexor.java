
package com.cardfight.server.poker;

import java.util.ArrayList;
//import poker.gui.HoldemGame;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.PotWinner;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.poker.ActionCallback;
import com.cardfight.client.poker.event.VFChatMsg;


public class VisualFeedbackMultiplexor implements VisualFeedback {

	private ArrayList observers = new ArrayList();
	private ArrayList<VisualFeedback> newObservers = new ArrayList<VisualFeedback>();
	private ArrayList<VisualFeedback> oldObservers = new ArrayList<VisualFeedback>();
	private SyncCallback syncCallback;
	private boolean waiting = false;
	private Object  waitingLock = new Object();
	private HoldemSimulation hs = null;

	public void setWaiting(boolean waiting) {
		synchronized(waitingLock) {
			waiting = waiting;
		}
	}
	
	public void ifWaitingSync() {
		synchronized(waitingLock) {
			if (waiting) {
				//System.out.println("syncing internal");
				sync();
				//System.out.println("Done syncing internal");
			}
		}
	}
	
	public void addObserver(VisualFeedback observer){
		observers.add(observer);
	}
	
	public void removeObserver(VisualFeedback observer){
		oldObservers.add(observer);
	}
	
	public int getNumChildren() {
		return observers.size() + newObservers.size() - oldObservers.size();
	}
	
	public void addObserverAfterSync(VisualFeedback observer){
		newObservers.add(observer);
		ifWaitingSync();
	}
	
	public void setSyncCallback(SyncCallback syncCallback) {
		this.syncCallback = syncCallback;
	}
	
	public void sync() {
		if (newObservers.size()> 0) {
			//System.out.println("syncing ...");
			for (VisualFeedback vf : newObservers){
				syncCallback.finishInitNewTable(vf);
				addObserver(vf);
			}
			//HoldemGame.hackDump();
			do {
				newObservers.remove(newObservers.get(0));
			} while (newObservers.size()> 0);
			//System.out.println("done syncing ...");
		}
		if (oldObservers.size()> 0) {
			//System.out.println("sync oldObs ...");

			for (VisualFeedback vf : oldObservers){
				observers.remove(vf);
			}
			//HoldemGame.hackDump();
			do {
				oldObservers.remove(oldObservers.get(0));
			} while (oldObservers.size()> 0);
			//System.out.println("done sync oldObs ...");
		}
	}

	public void initTable(int numPlayers) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.initTable(numPlayers);
		}
	}

	public void setDealer(int dealerLoc) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setDealer(dealerLoc);
		}
	}

	public void setPots(double pots[]) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setPots(pots);
		}
	}

	public void deliverPot(int potNum, PotWinner potWinners[]) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.deliverPot(potNum, potWinners);
		}
	}

	public void setCommonCards(CardCollection cards) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setCommonCards(cards);
		}
	}

	public void setPlayer(int playerNum, String nickname) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setPlayer(playerNum, nickname);
		}
	}

	public void setPlayerAmount(int playerNum, String amount, int delay) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setPlayerAmount(playerNum, amount, delay);
		}
	}

	public void dealPlayerCard(int playerNum, CardCollection cards) {
		//System.out.println("dealPlayerCard : "+ playerNum +" cards: "+cards);
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.dealPlayerCard(playerNum, cards);
		}
	}

	public void setPlayerCards(int playerNum, CardCollection cards) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setPlayerCards(playerNum, cards);
		}
	}

	public void setAllShowCards(boolean showCards) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setAllShowCards(showCards);
		}
	}

	public void setPlayerShowCards(int playerNum, boolean showCards) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setPlayerShowCards(playerNum, showCards);
		}
	}

	public void setPlayerBets(int playerNum, double bets[], int delay) {
		//System.out.println("setPlayerBets : "+ playerNum +" bets: "+bets);
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setPlayerBets(playerNum, bets, delay);
		}
	}
	public void setPlayerBetState(int playerNum, String state) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.setPlayerBetState(playerNum, state);
		}
	}

	public void collectBets(int potNum, boolean doAnim) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.collectBets(potNum, doAnim);
		}
	}
	
	
	public void takeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
			  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit, 
			  double minNonLimitRaise, ActionCallback ac) {
		//System.out.println("takeAction : "+ nick );

		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.takeAction(bettingMode, nick, playerBet, toCall, callable, bigBlind, maxRaiseIfNoLimit, raiseIfLimit, 
			  maxRaiseIfPotLimit, minNonLimitRaise, ac);
		}
	}
	
	public void actionTimeout() {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.actionTimeout();
		}
	}

	public void initRound() {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.initRound();
		}
	}

	public void update() {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.update();
		}
	}
	
	public void chatMsg(String nick, String msg) {
		VisualFeedback vf;
		for (int i = 0; i < observers.size(); i++) {
			vf = (VisualFeedback) observers.get(i);
			vf.chatMsg(nick, msg);
		}
	}
	
	public void setGame(HoldemSimulation hs) {
		this.hs = hs;
	}
	
	public HoldemSimulation getGame() {
		return hs;
	}
}
