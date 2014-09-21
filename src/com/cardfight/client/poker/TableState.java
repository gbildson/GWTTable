
package com.cardfight.client.poker;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TableState implements IsSerializable {
	public int          cards[];
	public double       pots[];
	public int          dealer;
	public PlayerState  players[];
	public boolean      active;

	public TableState() {
	}

	public TableState(int numPlayers) {
		players = new PlayerState[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			players[i] = new PlayerState(i);
		}
	}
}
