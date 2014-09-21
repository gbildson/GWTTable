
package com.cardfight.client.poker;

import com.google.gwt.user.client.rpc.IsSerializable;


public class Card implements IsSerializable {
	public static final int TWO       = 0;
	public static final int THREE     = 1;
	public static final int FOUR      = 2;
	public static final int FIVE      = 3;
	public static final int SIX       = 4;
	public static final int SEVEN     = 5;
	public static final int EIGHT     = 6;
	public static final int NINE      = 7;
	public static final int TEN       = 8;
	public static final int JACK      = 9;
	public static final int QUEEN     = 10;
	public static final int KING      = 11;
	public static final int ACE       = 12;
	private static final String ranks[] = {
	  "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"
	};

	private int _rank;
	private int _suit;
	
	public Card() {
	}

	public static Card create(int suit, int rank) {
		Card card = new Card();
		card._suit = suit;
		card._rank = rank;
		return card;
	}
	
	public static int parseRank(String rank) {
		int irank = -1;
		for(int i = 0; i < ranks.length; i++){
			if (ranks[i].equals(rank)) {
				irank = i;
				break;
			}
		}
		return irank;
	}
	
	public static Card create(String stringRep) {
		if (stringRep.length() != 2) {
			//System.out.println("cardStr:"+stringRep);
			return null;
		}
		
		String rank = stringRep.substring(0,1);
		String suit = stringRep.substring(1,2);
		int irank = -1;
		int isuit = -1;
		for(int i = 0; i < ranks.length; i++){
			if (ranks[i].equals(rank)) {
				irank = i;
				break;
			}
		}
		
		for(int i = 0; i < Suit.suitName.length; i++) {
			if (Suit.suitName[i].equals(suit)) {
				isuit = i;
				break;
			}
		}
		//System.out.println("sc:"+stringRep+" is:"+isuit+" ir:"+irank);
		return create(isuit, irank);
	}

	public int getSuit() {
		return _suit;
	}

	public int getRank() {
		return _rank;
	}

	public String getFaceValueString() {
		return ranks[_rank];
	}

	public String getSuitName() {
		return Suit.suitName[_suit];
	}
	
    public boolean equals(Object o) {
		Card other = (Card) o;
		return (other._rank == _rank && other._suit == _suit);
    }

}

