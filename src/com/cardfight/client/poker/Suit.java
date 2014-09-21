
package com.cardfight.client.poker;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Suit implements IsSerializable {
	public static final int HEARTS=0;
	public static final int SPADES=1;
	public static final int DIAMONDS=2;
	public static final int CLUBS=3;
	
	public Suit() {
	}

	public static final String suitName[] = {"h", "s", "d", "c"};
}
