
package com.cardfight.client.poker;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PlayerState implements IsSerializable {
	public int     position;
	public boolean sitting;
	public int     activeCards;
	public String  nickname;
	public String  amount;
	public boolean showCards;
	public int     cards[];
	public double  bets[];
	
	public PlayerState() {
	}
	
	public PlayerState(int position) {
		this.position = position;
	}
}

