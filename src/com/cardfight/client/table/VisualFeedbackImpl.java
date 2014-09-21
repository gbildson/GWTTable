
package com.cardfight.client.table;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.PotWinner;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.poker.ActionCallback;
import com.cardfight.server.poker.*;
import com.cardfight.client.TopControl;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


public class VisualFeedbackImpl implements VisualFeedback {
	
	//private static final Log LOG = LogFactory.getLog(VisualFeedbackImpl.class);
	
	private HoldemTable vtable;
	private TopControl  control;
	private int numPlayers;
	private String activePlayer = null;

	public VisualFeedbackImpl(HoldemTable vtable, TopControl control) {
		this.vtable  = vtable;
		this.control = control;
	}
	
	public void sync() {
		
	}

	public void initTable(int numPlayers) {
		this.numPlayers = numPlayers;
		//if (LOG.isDebugEnabled())
			//LOG.debug("initTable "+numPlayers);
		//table = new TableState(numPlayers);
		vtable.createTableState(numPlayers);
		vtable.createPlayerSeats(numPlayers);
		//playerSeats  = new PlayerSeat[numPlayers];
		//for (int i = 0; i < numPlayers; i++) {
		//playerSeats[i] = new PlayerSeat(i);
		//}
		vtable.createPlayerBoxes(numPlayers);
		//playerBoxes  = new PlayerBox[numPlayers];
		//playersCards = new PlayersCards[numPlayers];
		vtable.createPlayersCards(numPlayers);
		vtable.createSmallCards(numPlayers); //smallCards   = new SmallCards[numPlayers];
		vtable.createBets(numPlayers);
		//bets         = new Bet[numPlayers];
		vtable.createDealerMarker(0);
		//dealerMarker = new DealerMarker(0);
	}

	public void setDealer(int dealerLoc) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setDealer "+dealerLoc);
		//table.dealer = dealerLoc;
		vtable.moveDealer(dealerLoc);
		//dealerMarker.moveDealer(dealerLoc);
		update();
	}

	//private boolean firstPass = true;
	public void setPots(double pots[]) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setPots "+reportDoubles(pots));
		//table.pots  = pots;
		vtable.setPots(pots);
		//if ( htable.pots != null) {
		//for (int i = 0; i < htable.pots.length; i++) 
		//ToolTipHelper.clearToolTip(htable.pots[i]);
		//}
		//htable.pots = new Pot[pots.length];
		//for (int i = 0; i < pots.length; i++) {
		//htable.pots[i] = new Pot(i, pots[i]);
		//}
		//if (firstPass && pots.length > 1) {
		//firstPass = false;
		//addToolTip();
		//}
		update();
	}
	
	private String reportDoubles(double val[]) {
		String res = "";
		for (int i = 0; i < val.length; i++) {
			res += ""+i+": AMT:"+val[i]+ "  ";
		}
		return res;
	}

	public void deliverPot(int potNum, PotWinner potWinners[]) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("deliverPot :"+potNum);
		vtable.clearPot(potNum);

		DeliverPot anim = new DeliverPot(vtable, potNum, potWinners, numPlayers, control);
		//anim.done();
		control.waitUntil(anim.maxDelay());
		//TODO above
	}


	public void setCommonCards(CardCollection cards) {
		/*
		System.out.println("setCommonCards "+cards);
		vtable.setCommonCardsState(cards);
		//table.cards = makeVisualCards(cards);
		//if (table.cards.length > 0)
		if (cards.size() > 0)
			vtable.createCommonCards(); //commonCards = new CommonCards(table.cards);
		else 
			vtable.clearCommonCards();   //commonCards = null;
		update();
		*/
		
		//if (LOG.isDebugEnabled())
			//LOG.debug("setCommonCards "+cards);
		int priorCommonCards = vtable.getNumCommonCards();
		vtable.setCommonCardsState(cards);

		if (cards.size() > 0)
			vtable.createCommonCards(priorCommonCards); //commonCards = new CommonCards(table.cards);
		else {
			vtable.clearCommonCards();   //commonCards = null;
		    update();
		}
		//if (LOG.isDebugEnabled())
			//LOG.debug("prior:"+priorCommonCards+" current:"+cards.size());
		long max = 0;
		for (int i = priorCommonCards+1; i <= cards.size(); i++) {
			DealACommonCard anim = new DealACommonCard(vtable, this, i, control);
			//TODO above
			//anim.done();
			max = Math.max(max, anim.maxDelay());
		}
		control.waitUntil(max);
	}
	
	public void setActivePlayer(String nick) {
		activePlayer = nick;
	}

	public void setPlayer(int playerNum, String nickname) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setPlayer "+playerNum+" : "+nickname);

		if ( TAKE_SEAT.equals(nickname) ){
			vtable.removePlayerBox(playerNum);
			vtable.setTakeSeat(playerNum, numPlayers);
		} else if ( nickname != null ) {
			vtable.setPlayerBox(playerNum, nickname, "");
			vtable.removeTakeSeat(playerNum);
			if ( activePlayer != null && activePlayer.equals(nickname) )
				vtable.setActivePlayer(activePlayer);
		} else {
			vtable.removePlayerBox(playerNum);
		}
		update();
	}

	public void setPlayerAmount(int playerNum, String amount, int delay) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setPlayerAmount "+playerNum+" : "+amount);
		vtable.setPlayerAmount(playerNum, amount);

		vtable.repaint();
		//try { Thread.sleep(delay); } catch (InterruptedException ie) {}
	}

	public void dealPlayerCard(int playerNum, CardCollection cards) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("dealPlayerCard "+playerNum+" : "+cards);
		DealACard anim = new DealACard(vtable, this, playerNum, cards, numPlayers, control);
		//TODO
		//anim.done();
		control.waitUntil(anim.maxDelay());
	}

	public void setPlayerCards(int playerNum, CardCollection cards) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setPlayerCards "+playerNum+" : "+cards);
		//System.out.println("setPlayerCards "+playerNum+" : "+cards);
		if ( cards == null || cards.size() == 0){
			//int i = 4 / 0;
		}
		vtable.setPlayerCards(playerNum, cards);
		//table.players[playerNum].cards = makeVisualCards(cards);
		//setVisualCardsAppropriately(playerNum, table.players[playerNum].showCards);
		update();
	}

	public void setAllShowCards(boolean showCards) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setAllShowCards "+showCards);
		vtable.setAllShowCards(showCards);
		//for (int i = 0; i < table.players.length; i++) {
		//setPlayerShowCards(i,showCards);
		//}
		update();
	}

	public void setPlayerShowCards(int playerNum, boolean showCards) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setPlayerShowCards "+playerNum+" : "+showCards);
		vtable.setPlayerShowCards(playerNum, showCards);
		//table.players[playerNum].showCards = showCards;
		//setVisualCardsAppropriately(playerNum, showCards);
		update();
	}



	public void setPlayerBets(int playerNum, double bets[], int delay) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setPlayerBets "+playerNum+": "+reportDoubles(bets));
		//table.players[playerNum].bets = bets;
		vtable.setPlayerBets(playerNum, bets);
		//ToolTipHelper.clearToolTip(htable.bets[playerNum]);
		//if (bets.length > 0) {
		//htable.bets[playerNum] = new Bet(playerNum, bets);
		//} else {
		//htable.bets[playerNum] = null;
		//}
		//update();
		double potTotal = vtable.getPotTotal(/*table*/);
		//htable.potTotal = new PotTotal(potTotal);
		vtable.setPotTotal(potTotal);
		vtable.repaint();
		//try { Thread.sleep(delay); } catch (InterruptedException ie) {}
	}

	public void setPlayerBetState(int playerNum, String state) {
		vtable.setPlayerSeatStatus(playerNum,state);
		//playerSeats[playerNum].setStatusText(state);
		//vtable.repaint();
		//try { Thread.sleep(400); } catch (InterruptedException ie) {}
		//TODO above
	}

	public void collectBets(int potNum, boolean doAnim) {
		if (vtable.noActiveBet() /*bets == null || bets.length == 0*/)
			return;
		CollectBets anim = new CollectBets(vtable, potNum);
		if ( doAnim )
			control.waitUntil(anim.maxDelay());
		else 
			anim.done();
		//TODO above
	}

	public void takeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
			  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit,  
			  double minNonLimitRaise, ActionCallback ac) {
		vtable.takeAction(bettingMode, nick, playerBet, toCall, callable, bigBlind, maxRaiseIfNoLimit, raiseIfLimit, 
		  maxRaiseIfPotLimit, minNonLimitRaise, ac);
	}
	
	public void actionTimeout() {
		vtable.actionTimeout();
	}
	

	public void initRound() {
		//if (LOG.isDebugEnabled())
			//LOG.debug("initRound()");
		//table.cards = new int[0];
		vtable.clearCommonCards(); //htable.commonCards = null;
		//table.pots  = new double[0];
		//if (htable.pots != null) {
		//for(int i = 0; i < htable.pots.length; i++) 
		//ToolTipHelper.clearToolTip(htable.pots[i]);
		//}
		//htable.pots = null;
		vtable.clearPots();
		//htable.potTotal = new PotTotal(0.0d);
		vtable.setPotTotal(0.0d);
		for (int i = 0; i < vtable.getNumPlayers() /*table.players.length*/; i++) {
			vtable.setPlayerCardState(i);
			//table.players[i].cards = new int[0];
			//table.players[i].showCards = false;
			//htable.playersCards[i] = null;
			vtable.clearPlayerCards(i);
			//table.players[i].bets = new double[0];
			vtable.clearBet(i);
			//ToolTipHelper.clearToolTip(htable.bets[i]);
			//htable.bets[i] = null;
		}
		update();
	}

	public void update() {
		//table.active = true;
		vtable.setTableActive();
		//vtable.repaint();
		//try { Thread.sleep(10); } catch (InterruptedException ie) {}
	}
	
	public void chatMsg(String nick, String msg) { // Handled in incoming already
	}
}


