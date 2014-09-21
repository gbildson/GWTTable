
package com.cardfight.client.poker;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Player implements IsSerializable {
	double _playMoney;
	double _realMoney;
	String _nickName;
	
	public Player() {
		_playMoney = -1.00;
		_realMoney = -1.00;
		_nickName  = "";
	}
	
	public Player(double playMoney, double realMoney, String nickName) {
		_playMoney = playMoney;
		_realMoney = realMoney;
		_nickName  = nickName;
	}

	public String getNick() {
		return _nickName;
	}

	public double getRealMoney() {
		return _realMoney;
	}

	public double getPlayMoney() {
		return _playMoney;
	}
}
