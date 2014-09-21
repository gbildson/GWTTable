
package com.cardfight.client.poker;

import com.google.gwt.user.client.rpc.IsSerializable;


public class PotWinner implements IsSerializable {
	private PlayerInGame player;
	private double       winnings;
	
	public PotWinner() {
		this.player = null;
		this.winnings = -1.00;
	}
	
	public PotWinner(PlayerInGame player, double winnings) {
		this.player = player;
		this.winnings = winnings;
	}

	public PlayerInGame getPlayer() {
		return player;
	}

	public double getWinnings() {
		return winnings;
	}
}
