
package com.cardfight.client.poker;

import java.util.*;
import com.google.gwt.user.client.rpc.IsSerializable;


public class CardCollection implements IsSerializable {
	protected ArrayList _cards;

	public CardCollection() {
		clear();
	}
	
	public CardCollection copy() {
		CardCollection newCards = new CardCollection();
		newCards.addAll(this);
		return newCards;
	}

	public void clear() {
		_cards = new ArrayList();
	}

	public int size() {
		return _cards.size(); 
	}

	public ArrayList getCards() {
		return _cards;
	}

	public void shuffle() {
		//Collections.shuffle(_cards);
	}
	
	public boolean contains(Card card) {
		return _cards.contains(card);
	}

	public void add( Card card ) {
		_cards.add(card);
	}

	public void addAll(CardCollection cards) {
		_cards.addAll(cards.getCards());	
	}

	public Card get( int cardNum ) {
		return (Card) _cards.get(cardNum);
	}
	
	public Card remove() {
		return (Card) _cards.remove(0);
	}

	public String toString() {
		Card   card;
		String str  = "";
	
		for(int i=0; i < _cards.size(); i++) {
			card = (Card) _cards.get(i);
			str += card.getFaceValueString() + card.getSuitName();
			//if ( i != _cards.size()-1 )
			    //str += "";
		}

		return str + "";
	}
}
