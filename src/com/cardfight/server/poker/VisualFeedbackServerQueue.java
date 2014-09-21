package com.cardfight.server.poker;

import java.util.ArrayList;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.PotWinner;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.poker.ActionCallback;
import com.cardfight.client.poker.event.*;

public class VisualFeedbackServerQueue extends VisualFeedbackEventQueue {
	// Only takeAction is active.
	public void initTable(int numPlayers) {
	}
	public void setDealer(int dealerLoc) {
	}
	public void setPots(double pots[]){
	}
	public void deliverPot(int potNum, PotWinner potWinners[]){
	}
	public void setCommonCards(CardCollection cards){
	}
	public void setPlayer(int playerNum, String nickname){
	}
	public void setPlayerAmount(int playerNum, String amount, int delay){
	}
	public void dealPlayerCard(int playerNum, CardCollection cards){
	}
	public void setPlayerCards(int playerNum, CardCollection cards){
	}
	public void setAllShowCards(boolean showCards){
	}
	public void setPlayerShowCards(int playerNum, boolean showCards){
	}
	public void setPlayerBets(int playerNum, double bets[], int delay){
	}
	public void setPlayerBetState(int playerNum, String state){
	}
	public void collectBets(int potNum, boolean doAnim){
	}
	public void actionTimeout(){
	}
	public void initRound(){
	}
	public void update(){
	}
	public void sync(){
	}
	public void chatMsg(String nick, String msg) {
	}
}
