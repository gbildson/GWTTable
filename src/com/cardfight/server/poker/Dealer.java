
package com.cardfight.server.poker;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.PlayerInGame;

public class Dealer {
	private Shoe _shoe;
	private int _decks;

	public Dealer(int decks) {
		_decks = decks;
	}

	public void initialize() {
		_shoe = new Shoe();
		Deck deck; 
		for (int i =0; i < _decks; i++) {
			deck = new Deck();
			_shoe.addDeck(deck);
		}
		_shoe.shuffle();
	}

	public void dealCard(PlayerInGame player) {
		player.dealCard(_shoe.dealCard());
	}

	public void dealCard(CardCollection cards) {
		cards.add(_shoe.dealCard());
	}
}
