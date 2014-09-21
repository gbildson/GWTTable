
package com.cardfight.server.poker;

import com.cardfight.client.poker.Card;
import com.cardfight.client.poker.CardCollection;
import java.util.*;


public class Deck extends CardCollection {

	public Deck() {
		for(int i=0; i <= 3; i++) {
			for (int j=0; j <= 12; j++) {
			    add(Card.create(i,j));
			}
		}
	}
	
	public void shuffle() {
		Collections.shuffle(_cards);
	}
}
