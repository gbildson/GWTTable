package com.cardfight.server.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

//import poker.HandRange.ID;
//import poker.HandRange.PairID;
//import poker.HandRange.SuitedID;
//import poker.HandRange.UnsuitedID;

public class HandCompression {
	private static String rank[] = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
	private static String suit[] = {"s", "h", "d", "c"};
	private static ArrayList<String> allHands = new ArrayList<String>();
	private static HashSet<String> recordedHands = new HashSet<String>();
	private static HashSet<String> uniqueHands   = new HashSet<String>();
	
	public static void main(String args[]) {
		test3Hands();
	}
	
	private static void test3Hands() {
		
		// Generate pairs
		for ( int i = 0; i < rank.length; i++) {
			for ( int s1 = 0; s1 < suit.length; s1++ ) {
				for ( int s2 = s1 + 1; s2 < suit.length; s2++ ) {
					allHands.add(rank[i]+suit[s1]+rank[i]+suit[s2]);
				}
			}
		}
		//allHands.add("2s2h");
		//allHands.add("2s2d");
		//allHands.add("2s2c");
		//allHands.add("2h2d");
		//allHands.add("2h2c");
		//allHands.add("2d2c");

		
		// Generate suited 
		for ( int i = 0; i < rank.length; i++) {
			//if ( !rank[i].equals("4") ) continue;
			for ( int j = i+1; j < rank.length; j++) {
				//if ( !rank[j].equals("3") ) continue;
				for ( int s1 = 0; s1 < suit.length; s1++ ) {
					allHands.add(rank[i]+suit[s1]+rank[j]+suit[s1]);
				}
			}
		}
		
		// Generate unsuited
		for ( int i = 0; i < rank.length; i++) {
			//if ( !rank[i].equals("4") ) continue;
			for ( int j = i+1; j < rank.length; j++) {
				//if ( !rank[j].equals("3") ) continue;
				for ( int s1 = 0; s1 < suit.length; s1++ ) {
					for ( int s2 = 0; s2 < suit.length; s2++ ) {
						if ( s1 != s2  )  
							allHands.add(rank[i]+suit[s1]+rank[j]+suit[s2]);
					}
				}
			}
		}
		
		// Generate all 2 handed
		long t1 = System.currentTimeMillis();
		String hand1 = "";
		String hand2;
		String hand3;
		System.out.println(" all Hands: "+ allHands);

		for ( int i = 0; i < allHands.size(); i++ ) {
			//System.out.println(hand1+" unique: "+uniqueHands.size());
			hand1 = allHands.get(i);
			//uniqueHands   = new HashSet<String>();
			for ( int j = i+1; j < allHands.size(); j++ ) {
				hand2 = allHands.get(j);
				for ( int k = j+1; k < allHands.size(); k++ ) {
					hand3 = allHands.get(k);
					ArrayList<String> hands = new ArrayList<String>(3);
					hands.add(hand1);
					hands.add(hand2);
					hands.add(hand3);
					if ( hasOverlap(hands) ) continue;
					String uniqueHand = buildHand(hands);
					System.out.println(uniqueHand);
				}
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println("rec: "+recordedHands.size() + " unique: "+uniqueHands.size()+ " t2-t1: " +(t2-t1));
	}
	
	private static boolean hasOverlap(ArrayList<String> hands) {
		HashSet<String> comp = new HashSet<String>();
		for (String hand : hands) {
			String c1 = hand.substring(0,2);
			String c2 = hand.substring(2,4);
			if ( comp.contains(c1) ) return true;
			comp.add(c1);
			if ( comp.contains(c2) ) return true;
			comp.add(c2);
		}
		
		return false;
	}

	// Old code
	private static void recordHands(ArrayList<String> hands) {
		HashMap<String, String> suits = new HashMap<String, String>();
		HashSet<String> used = new HashSet<String>();
		HashMap<String, String> pairs = new HashMap<String, String>();
		HashMap<String, Integer> notTogether = new HashMap<String, Integer>();


		String fullHand   = "";
		String uniqueHand = "";
		int suitNum = 1;
		String smap1;
		String smap2;
		int    count = 0;

		for (String hand : hands) {
			String r1 = hand.substring(0,1);
			String s1 = hand.substring(1,2);
			String r2 = hand.substring(2,3);
			String s2 = hand.substring(3,4);
			boolean pair         = r1.equals(r2);
			boolean unsuited     = !pair && !s1.equals(s2);
			boolean mappablePair = true;
			
			if ( (smap1 = suits.get(s1)) == null ) {
				smap1 = "" + suitNum++;
				suits.put(s1, smap1);
			} else {  
				mappablePair = false;
			}
			
			if ( (smap2 = suits.get(s2)) == null ) {
				smap2 = "" + suitNum++;
				suits.put(s2, smap2);
			} else {  
				mappablePair = false;
				// Normalize the suit numbers to see if they are possibly from same pair hand
				int i1 = Integer.parseInt(smap1);
				int i2 = Integer.parseInt(smap2);
				if ( i1 % 2 == 1 )
					i1--;
				else if (i2 % 2 == 1)
					i2--;
				// Only map as unusable if hand uses both suits of a pair.
				//System.out.println("un test: "+r1+s1+r2+s2+" int :"+i1 + " "+i2);
				if ( i1 == i2 ) {
					used.add(smap2);
					used.add(smap1);
				}
			}
			if ( pair && mappablePair ) {
				//System.out.println("PAIR : "+smap2 +" to: "+smap1);
				pairs.put(smap2, smap1);
			}
			//Don't map if both suits are used together prior to this hand
			if ( !s1.equals(s2) ) {
				String bothSuits = getSuitPair(s1, s2);
				if ( notTogether.get(bothSuits) == null )
					notTogether.put(bothSuits, count);
			}
			count++;
			//fullHand   += hand;
			//uniqueHand += r1 + smap1 + r2 + smap2;
		}
		
		count = 0;
		suitNum = 1;
		suits = new HashMap<String, String>();
		for (String hand : hands) {
			String r1 = hand.substring(0,1);
			String s1 = hand.substring(1,2);
			String r2 = hand.substring(2,3);
			String s2 = hand.substring(3,4);
			Boolean s1mapped = false;
			if ( (smap1 = suits.get(s1)) == null ) {
				smap1 = "" + suitNum++;
				suits.put(s1, smap1);
			} else {  
				s1mapped = true;
			}
			
			if ( (smap2 = suits.get(s2)) == null ) {
				smap2 = "" + suitNum++;
				suits.put(s2, smap2);
			} else {  
				
				//Don't map if both suits are used together prior to this hand
				boolean isMappable = true;
				if ( !s1.equals(s2) ) {
					String bothSuits = getSuitPair(s1, s2);
					Integer idx;
					if (  (idx = notTogether.get(bothSuits)) != null ) {
						int index = idx;
						if ( idx < count ) {
							isMappable = false;
						}
					}
				}
				
				if ( isMappable ) {
					String sswitch = pairs.get(smap2);
					boolean b1 = (sswitch != null);
					boolean b2 = ( !used.contains(sswitch));
					//System.out.println("b1: "+b1+ " b2: "+b2);
					if ( sswitch != null && !used.contains(sswitch) ) {
						//System.out.println("Switched :" +r2+smap2 +" to: "+r2+sswitch);
						smap2 = sswitch;
					}

					if ( s1mapped ) {
						sswitch = pairs.get(smap1);
						b1 = (sswitch != null);
						b2 = ( !used.contains(sswitch));
						//System.out.println("b1: "+b1+ " b2: "+b2);
						if ( sswitch != null && !used.contains(sswitch) ) {
							//System.out.println("Switched :" +r1+smap1 +" to: "+r1+sswitch);
							smap1 = sswitch;
						}
					}
				}
				
				// if hand 3 only mapped to hand 2 and 1 equiv to 2, map to hand 1 where equiv implies pair or suited
			}
			
			fullHand   += hand;
			uniqueHand += r1 + smap1 + r2 + smap2;
			count++;
		}

		
		/*if ( recordedHands.contains(fullHand) ) {
			System.out.println("Dup on : "+fullHand);
		} else {
			recordedHands.add(fullHand);
		}*/
		//uniqueHands.add(uniqueHand);
		//if ( uniqueHands.size() % 100000 == 0 ) System.out.println("Unique size: "+uniqueHands.size());
		//System.out.println(uniqueHand  + " full: "+fullHand);
		System.out.println(uniqueHand);
	}
	// Old Code
	
	private static String getSuitPair( String s1, String s2 ) {
		return (s1.hashCode() > s2.hashCode()) ? s1+s2 : s2+s1;
	}
	
	public static String buildHand(ArrayList<String> hands) {
		HashMap<String, String> suits = new HashMap<String, String>();
		HashSet<String> used = new HashSet<String>();
		HashMap<String, String> pairs = new HashMap<String, String>();
		HashMap<String, Integer> notTogether = new HashMap<String, Integer>();
		
		suitCounts = new HashMap<String, Integer>();
		suitHints  = new HashMap<String, Integer>();
		handIDs    = new ArrayList<ID>();

		debugStr = "";
		String fullHand   = "";
		String uniqueHand = "";
		int suitNum = 1;
		String smap1;
		String smap2;
		int    count = 0;

		for (String hand : hands) {
			String r1 = hand.substring(0,1);
			String s1 = hand.substring(1,2);
			String r2 = hand.substring(2,3);
			String s2 = hand.substring(3,4);
			boolean pair         = r1.equals(r2);
			boolean unsuited     = !pair && !s1.equals(s2);
			boolean mappablePair = true;
			boolean suit1Orig = false;
			boolean suit2Orig = false;
			int     suit1Hint = 0;
			int     suit2Hint = 0;
			
			
			if ( (smap1 = suits.get(s1)) == null ) {
				smap1 = "" + suitNum++;
				suits.put(s1, smap1);
				suitCounts.put(smap1, 0);
				suitHints.put(smap1, 0);
				suit1Orig = true;
			} else {  
				int scount = suitCounts.get(smap1);
				scount++;
				suitCounts.put(smap1, scount);
				suitHints.put(smap1, 1);
			}
			
			if ( (smap2 = suits.get(s2)) == null ) {
				smap2 = "" + suitNum++;
				suits.put(s2, smap2);
				suitCounts.put(smap2, 0);
				suitHints.put(smap2, 0);
				suit2Orig = true;
			} else {  
				int scount = suitCounts.get(smap2);
				scount++;
				suitCounts.put(smap2, scount);
				suitHints.put(smap2, 2);
			}
			if (pair) {
				PairID p = new PairID();
				p.rank = parseRank(r1);
				p.suitID1 = Integer.parseInt(smap1);
				p.suitID2 = Integer.parseInt(smap2);
				p.suit1Originator = suit1Orig;
				p.suit2Originator = suit2Orig;
				handIDs.add(p);
			} else if ( unsuited ) {
				UnsuitedID p = new UnsuitedID();
				p.rank1 = parseRank(r1);
				p.rank2 = parseRank(r2);
				p.suitID1 = Integer.parseInt(smap1);
				p.suitID2 = Integer.parseInt(smap2);
				p.suit1Originator = suit1Orig;
				p.suit2Originator = suit2Orig;
				handIDs.add(p);
			} else {
				SuitedID p = new SuitedID();
				p.rank1 = parseRank(r1);
				p.rank2 = parseRank(r2);
				p.suitID = Integer.parseInt(smap1);
				p.suitOriginator = suit1Orig;
				handIDs.add(p);
			}
			fullHand += hand;
		}
		
		
		suitMap = new HashMap<String, String>();
		Collections.sort(handIDs);
		suitNum = 1;

		for (ID handID : handIDs) {
			suitNum = handID.mapSuits(suitNum);
		}
		debugStr += suitMap + "\n";
		//System.out.println("hand :"+ fullHand);
		for (ID handID : handIDs) {
			//System.out.println(handID);
			uniqueHand += handID.toString();
		}
		
		// Make specific swaps
		String r1 = uniqueHand.substring(0,1);
		String r2 = uniqueHand.substring(4,5);
		if ( uniqueHand.equals(r1+"1"+r1+"2"+r2+"2"+r2+"3"+r2+"1"+r2+"4") ) {
			uniqueHand = r1+"1"+r1+"2"+r2+"1"+r2+"3"+r2+"2"+r2+"4";
		} else if ( uniqueHand.equals(r1+"1"+r1+"2"+r2+"3"+r2+"4"+r2+"1"+r2+"2") ) {
			uniqueHand = r1+"1"+r1+"2"+r2+"1"+r2+"2"+r2+"3"+r2+"4";
		}
		
		/*if ( uniqueHand.startsWith("A3A2") )
			System.out.println(uniqueHand  + " full: "+fullHand + "\n"+debugStr);
		else
			System.out.println(uniqueHand);*/

		return uniqueHand;
	}
	
	private static HashMap<String, Integer> suitCounts = new HashMap<String, Integer>();
	private static HashMap<String, Integer> suitHints  = new HashMap<String, Integer>();
	private static ArrayList<ID>            handIDs    = new ArrayList<ID>();
	private static HashMap<String, String>  suitMap;
	private static String                   debugStr   = "";

	
	static class UnsuitedID extends ID {
		public int rank1;
		public int rank2;
		public int suitID1;
		public int suitID2;
		public boolean suit1Originator = false;
		public boolean suit2Originator = false;
		private int suitCount1 = 0;
		private int suitCount2 = 0;
		
		public UnsuitedID() {
			type = UNSUITED_VALUE;
		}
		
		int getValue() {
			if ( suit1Originator )
				suitCount1 = suitCounts.get(""+suitID1);
			if ( suit2Originator )
				suitCount2 = suitCounts.get(""+suitID2);
			
			return (((type * 10 + rank1) * 100 + rank2) * 100 + suitCount1) * 10 + suitCount2;
		}
		
		public String toString() {
			String smap1 = suitMap.get(""+suitID1);
			if (smap1 != null) 
				suitID1 = Integer.parseInt(smap1);
			String smap2 = suitMap.get(""+suitID2);
			if (smap2 != null) 
				suitID2 = Integer.parseInt(smap2);
			return rank[12-rank1] + suitID1 + rank[12-rank2] + suitID2;
		}
	}
	
	static class SuitedID extends ID {
		public int rank1;
		public int rank2;
		public int suitID;
		public boolean suitOriginator = false;
		private int suitCount = 0;
		
		public SuitedID() {
			type = SUITED_VALUE;
		}
		
		int getValue() {
			if ( suitOriginator )
				suitCount = suitCounts.get(""+suitID);
			return (((type * 10 + rank1) * 100 + rank2) * 100 + suitCount) * 10;
		}
		
		public String toString() {
			String smap = suitMap.get(""+suitID);
			if (smap != null) 
				suitID = Integer.parseInt(smap);
			return rank[12-rank1] + suitID + rank[12-rank2] + suitID;
		}
	}
	
	
	static class PairID extends ID {
		public int rank;
		public int suitID1;
		public int suitID2;
		public boolean suit1Originator = false;
		public boolean suit2Originator = false;
		private int suitCount1 = 0;
		private int suitCount2 = 0;
		
		public PairID() {
			type = PAIR_VALUE;
		}
		
		public int mapSuits(int suitNum) {
			if ( suit1Originator ) {
				if ( suitID1 != suitNum ) {
					debugStr += "suitMap1 : " +suitID1 + " " + suitNum+ " rank: "+rank+"\n";
					suitMap.put(""+suitID1,""+suitNum++);
				} else {
					suitNum++;
				}
			}
			if ( suit2Originator ) {
				if ( suitID2 != suitNum ) {
					debugStr += "suitMap2 : " +suitID2 + " " + suitNum+  " s1Orig: " + suit1Originator +" r:"+HandCompression.rank[12-rank]+"\n";
					suitMap.put(""+suitID2,""+suitNum++);
				} else {
					suitNum++;
				}
			}
				
			return suitNum;
		}
		
		int getValue() {
			int suitHints1 = 0;
			int suitHints2 = 0;
			int suitHintAddon = 0;
			if ( suit1Originator ) {
				suitCount1 = suitCounts.get(""+suitID1);
				suitHints1 = suitHints.get(""+suitID1);
			}
			if ( suit2Originator ) {
				suitCount2 = suitCounts.get(""+suitID2);
				suitHints2 = suitHints.get(""+suitID2);
			}
			
			if ( ((suitCount1 < suitCount2) || (suitCount1 == suitCount2 && suitHints2 < suitHints1)) && 
				  suit1Originator == suit2Originator ) {
				int temp = suitCount1;
				suitCount1 = suitCount2;
				suitCount2 = temp;
				temp = suitID1;
				suitID1 = suitID2;
				suitID2 = temp;
				debugStr += "Switched : " +suitID1+ " "+ suitID2+"\n";
			}
			if ( suitHints1 + suitHints2 > 0 ) {
				suitHintAddon = 6 - suitHints1 - suitHints2;
			}

			return (((((type * 10) + rank) * 100 * 100) + suitCount1) * 10 + suitCount2) * 10 + suitHintAddon;
		}
		
		public String toString() {
			String smap1 = suitMap.get(""+suitID1);
			if (smap1 != null) {
				//System.out.println("Using : "+suitID1+ " "+ smap1);
				suitID1 = Integer.parseInt(smap1);
			}
			String smap2 = suitMap.get(""+suitID2);
			if (smap2 != null) {
				//System.out.println("Using : "+suitID2+ " "+ smap2);
				suitID2 = Integer.parseInt(smap2);
			}
			if ( suitID1 > suitID2) {
				int temp = suitID1;
				suitID1 = suitID2;
				suitID2 = temp;
				debugStr += "toString switched : " +suitID1+ " "+ suitID2+"\n";
			}
			
			return HandCompression.rank[12-rank] + suitID1 + HandCompression.rank[12-rank] + suitID2;
		}
	}
	
	abstract static class ID implements Comparable {
		protected static final int PAIR_VALUE     = 3;
		protected static final int SUITED_VALUE   = 2;
		protected static final int UNSUITED_VALUE = 1;
		protected int type;

		
		public int compareTo(Object o) {
			ID id = (ID) o;
			//System.out.println("type: " +type+ " v: "+getValue());
			//System.out.println("o type: " +id.type+ " v: "+id.getValue());

			return id.getValue() - getValue();
		}
		
		abstract int getValue();
		public int mapSuits(int count) {
			return count;
		}
	}
	
	  private static int getIndexOf(String c) {
		  for ( int i = 0; i < rank.length; i++ )
			  if ( rank[i].equals(c) )
				  return i;
		  return -1;
	  }
	  
	  private static int parseRank(String rank) {
		  int idx = getIndexOf(rank);
		  return (12 - idx);
	  }

}
