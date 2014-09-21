package com.cardfight.server.poker;

import java.util.ArrayList;

import com.cardfight.client.poker.Card;
import com.cardfight.client.poker.CardCollection;

public class BestChances {
	public static void main(String args[]) {
		//CardCollection p1hand;		
		//CardCollection p2hand;
		//CardCollection common = new CardCollection();
		
		if (args.length < 2) {
			throw new RuntimeException("Invalid Input");
		}
		
		String p1string = args[0];
		String p2string = args[1];
		String commonCards;
		
		if (args.length < 3) 
			commonCards = null;
		else
			commonCards = args[2];
		
		if (args.length > 3 && "-d".equals(args[3])) {
			// Detailed response at each stage
			String trimmedCommon;

			calculateOdds(p1string, p2string, null);
			
			if (commonCards != null && commonCards.length() >= 6) {
				trimmedCommon = commonCards.substring(0,6);
				//System.out.println("trim="+trimmedCommon);
				calculateOdds(p1string, p2string, trimmedCommon);
			}
			if (commonCards != null && commonCards.length() >= 8) {
				trimmedCommon = commonCards.substring(0,8);
				calculateOdds(p1string, p2string, trimmedCommon);
			}
			if (commonCards != null && commonCards.length() >= 10) {
				trimmedCommon = commonCards.substring(0,10);
				calculateOdds(p1string, p2string, trimmedCommon);
			}
			
		} else {
			// One set of odds only
			calculateOdds(p1string, p2string, commonCards);
			
			/*
			ArrayList<CardCollection> hands = new ArrayList<CardCollection>();
			hands.add(createCards(p1string));
			hands.add(createCards(p2string));
			calculateOddsAndPrint(hands, (commonCards == null) ? null: createCards(commonCards));*/
		}
	}
	static class Multiple {
		public static void main(String args[]) {
			if (args.length < 2) {
				throw new RuntimeException("Invalid Input");
			}
			
			CardCollection commonCards = null;
			ArrayList<CardCollection> hands = new ArrayList<CardCollection>();
			int i = 0;
			for(; i < args.length; i++) {
				if (args[i].equals("-c"))
					break;
				hands.add(createCards(args[i]));
			}
			if ( i < (args.length-1) )
				commonCards = createCards(args[i+1]);

			calculateOddsAndPrint(hands, commonCards);
		}
	}
	
	public static void calculateOdds(String p1string, String p2string, String commonCards) {
		CardCollection p1hand;		
		CardCollection p2hand;
		CardCollection common = new CardCollection();
		
		if (p1string.length() != 4 || p2string.length() != 4) {
			throw new RuntimeException("Players cards input error");
		}
		p1hand = createCards(p1string);
		p2hand = createCards(p2string);
		
		if (commonCards != null) {
			if (commonCards.length() != 6 && 
				commonCards.length() != 8 && 
				commonCards.length() != 10) {
				throw new RuntimeException("Common cards input error");
			}
			common = createCards(commonCards);
		}
		calculateOddsAndPrint(p1hand, p2hand, common);
	}
	
	private static CardCollection createCards( String cardString ) {
		CardCollection ret = new CardCollection();
		for (int i = 0; (i+2) <= cardString.length(); i += 2) {
			ret.add(Card.create(cardString.substring(i,i+2)));
		}
		return ret;
	}
	
	private static String dec(long num) {
		String snum = String.valueOf(num);
		int  length = snum.length();
		
		if (length < 2)
			return snum;
		
		String begin = snum.substring(0,length-2);
		String end   = snum.substring(length-2, length);
		return begin + "." + end;
	}
		
    public static WinLoseTie calculateOdds( CardCollection p1hand, CardCollection p2hand, CardCollection common) {
    	if (common == null)
    		common = new CardCollection();
    	
		// Assemble all the cards that have been seen
		CardCollection seenCards = new CardCollection();
		seenCards.addAll(p1hand);
		seenCards.addAll(p2hand);
		seenCards.addAll(common);
		
		Deck deck = new Deck();
		Shoe shoe = new Shoe();
		shoe.addDeck(deck);
		
		// Find the remaining cards to be dealt
		CardCollection remainder = new CardCollection();
		for(int i = 0; i < 52; i++) {
			Card card = shoe.dealCard();
			if (!seenCards.contains(card))
				remainder.add(card);
		}
		
		// Run a set of random deals from this point on and count wins
		int p1win = 0;
		int p2win = 0;
		int tie = 0;
		CardCollection sampleDeck;
		CardCollection sampleCommon;
		BestHand bestHand;		
		int sampleSize = 500000;	
		
		BestHand bh1, bh2;
		WinLoseTie stats = new WinLoseTie();

		for(int i= 0; i < sampleSize; i++) {
			sampleCommon = common.copy();
			sampleDeck = remainder.copy();
			sampleDeck.shuffle();
			
			while (sampleCommon.size() < 5) {
				sampleCommon.add(sampleDeck.remove());
			}
			
			ArrayList allHands = new ArrayList(2);
			bestHand = BestHand.makeBestHand(p1hand, sampleCommon);
			bestHand.setUserHook(p1hand);
			allHands.add(bestHand);
			bh1 = bestHand;
			
			bestHand = BestHand.makeBestHand(p2hand, sampleCommon);
			bestHand.setUserHook(p2hand);
			allHands.add(bestHand);
			bh2 = bestHand;
			
			ArrayList winner = BestHand.findBestHand(allHands);
			//System.out.println("old winner size:"+winner.size());

			if ( winner.size() > 1 ) {
				stats.tie++;
				//stats.ties1[bh1.rank]++;
				//stats.ties2[bh2.rank]++;
			} else {
				bestHand = (BestHand) winner.get(0);
				CardCollection phand = (CardCollection) bestHand.getUserHook();
				if (phand == p1hand) {
					p1win++;
					//stats.winner1[bh1.rank]++;
					//stats.loser2[bh2.rank]++;
				} else if (phand == p2hand) {
					p2win++;
					//stats.winner2[bh2.rank]++;
					//stats.loser1[bh1.rank]++;
				} else
					throw new RuntimeException("BestHand choice error");
			}	
		}
		//WinLoseTie stats = new WinLoseTie();
		stats.win1 = p1win;
		stats.win2 = p2win;
		//stats.tie = tie;
		return stats;
	}

    public static void calculateOddsAndPrint(CardCollection p1hand, CardCollection p2hand, CardCollection common) {
    	WinLoseTie stats;
    	stats = calculateOdds(p1hand, p2hand, common);
    	long sampleSize = stats.win1 + stats.win2 + stats.tie;
		System.out.print("P1Hand: "+p1hand+" w: "+dec(stats.win1*10000/sampleSize));
		System.out.print(" P2Hand: "+p2hand+" w: "+dec(stats.win2*10000/sampleSize));
		System.out.print(" Common: "+common);
		System.out.println(" tie: " +dec(stats.tie*10000/sampleSize));
		
		/*
		System.out.print("P1Hand: "+p1hand+" w: "+(stats.win1));
		System.out.print(" P2Hand: "+p2hand+" w: "+(stats.win2));
		System.out.print(" Common: "+common);
		System.out.println(" tie: " +(stats.tie));
		
		System.out.print("P1Winners - "); 
		printAggregate(stats.winner1);
		System.out.print("P1Losers - "); 
		printAggregate(stats.loser1);
		System.out.print("P1Ties - "); 
		printAggregate(stats.ties1);
		System.out.print("P2Winners - "); 
		printAggregate(stats.winner2);
		System.out.print("P2Losers - "); 
		printAggregate(stats.loser2);
		System.out.print("P2Ties - "); 
		printAggregate(stats.ties2); */
    }
    
    public static ArrayList<WinningStats> calculateOdds( ArrayList<CardCollection> hands, CardCollection common) {
    	if (common == null)
    		common = new CardCollection();
    	
		// Assemble all the cards that have been seen
		CardCollection seenCards = new CardCollection();
		for (CardCollection hand : hands) {
			seenCards.addAll(hand);
		}
		seenCards.addAll(common);
		
		Deck deck = new Deck();
		Shoe shoe = new Shoe();
		shoe.addDeck(deck);
		
		// Find the remaining cards to be dealt
		CardCollection remainder = new CardCollection();
		for(int i = 0; i < 52; i++) {
			Card card = shoe.dealCard();
			if (!seenCards.contains(card))
				remainder.add(card);
		}
		
		// Run a set of random deals from this point on and count wins
		CardCollection sampleDeck;
		CardCollection sampleCommon;
		BestHand       bestHand;	
		WinningStats   winner;
		int            windex;
		int            sampleSize = 500000;	
		
		//BestHand bh1, bh2;
		ArrayList<WinningStats> stats = new ArrayList<WinningStats>(hands.size());
		for (int i = 0; i < hands.size(); i++)
			stats.add(new WinningStats());

		for(int i= 0; i < sampleSize; i++) {
			sampleCommon = common.copy();
			sampleDeck = remainder.copy();
			sampleDeck.shuffle();
			
			while (sampleCommon.size() < 5) {
				sampleCommon.add(sampleDeck.remove());
			}
			
			ArrayList allHands = new ArrayList(hands.size());
			int hcount = 0;
			for (CardCollection hand : hands) {
				bestHand = BestHand.makeBestHand(hand, sampleCommon);
				bestHand.setUserHook(new Integer(hcount));
				allHands.add(bestHand);
				hcount++;
			//bh1 = bestHand;
			}
			
			ArrayList winners = BestHand.findBestHand(allHands);
			//System.out.println("winner size:"+winners.size());
			ArrayList<WinningStats> used = new ArrayList<WinningStats>();
			for ( int j = 0; j < winners.size(); j++ ) {
				bestHand = (BestHand) winners.get(j);
				windex = (Integer) bestHand.getUserHook();
				winner = stats.get(windex);
				//System.out.println("winner idx:"+windex);
				//System.out.println("Winner: "+bestHand);

				used.add(winner);
				if ( winners.size() > 1 ) {
					winner.tie++;
				} else {
					winner.win++;
				}
			}
			// Handle losers
			for ( int j = 0; j < hands.size(); j++ ) {
				winner = stats.get(j);
				if ( !used.contains(winner) )
					winner.lose++;
			}
		}
		return stats;
	}
 
    public static void calculateOddsAndPrint( ArrayList<CardCollection> hands, CardCollection common) {
    	ArrayList<WinningStats> stats;
    	stats = calculateOdds(hands, common);
    	for (int i = 0; i < hands.size(); i++) {
    		WinningStats winner = stats.get(i);
    		CardCollection hand = hands.get(i);
    		long sampleSize = winner.win + winner.lose + winner.tie;
    		System.out.print("P"+(i+1)+"Hand: "+hand+" w: "+dec(winner.win*10000/sampleSize));
    		System.out.print("\tl: "+dec(winner.lose*10000/sampleSize));
    		System.out.println("\tt: "+dec(winner.tie*10000/sampleSize));
    		System.out.print("\t Raw"+" w: "+(winner.win));
    		System.out.print("\tl: "+(winner.lose));
    		System.out.println("\tt: "+(winner.tie));

    		/*
		System.out.print("P1Hand: "+p1hand+" w: "+(stats.win1));
		System.out.print(" P2Hand: "+p2hand+" w: "+(stats.win2));
		System.out.print(" Common: "+common);
		System.out.println(" tie: " +(stats.tie));

		System.out.print("P1Winners - "); 
		printAggregate(stats.winner1);
		System.out.print("P1Losers - "); 
		printAggregate(stats.loser1);
		System.out.print("P1Ties - "); 
		printAggregate(stats.ties1);
		System.out.print("P2Winners - "); 
		printAggregate(stats.winner2);
		System.out.print("P2Losers - "); 
		printAggregate(stats.loser2);
		System.out.print("P2Ties - "); 
		printAggregate(stats.ties2); */
    	}
    }
    
    public static void printAggregate(long[] results) {
    	for (int i = 0; i < 10; i++) {
    		System.out.print(i + ":" + results[i] + " ");
    		if (i > 0 && (i % 5) == 0 )
    			System.out.println();
    	}
    	System.out.println();
    }
    
}
