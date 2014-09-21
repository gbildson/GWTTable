
package com.cardfight.server.poker;

import java.util.*;

import com.cardfight.client.poker.Card;
import com.cardfight.client.poker.CardCollection;

public class BestHand extends CardCollection implements Comparable {
	public static final int ROYAL_FLUSH       = 9;
	public static final int STRAIGHT_FLUSH    = 8;
	public static final int FOUR_OF_A_KIND    = 7;
	public static final int FULL_HOUSE        = 6;
	public static final int FLUSH             = 5;
	public static final int STRAIGHT          = 4;
	public static final int THREE_OF_A_KIND   = 3;
	public static final int TWO_PAIR          = 2;
	public static final int ONE_PAIR          = 1;
	public static final int HIGH_CARD         = 0;

	public static final String rankString[] = {
	  "high card",
	  "one pair",
	  "two pair",
	  "three of a kind",
	  "straight",
	  "flush",
	  "full house",
	  "four of a kind",
	  "straight flush",
      "royal flush"};

	private static final String BLANK = "                                    ";

	public int rank;
	private int cardRank = 0;

	private Object userHook;
	
	public Object getUserHook() {
		return userHook;
	}
	
	public void setUserHook(Object hook) {
		userHook = hook;
	}

	public static BestHand makeBestHand(CardCollection cards1, 
	  									CardCollection cards2) {
		BestHand bestHand = new BestHand();
		CardCollection cards = new CardCollection();
		cards.addAll(cards1);
		if ( cards2 != null )
			cards.addAll(cards2);

		Card card;
		int suits[] = new int[4];
		int biggestSuit = -1;
		int numInSuit   = -1;

		// Find most common suit
		for(int i = 0; i < cards.size(); i++) {
			card = cards.get(i);
			suits[card.getSuit()]++;
		}
		for(int i = 0; i < suits.length; i++) {
			if ( suits[i] > numInSuit ) {
				biggestSuit = i;
				numInSuit   = suits[i];
			}
		}
//System.out.println("cards="+cards.size()+" numInSuit="+numInSuit);

		// First Look for Royal Flush / Straight Flush
		Card ranks[] = new Card[13];
		int  highestRank = -1;
		if ( numInSuit >= 5 ) {
			// Place cards in a row
			for(int i = 0; i < cards.size(); i++) {
				card = cards.get(i);
				if (card.getSuit() == biggestSuit) {
					ranks[card.getRank()] = card;
				}
			}
			// Look for Royal Flush / Straight Flush
			for(int i = Card.ACE; i >= 3; i--) {
				card = ranks[i];
				if (card != null) {
					// Look for 4 more cards
					highestRank = card.getRank();
			        for(int j = highestRank-1; j >= highestRank-4; j--) {
						int num = j;
						if ( num == -1 ) num = card.ACE;
				        card = ranks[num];
						if (card == null) break;
					}
					if ( card != null ) { 
						// We have a straight flush of some kind
						if (highestRank == card.ACE) {
							bestHand.rank = BestHand.ROYAL_FLUSH;
						} else {
							bestHand.rank = BestHand.STRAIGHT_FLUSH;
						}
						for(int j = highestRank; j >= highestRank-4; j--) {
							int num = j;
							if ( num == -1 ) num = card.ACE;
							card = ranks[num];
						    bestHand.add(card);
						}
						return bestHand;
					}
				}
			}
		}

		// Setup a pairing data structure
		CardCollection pairing[] = new CardCollection[Card.ACE+1];
		for(int i = 0; i < pairing.length; i++) {
			pairing[i] = new CardCollection();
		}
		for(int i = 0; i < cards.size(); i++) {
			card = cards.get(i);
			pairing[card.getRank()].add(card);
		}

		// Look for 4 of a kind
		int bestRank       = -1;
		int secondBestRank = -1;
		int highCard       = -1;
		for(int i = Card.ACE; i >= Card.TWO; i--) {
			if (pairing[i].size() == 4 ) {
				bestRank = i;
				break;
			}
		}
		if ( bestRank != -1 ) {
			for(int i = Card.ACE; i >= Card.TWO; i--) {
				if (pairing[i].size() > 0 && i != bestRank ) {
					highCard = i;
					break;
				}
			}
			bestHand.rank = BestHand.FOUR_OF_A_KIND;
			bestHand.add(pairing[bestRank].get(0));
			bestHand.add(pairing[bestRank].get(1));
			bestHand.add(pairing[bestRank].get(2));
			bestHand.add(pairing[bestRank].get(3));
			bestHand.add(pairing[highCard].get(0));
			return bestHand;
		}

		// Look for Full House
		for(int i = Card.ACE; i >= Card.TWO; i--) {
			if (pairing[i].size() == 3 ) {
				bestRank = i;
				break;
			}
		}
		for(int i = Card.ACE; i >= Card.TWO; i--) {
			if (pairing[i].size() >= 2 && i != bestRank ) {
				secondBestRank = i;
				break;
			}
		}
		if ( bestRank != -1 && secondBestRank != -1 ) {
			bestHand.rank = BestHand.FULL_HOUSE;
			bestHand.add(pairing[bestRank].get(0));
			bestHand.add(pairing[bestRank].get(1));
			bestHand.add(pairing[bestRank].get(2));
			bestHand.add(pairing[secondBestRank].get(0));
			bestHand.add(pairing[secondBestRank].get(1));
			return bestHand;
		}

		// Look for flush
		if ( numInSuit >= 5 ) {
			bestHand.rank = BestHand.FLUSH;
			for(int i = Card.ACE; i >= Card.TWO; i--) {
				card = ranks[i];
				if (card != null) {
					bestHand.add(card);
//System.out.println("bestHand="+bestHand);
					if (bestHand.size() == 5)
						return bestHand;
				}
			}
		}

		// Look for any straight
		for(int i = 0; i < cards.size(); i++) {
			card = cards.get(i);
			ranks[card.getRank()] = card;
		}
		for(int i = Card.ACE; i >= 3; i--) {
			card = ranks[i];
			if (card != null) {
				// Look for 4 more cards
				highestRank = card.getRank();
				for(int j = highestRank-1; j >= highestRank-4; j--) {
					int num = j;
					if ( num == -1 ) num = card.ACE;
					card = ranks[num];
					if (card == null) break;
				}
				if ( card != null ) { 
					// We have a straight 
					bestHand.rank = BestHand.STRAIGHT;
					for(int j = highestRank; j >= highestRank-4; j--) {
						int num = j;
						if ( num == -1 ) num = card.ACE;
						card = ranks[num];
						bestHand.add(card);
					}
					return bestHand;
				}
			}
		}

		// Look for three of a kind
		int hccount=0;
		int hclist[] = new int[2];
		if ( bestRank != -1 ) {
			for(int i = Card.ACE; i >= Card.TWO; i--) {
				if (pairing[i].size() > 0 && i != bestRank ) {
					hclist[hccount++] = i;
					if (hccount == 2) break;
				}
			}
			bestHand.rank = BestHand.THREE_OF_A_KIND;
			bestHand.add(pairing[bestRank].get(0));
			bestHand.add(pairing[bestRank].get(1));
			bestHand.add(pairing[bestRank].get(2));
			bestHand.add(pairing[hclist[0]].get(0));
			bestHand.add(pairing[hclist[1]].get(0));
			return bestHand;
		}

		// Look for two pair
		secondBestRank = -1;
		highCard       = -1;
		for(int i = Card.ACE; i >= Card.TWO; i--) {
			if (pairing[i].size() == 2 ) {
				bestRank = i;
				break;
			}
		}
		if ( bestRank != -1 ) {
			for(int i = bestRank-1; i >= Card.TWO; i--) {
				if (pairing[i].size() == 2 ) {
					secondBestRank = i;
					break;
				}
			}
			if ( secondBestRank != -1 ) {
				for(int i = Card.ACE; i >= Card.TWO; i--) {
					if (pairing[i].size() > 0 && i != bestRank && 										i != secondBestRank ) {
						highCard = i;
						break;
					}
				}
				bestHand.rank = BestHand.TWO_PAIR;
				bestHand.add(pairing[bestRank].get(0));
				bestHand.add(pairing[bestRank].get(1));
				bestHand.add(pairing[secondBestRank].get(0));
				bestHand.add(pairing[secondBestRank].get(1));
				bestHand.add(pairing[highCard].get(0));
				return bestHand;
			}
		}

		// Look for one pair
		if ( bestRank != -1 ) {
			// Get 3 high cards
			hccount=0;
			hclist = new int[3];
			for(int i = Card.ACE; i >= Card.TWO; i--) {
				if (pairing[i].size() == 1) {
					hclist[hccount++] = i;
					if (hccount == 3) break;
				}
			}
			bestHand.rank = BestHand.ONE_PAIR;
			bestHand.add(pairing[bestRank].get(0));
			bestHand.add(pairing[bestRank].get(1));
			bestHand.add(pairing[hclist[0]].get(0));
			bestHand.add(pairing[hclist[1]].get(0));
			bestHand.add(pairing[hclist[2]].get(0));
			return bestHand;
		}

		// Determine 5 high cards
		hccount=0;
		hclist = new int[5];
		for(int i = Card.ACE; i >= Card.TWO; i--) {
			if (pairing[i].size() == 1) {
				hclist[hccount++] = i;
				if (hccount == 5) break;
			}
		}
		bestHand.rank = BestHand.HIGH_CARD;
		bestHand.add(pairing[hclist[0]].get(0));
		bestHand.add(pairing[hclist[1]].get(0));
		bestHand.add(pairing[hclist[2]].get(0));
		bestHand.add(pairing[hclist[3]].get(0));
		bestHand.add(pairing[hclist[4]].get(0));
		return bestHand;
	}

	public static ArrayList findBestHand(ArrayList allHands) {
		BestHand prevHand = null;
		BestHand curHand;
		ArrayList best = new ArrayList();

		/*
		// Allocate a hierarchy of best hands
		ArrayList ranks[] = new ArrayList[ROYAL_FLUSH+1];	
		for (int i = 0; i < ranks.length; i++) {
			ranks[i] = new ArrayList();
		}

		// Fill hands into hierarchy
		for (int i = 0; i < allHands.size(); i++) {
			curHand = (BestHand) allHands.get(i);
			ranks[curHand.rank].add(curHand);
		}

		// Find overall best hand
		for (int i = ROYAL_FLUSH; i >= HIGH_CARD; i--) {
			if ( ranks[i].size() > 0 ) {
				if ( ranks[i].size() == 1 ) {
					best.add(ranks[i].get(0));
					return best;
				}

				// Compare hands to see if best can be determined
			}
		}
		*/
		Collections.sort(allHands);
		int comp = 0;
		for (int i = allHands.size()-1; i >= 0 ; i--) {
			curHand = (BestHand) allHands.get(i);
			if ( prevHand != null ) {
				if (!prevHand.equals(curHand))
					return best;
			}	
			prevHand = curHand;
			best.add(curHand);
		}
		return best;
	}

	public void add(Card card) {
		super.add(card);
		cardRank = cardRank * 16 + card.getRank();
	}

    public int compareTo(Object o) {
        BestHand other = (BestHand)o; 

        if(rank > other.rank)
        return 1;
        if(rank < other.rank)
        return -1;

        if(cardRank > other.cardRank)
        return 1;
        if(cardRank < other.cardRank)
        return -1;

        return 0;
    }

    public boolean equals(Object o) {
		return (compareTo(o) == 0);
    }

	private String pad(String str, int size) {
		return str + BLANK.substring(0, size-str.length());
	}

	public String toString() {
		String ret = super.toString();
		//return pad(rankString[rank],15) +" - "+ ret;
		return rankString[rank]+" ("+ ret + ")";
	}
}
