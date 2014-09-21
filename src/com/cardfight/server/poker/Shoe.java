
package com.cardfight.server.poker;

import java.util.Collections;

import com.cardfight.client.poker.Card;
import com.cardfight.client.poker.CardCollection;

public class Shoe extends CardCollection {
	public Shoe() {
	}

	public void addDeck(Deck deck) {
		addAll(deck);	
	}

	public Card dealCard() {
		return remove();
	}
	
	public void shuffle() {
		Collections.shuffle(_cards);
	}
}
