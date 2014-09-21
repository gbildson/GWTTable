
package com.cardfight.server.poker;

import java.util.*;

import com.cardfight.client.poker.PlayerInGame;
import com.cardfight.client.poker.CommonCards;


public class Table {

	private Pot          _currentPot;
	private ArrayList    _pots;	
	private PlayerInGame _seats[];
	private int          _dealerPosition;
	private CommonCards  _commonCards;

	public Table(int numSeats) {
		_seats = new PlayerInGame[numSeats];
		_dealerPosition = 0;
		initialize();
	}

	public void initialize() {
		_currentPot  = new Pot();
		_pots     = new ArrayList();
		_pots.add(_currentPot);
		_commonCards = new CommonCards();
	}

	public void takeSeat(int seat, PlayerInGame player) {
		_seats[seat] = player;
		player.setSeatNum(seat);
	}
	
	public void standUp(int seat, PlayerInGame player) {
		_seats[seat] = null;
		player.setSeatNum(-1);
	}

	public PlayerInGame getPlayer(int seat) {
//System.out.println("gp("+seat+")  size:"+_seats.length);
		return _seats[seat];
	}

	public CommonCards getCommonCards() {
		return _commonCards;
	}

	public int getSize() {
		return _seats.length;
	}

	public void advanceButton() {
		_dealerPosition = (_dealerPosition + 1) % getSize();
		
		for (int i = 0; i < getSize(); i++) {
			PlayerInGame nextDealerPos = _seats[_dealerPosition];
			if ( nextDealerPos == null || !nextDealerPos.isActive() ) 
				_dealerPosition = (_dealerPosition + 1) % getSize();
			else
				break;
		}
	}

	public int getDealerPosition() {
		return _dealerPosition;
	}

	public void addToPot(double bet, PlayerInGame player) {
		_currentPot.add(bet, player);
		player.addToPot(bet);
	}
	
	/*public void refundPot() {
		if ( _currentPot == null || _currentPot.getNumPlayers() == 0) return;
		
		double potAmount = _currentPot.getPotSize() / ((double)_currentPot.getNumPlayers());
		for (int i = 0; i < _currentPot.getNumPlayers(); i++) {
			_currentPot.getPlayer(i).giveWinnings(potAmount);
		}
		_currentPot.clearPot();
	}*/

	public double getPotSize() {
		return _currentPot.getPotSize();
	}

	public double getTotalPotSize() {
		ArrayList pots = getPots();
		Pot       pot;
		double    total = 0.0;

		// Add up pots
		for (int i = 0; i < pots.size(); i++) {
			pot = (Pot)pots.get(i);
			total += pot.getPotSize();
		}

		// Add in current bets/blinds
		int tableSize = getSize();
		PlayerInGame player;
		double       curBet;
		for (int i = 0; i < tableSize; i++) {
			player = getPlayer(i);

			if (player == null) continue;

			if (!player.isActive()) continue;

			curBet = player.getBetInRound();   // TODO: ante?
			total += curBet;
		}
		return total;
	}

	public ArrayList getPots() {
		return _pots;
	}

	public void newPot() {
		Pot pot = new Pot();
		_currentPot = pot;
		_pots.add(_currentPot);
	}
}
