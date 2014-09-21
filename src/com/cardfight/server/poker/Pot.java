
package com.cardfight.server.poker;

import com.cardfight.client.poker.PlayerInGame;

public class Pot {
	double _pot;
	Participants _participants;

	public Pot() {
		clearPot();
	}
	
	public void clearPot() {
		_pot = 0;
		_participants = new Participants();
	}
	
	public void add(double bet, PlayerInGame player) {
		_pot += bet;
		_participants.addIfNotThere(player);
	}

	public double getPotSize() {
		return _pot;
	}

	public int getNumPlayers() {
		return _participants.getSize();
	}

	public PlayerInGame getPlayer(int num) {
		return _participants.get(num);
	}

	public String toString() {
		String str  = "" +_pot;
		return str;
	}
}
