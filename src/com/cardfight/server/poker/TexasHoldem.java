package com.cardfight.server.poker;

import java.util.*;
import java.io.*;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.CommonCards;
import com.cardfight.client.poker.Hand;
import com.cardfight.client.poker.PlayerInGame;
import com.cardfight.client.poker.PotWinner;
import com.cardfight.client.table.PotTotal;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.poker.ActionCallback;
import com.cardfight.server.poker.VisualFeedbackEventQueue;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


public class TexasHoldem implements SyncCallback {

	//private static final Log LOG = LogFactory.getLog(TexasHoldem.class);

	public static final int LIMIT     = 1;
	public static final int NO_LIMIT  = 2;
	public static final int POT_LIMIT = 3;
	
	private static final CardCollection NO_CARDS = new CardCollection();

	// Game Parameters
	boolean _activeSitOut = true;
	boolean _automatedBetting = false;
	boolean _useGUI           = true;
	int     _bettingMode;

	Table   _table;
	double  _smallBlind;
	double  _bigBlind;
	double  _ante;
	boolean _gameInProgress;
	boolean commonCardsVisible = false;
	int     _bettingRound;
	int     _betsInRound;
	Dealer  _dealer;
	VisualFeedback _vfeedback;
	private boolean _playedRound = false;
	private double  _smallestChip;
	private int     bigBlindPosition;

	public TexasHoldem( int numPlayers, double smallBlind, double bigBlind, 
	  double ante, double smallestChip, int bettingMode ) {
		_smallBlind   = smallBlind;
		_bigBlind     = bigBlind;
		_ante         = ante;
		_smallestChip = smallestChip;
		_bettingMode  = bettingMode;
		_table        = new Table(numPlayers);
		_dealer       = new Dealer(1);
	}

	public void setVisualFeedback( VisualFeedback vf ) {
		_vfeedback = vf;
	}

	public void initialize() {
		_table.initialize();
		_dealer.initialize();
		initializePlayers();
		_bettingRound = 0;
		_betsInRound  = 0;
		if (_vfeedback != null)
			_vfeedback.initRound();
	}

	public void setAutomatedBetting(boolean automate) {
		_automatedBetting = automate;
	}

	public void takeSeat(int seat, PlayerInGame player) {
		_table.takeSeat(seat, player);
		if (_vfeedback != null) {
			_vfeedback.setPlayer(seat, player.getNick());
			setPlayerAmount(_vfeedback, seat, player, 0);
			sleep(100);
		}
	} 
	
	public void removePlayer(String nick) {
		int tableSize = _table.getSize();
		PlayerInGame player;
		int seat = -1;
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			if ( player.getNick().equals(nick) ) {
				seat = i;
				_table.standUp(seat,player);
				break;
			}
		}
		if (_vfeedback != null && seat != -1) {
			_vfeedback.setPlayer(seat, _vfeedback.TAKE_SEAT);
			//setPlayerAmount(_vfeedback, seat, player, 0);
			sleep(100);
		}
	} 
		
	public void initializePlayers() {
		int tableSize = _table.getSize();
		PlayerInGame player;
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			player.initialize();
		}
	}
		
	private static final double       DEFAULT_SMALL = 999999999999.99;
	private static final double       DEFAULT_LARGE = -1.0;
	public void addToPot() {
		int tableSize = _table.getSize();

		PlayerInGame player;
		double       smallestBet = DEFAULT_SMALL;
		double       largestBet  = DEFAULT_LARGE;
		double       curBet;
		boolean      allInBet    = false;

		// Break bets with allins into appropriate pots
		for( ;; ) {
			
			
			// Find smallest/largest bet this iteration
			for (int i = 0; i < tableSize; i++) {
				player = _table.getPlayer(i);

				if (player == null) continue;

				if (!player.isActive()) continue;

				curBet = player.getBetInRound();

				if ( curBet == 0.0 && !player.isPlaying() ) continue;

				if ( player.getState() == PlayerInGame.ALL_IN) 
					allInBet = true;

				if ( curBet < smallestBet ) {
					smallestBet = curBet;
				} 
				if ( curBet > largestBet ) {
					largestBet = curBet;
				} 
			}

			if ( smallestBet == DEFAULT_SMALL ) break;

			// Determine if everyone folded to a bet at the end of this round
			int numParticipants = 0;
			PlayerInGame activePlayer = null;
			for (int i = 0; i < tableSize; i++) {
				player = _table.getPlayer(i);

				if (player == null) continue;

				curBet = player.getBetInRound();

				if (!player.isActive() || (player.isActive() && curBet <= 0)) 
					continue;

				activePlayer = player;
				numParticipants++;
			}
			
			// If everyone did fold to the last bet then give the active player the bets on table
			if (numParticipants == 1) {
				for (int i = 0; i < tableSize; i++) {
					player = _table.getPlayer(i);

					if (player == null) continue;

					curBet = player.getBetInRound();

					activePlayer.giveWinnings(curBet);
				}
			} else {
				// Otherwise, add bets to pot
				for (int i = 0; i < tableSize; i++) {
					player = _table.getPlayer(i);

					if (player == null) continue;

					curBet = player.getBetInRound();
					//System.out.println("bet for POT:"+curBet);

					//if (!player.isActive() || (player.isActive() && curBet <= 0)) 
					if (curBet <= 0)
						continue;
					
					// Determine how much was actually put on the table
					double actualBet = Math.min(curBet, smallestBet);  

					//System.out.print("PRIORPOT:"+_table.getPotSize()+" Player:" +i+" actualBet:"+actualBet);
					_table.addToPot(actualBet, player);
					//System.out.println(" CurrentPOT:"+_table.getPotSize() );

				}
			}
			
			if ( (smallestBet == 0.0 || smallestBet == largestBet) && 
				  !allInBet ) {
				break;
			} else {
				// reset initial values
				allInBet = false;
				smallestBet = DEFAULT_SMALL;
				largestBet  = DEFAULT_LARGE;

				// New Pot
				_table.newPot();
			}
		}
		
		// Clear out bets in player state
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			//if (!player.isActive()) continue;
			player.clearBet();
		}
		
		potFeedback(_vfeedback);
	}

	public void takeAnte() {
		if (_ante <= 0)
			return;

		int dealer = _table.getDealerPosition();
		int tableSize = _table.getSize();

		PlayerInGame player;
		double       leftOver;
		for (int i = 0; i < tableSize; i++) {
			int playerNum = (i+dealer+1)%tableSize;
			player = _table.getPlayer(playerNum);

			if (player == null) continue;

			if (!player.isPlaying() && !_activeSitOut) continue;

			if (player.isSittingOut() && player.getMoney() <= 0.0) continue;

			leftOver = player.takeAnte(_ante);
			if ( leftOver > 0 )
				;//_table.addToPot(_ante, player);
			else
				player.setState(PlayerInGame.ALL_IN);
			//else  TODO IMPLEMENT PARTIAL ANTE - Second Pot
			// TODO - Implement Sitting Out
			_vfeedback.setPlayerBetState(playerNum, "ANTE");
		}
		//try { Thread.sleep(400); } catch (InterruptedException ie) {}
	}

	public void takeBlinds() {

		int blindNumber = 0;
		int dealer = _table.getDealerPosition();
		int tableSize = _table.getSize();

		PlayerInGame player;
		double       leftOver;
		for (int i = 0; i < tableSize; i++) {
			int playerNum = (i+dealer+1)%tableSize;
			player = _table.getPlayer(playerNum);

			if (player == null) continue;

			if (!player.isPlaying() && !_activeSitOut) continue;

			if (player.isSittingOut() && player.getMoney() <= 0.0) continue;

			if ( blindNumber == 0 ) { // Small blind
				leftOver = player.takeBlind(_smallBlind, _bigBlind);
				if ( leftOver > 0 )
					;//_table.addToPot(_smallBlind, player);
			    else
				    player.setState(PlayerInGame.ALL_IN);
				//else  TODO IMPLEMENT PARTIAL blind - Second Pot
				// TODO - Implement Sitting Out
				blindNumber++;
				bettingFeedback(_vfeedback);
				_vfeedback.setPlayerBetState(playerNum, "POST SB");
			} else if ( blindNumber == 1 ) { // Big blind
				leftOver = player.takeBlind(_bigBlind, _bigBlind);
				if ( leftOver > 0 )
					;//_table.addToPot(_bigBlind, player);
			    else
				    player.setState(PlayerInGame.ALL_IN);
				//else  TODO IMPLEMENT PARTIAL blind - Second Pot
				// TODO - Implement Sitting Out
				blindNumber++;
				bettingFeedback(_vfeedback);
				_vfeedback.setPlayerBetState(playerNum, "POST BB");
				bigBlindPosition = playerNum;
			} else {
				player.clearBet();
				player.setBigBlind(_bigBlind);
			}
		}
		//try { Thread.sleep(400); } catch (InterruptedException ie) {}
	}

	
	public void takeBets() {
		PlayerInGame player;
		int startPosition;
		int dealer    = _table.getDealerPosition();
		int tableSize = _table.getSize();

		// Initialize highest bet seen by each player
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			if (!player.isPlaying()) continue;

			player.setHighestAskedBet(0.0);
		}

		// Determine who is the first to bet
		if ( _bettingRound == 0 ) {
			startPosition = (bigBlindPosition + 1) % tableSize;
		} else {
			startPosition = (dealer + 1) % tableSize;
		}

		double       leftOver;
		double       tableBet = _bigBlind;  // True for first round
		double       bet;
		double       currentBet;
		double       highestAskedBet;
		double       highestPriorBet = 0;
		double       playerBet;
		double[]     tableBetStructure = new double[1];  // Record the structure of the betting per player in a round
		//double       maxBet = -1;  // No max unless LIMIT
		boolean      betsAllowed = true;
		
		// Ensure that tableBet starts at zero after first round
		if (_bettingRound > 0) {
			tableBet = 0.0d;
			tableBetStructure = new double[0]; 
		} else {
		    tableBetStructure[0] = tableBet;
		}
		
		//if (LOG.isDebugEnabled())
			//LOG.debug("START POSITION:"+startPosition+" TABLESIZE:"+tableSize);

		// Allow 4 raises per betting round for limit.
		// Actually that would be 4x round betting size in case some players were allin.
		for (int j=0; j < 4; j++) {
			// See who wants to bet
			for (int i = 0; i < tableSize; i++) {

				// Done if everyone folded to the Big Blind or checked in the round of betting
				if ( highestPriorBet == 0 && j > 0 ) return;
				
				int playerNum = (i+startPosition)%tableSize;
				player = _table.getPlayer(playerNum);
				
				//if (LOG.isDebugEnabled())
					//LOG.debug("PLAYER:"+playerNum);

				if (player == null) continue;
				
				//if (LOG.isDebugEnabled())
					//LOG.debug("PLAYER B:"+playerNum);

				if (!player.isPlaying()) continue;

				//if (LOG.isDebugEnabled())
					//LOG.debug("PLAYER C:"+playerNum);
				
				highestAskedBet = player.getHighestAskedBet();

				// Make sure that table bet is accurate 
				// from prior betting rounds
				playerBet       = player.getCurrentBet();	
				if (playerBet > tableBet) {
					tableBet = playerBet;
				}

				// Done if everyone has had a chance to bet at a given level
				if ( highestAskedBet >= tableBet && j > 0 ) return;
				
				//if (LOG.isDebugEnabled())
					//LOG.debug("PLAYER D:"+playerNum);

				// TODO - need to enforce only 4 raises per betting round
				//if (_bettingMode == LIMIT) 
				//	maxBet = 
				bet = askBet(player, tableBet, tableBetStructure);
				highestPriorBet = Math.max(bet, highestPriorBet );
				//if (LOG.isDebugEnabled())
					//LOG.debug(" RETURNED BET:"+bet);
				

				
				double incrementalBet = bet - playerBet;
				//if (LOG.isDebugEnabled())
					//LOG.debug(" INCREMENTAL BET:"+incrementalBet);

				if (bet > 0 ) 
					leftOver = player.takeBet(incrementalBet, tableBetStructure);
				else 
					leftOver = player.getMoney();
				
				currentBet = player.getCurrentBet();
				//if (LOG.isDebugEnabled()) {
					//LOG.debug("CURRENTBET:"+currentBet);
					//LOG.debug("TABLEBET:"+tableBet);
					//LOG.debug("PLAYERBET:"+playerBet);
				//}

				if ( currentBet > tableBet) {
					tableBetStructure = player.getAllBets();
					//if (LOG.isDebugEnabled())
						//LOG.debug("NEW TABLEBET: ");
					PlayerInGame.printBets(tableBetStructure);
					_betsInRound++;
					// RAISE
					if (tableBet == 0.0d)
						_vfeedback.setPlayerBetState(playerNum, "BET");
					else
						_vfeedback.setPlayerBetState(playerNum, "RAISE");
					tableBet = currentBet;
				} else if (currentBet > playerBet) {
					// CALL
					_vfeedback.setPlayerBetState(playerNum, "CALL");
				} else if (currentBet == playerBet && bet >= 0) {
					// CHECK
					_vfeedback.setPlayerBetState(playerNum, "CHECK");
				}

				//if ( tableBet > _bigBlind || _bettingRound == 0)
				bettingFeedback(_vfeedback);

				player.setHighestAskedBet(tableBet);

				if ( leftOver > 0 )
					;//_table.addToPot(bet, player);
			    else {
				    player.setState(PlayerInGame.ALL_IN);
				    // ALL_IN
			    }
				//else  TODO IMPLEMENT PARTIAL Bet - Second Pot
				// TODO - Implement Sitting Out

				// If you didn't try to bet this round,(not BB) then you are out
				if ( currentBet < tableBet && 
					player.getState() != PlayerInGame.ALL_IN) {
				    player.setState(PlayerInGame.OUT_OF_HAND);
				    // FOLD
				    _vfeedback.setPlayerBetState(playerNum, "FOLD");
				    //System.out.println("money: "+player.getMoney()+" highestPriorBet :"+highestPriorBet);
				    //System.out.println("bet: "+bet+" incBet:"+incrementalBet);
				    //System.out.println("currentBet: "+currentBet+" tableBet:"+tableBet);
				    //System.out.println("NO_CARDS i: "+i+" j:"+j);
				    _vfeedback.setPlayerCards(playerNum, NO_CARDS);
				}
			}
		}
		sleep(600);
	}

	public int countPlayersPlaying() {
		PlayerInGame player;
		int tableSize = _table.getSize();
		int count = 0;

		// Count who is still in playing mode 
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			if (!player.isPlaying()) continue;

			count++;
		}
		return(count);
	}

	public int countPlayersActive() {
		PlayerInGame player;
		int tableSize = _table.getSize();
		int count = 0;

		// Count who is still involved in hand in some way
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			if (!player.isActive()) continue;

			count++;
		}
		return(count);
	}

	public void sitPlayersOutIfNoMoney() {
		PlayerInGame player;
		int tableSize = _table.getSize();

		// Check for anyone with no money left
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			if (!player.isActive()) continue;
			//if (LOG.isDebugEnabled())
				//LOG.debug("ACTIVE: "+player.getMoney());

			if (player.getMoney() <= 0.0) {
				player.setState(player.SITTING_OUT);
				//if (LOG.isDebugEnabled())
					//LOG.debug("SITTING_OUT: "+player.getMoney());
			} else if (player.getState() == player.ALL_IN) {
				player.setState(player.PLAYING);
				//if (LOG.isDebugEnabled())
					//LOG.debug("PLAYING: "+player.getMoney());
			}
		}
	}

	public void dealCards() {
		if ( _bettingRound == 0 ) {
			dealPlayers();
		}  else {
			dealCommonCards();
		}
	}

	public void dealPlayers() {
		int dealer = _table.getDealerPosition();
		int tableSize = _table.getSize();
		int startPosition;
		startPosition = (dealer + 1) % tableSize;

		PlayerInGame player;
		int 		 position;
		// Deal each player 2 cards - 1 at a time
		for (int j = 0; j < 2; j++) {  
			for (int i = 0; i < tableSize; i++) {
				position = (i+startPosition)%tableSize;
				player   = _table.getPlayer(position);

				if (player == null || !player.isActive()) continue;

				_dealer.dealCard(player);
				if (_vfeedback != null) { 
					_vfeedback.dealPlayerCard(position, player.getHand().copy());
					//_vfeedback.setPlayerCards(position, player.getHand());
				}
			}
		}
		sleep(800);
	}

	public void dealCommonCards() {
		int cardsToDeal;

		if ( _bettingRound == 1 ) {
			cardsToDeal = 3;
		} else {
			cardsToDeal = 1;
		}

		CommonCards common = _table.getCommonCards();
		// Deal common cards
		for (int i = 0; i < cardsToDeal; i++) {
			_dealer.dealCard(common);
			if (_vfeedback != null) 
				_vfeedback.setCommonCards(common.copy());
		}
		sleep(400);
	}

	public void advanceButton() {
		_table.advanceButton();
		if (_vfeedback != null) 
			_vfeedback.setDealer(_table.getDealerPosition());
	}

	public void advanceState() {
		_bettingRound++;
		_betsInRound = 0;
		sync();
	}

	public void displayState(PlayerInGame player, double tableBet) {
		System.out.print("Nick: "+player.getNick());
		System.out.print("\t$$: "+player.getMoney());
		System.out.print("\tHand: "+player.getHand());
		System.out.println("\tYourBet: "+player.getCurrentBet());
		System.out.print("BetToYou: "+(tableBet-player.getCurrentBet()));
		System.out.print("\tTotalBet: "+tableBet);
		System.out.print("\tPot: "+_table.getTotalPotSize());
		System.out.println("\tCommon: "+_table.getCommonCards());
	}

	public double askBet(PlayerInGame player, double tableBet, double[] tableBetStructure) {
		//if (LOG.isDebugEnabled())
			//displayState(player, tableBet);
		return getUserRaise(player, tableBet, tableBetStructure);
	}


	public double getUserRaise(PlayerInGame player, double tableBet, double[] tableBetStructure) {
		double playerBet = player.getCurrentBet();
		double toCall = tableBet - playerBet;
		//if (LOG.isDebugEnabled())
			//LOG.debug("TOCALL:"+toCall+" PLAYERBET:"+playerBet+" TABLEBET:"+tableBet);
		double callable = Math.min(toCall, player.getMoney());
		//System.out.println("TOCALL:"+toCall+" PLAYERBET:"+playerBet+" TABLEBET:"+tableBet+ " callable:"+callable);

		double maxRaiseIfNoLimit = Math.max(player.getMoney() - toCall, 0.0d);
		double roundMultiplier = (_bettingRound > 1)? 2.0d : 1.0d;
		double max4BetInRound = 4*_bigBlind*roundMultiplier - tableBet;
		double raiseIfLimit = Math.min(maxRaiseIfNoLimit, _bigBlind*roundMultiplier);
		if (_bettingMode == LIMIT)
			raiseIfLimit = Math.min(raiseIfLimit, max4BetInRound);
		double maxRaiseIfPotLimit = Math.min(_table.getTotalPotSize()+toCall,maxRaiseIfNoLimit);
		double minNonLimitRaise = findLastFullBet(tableBetStructure);
		
		// Handle auto betting simulation
		if ( _automatedBetting ) { 
			sleep(200);
			return callable + playerBet;  // TODO: was this change correct?
		}

		if (_useGUI) {
			ActionCallback ac = new ActionCallbackImpl(_vfeedback);
			_vfeedback.setPlayerShowCards(player.getSeatNum(), true);
			_vfeedback.takeAction(_bettingMode, player.getNick(), playerBet, toCall, callable, _bigBlind, maxRaiseIfNoLimit, raiseIfLimit, 
				maxRaiseIfPotLimit, minNonLimitRaise, ac);
			//TODO put this back in
			sync();
			//System.out.println("Waiting");
			ac.waitForResponse(20000l);
			_vfeedback.setPlayerShowCards(player.getSeatNum(), false);
			return ac.getBettingResponse();
		} else {
			System.out.print("  -- What is your bet> ");
			/*BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
			try {
				String command=in.readLine();	
				int bet=Integer.parseInt(command);
				return((double)bet);
			} catch (IOException e) {
				System.exit(1);
			}*/
		}
		return((double)0);
	}
	
	private double findLastFullBet(double[] tableBetStructure) {
		double lastBet = _bigBlind;
		
		for( int i = tableBetStructure.length - 1; i >= 0; i-- ) {
			if (tableBetStructure[i] > lastBet)
				lastBet = tableBetStructure[i];
		}
		return lastBet;
	}

	//public double askBet(PlayerInGame player, double tableBet) {
	//double  _bigBlind;
	//int     _bettingRound;
	//}

	public void decideWinner() {
		ArrayList pots = _table.getPots();
		Pot       pot;
		HashSet<PlayerInGame> winningSet = new HashSet<PlayerInGame>();
		for (int i = 0; i < pots.size(); i++) {
			pot = (Pot)pots.get(i);
			if ( pot.getPotSize() > 0.0 )
				decideWinnerInPot(pot, i, winningSet);
		}
		//showCardsForWinner(winningSet);
		sleep(2600);
	}
	
	private void showCardsForWinner(HashSet<PlayerInGame> winningSet) {
		//if ( _vfeedback != null )
			//_vfeedback.setAllShowCards(true);
		//commonCardsVisible = true;
		for( PlayerInGame player : winningSet) {
			_vfeedback.setPlayerShowCards(player.getSeatNum(), true);
		}
		
		//sleep(500);
	}

	/*
	public void decideWinner() {
		System.out.println("--- Who Won? ---");

		PlayerInGame player;
		int tableSize = _table.getSize();
		Hand hand;
		CommonCards cards = _table.getCommonCards();
		BestHand    bestHand;

		// List of best hands for comparison
		ArrayList    allHands     = new ArrayList();

		// Check if only one player remains active
		int          numActive    = 0;
		PlayerInGame activePlayer = null;
		for (int i = 0; i < tableSize; i++) {
			player = _table.getPlayer(i);

			if (player == null) continue;

			if (!player.isActive()) continue;

			numActive++;
			activePlayer = player;
		}

		
		if ( numActive == 1 ) {
			// Everyone else folded
			System.out.println("** Winner: "+
			  activePlayer.getNick()+
			  " \t cards: "+activePlayer.getHand() +
			  " common: "+cards );
		} else {

			// Get best hands for remaining Players
			for (int i = 0; i < tableSize; i++) {
				player = _table.getPlayer(i);

				if (player == null) continue;

				if (!player.isActive()) continue;

				hand = player.getHand();
				if ( hand != null && cards != null ) {
					bestHand = BestHand.makeBestHand(hand, cards);
					bestHand.userHook = player;
					allHands.add(bestHand);
					System.out.println("   Player: "+
					  player.getNick()+
					  " \tbestHand: "+bestHand);
				}
			}
	
			System.out.println("**");

			ArrayList winner = BestHand.findBestHand(allHands);
			for (int i = 0; i < winner.size(); i++) {
				bestHand = (BestHand) winner.get(i);
				player   = (PlayerInGame) bestHand.userHook;
				System.out.println("** Winner: "+
				  player.getNick()+
				  " \tbestHand: "+bestHand);
			}
		}
	}
	*/

	public void decideWinnerInPot(Pot pot, int potNum, HashSet<PlayerInGame> winningSet) {
		if ( potNum == 0 ) {
			//if (LOG.isDebugEnabled())
				//LOG.debug("-- Main Pot: "+pot+" -- Who Won? --");
		} else {
			//if (LOG.isDebugEnabled())
				//LOG.debug("-- Pot "+(potNum+1)+": "+pot+" -- Who Won? --");
		}
 
		PlayerInGame player;
		int numPlayers = pot.getNumPlayers();
		Hand hand;
		CommonCards cards = _table.getCommonCards();
		BestHand    bestHand;

		// List of best hands for comparison
		ArrayList    allHands     = new ArrayList();

		// Check if only one player remains active
		int          numActive    = 0;
		PlayerInGame activePlayer = null;
		for (int i = 0; i < numPlayers; i++) {
			player = pot.getPlayer(i);

			if (player == null) continue;

			if (!player.isActive()) continue;

			numActive++;
			activePlayer = player;
		}

		
		double winnings = pot.getPotSize();
		if ( numActive == 1 ) {
			winningSet.add(activePlayer);
			// Everyone else folded
			PotWinner potWinners[] = new PotWinner[1];
			potWinners[0] = new PotWinner(activePlayer, winnings);
			winningsFeedback(potNum, potWinners);  // Animate the pot delivery
			activePlayer.giveWinnings(winnings);
			setPlayerAmount(_vfeedback, activePlayer.getSeatNum(), activePlayer, 0);// Set visual player amount

			//givePlayerWinnings(activePlayer, pot, winnings);
			//if (LOG.isDebugEnabled())
				//LOG.debug("** Winner: "+
			 // activePlayer.getNick()+
			 // " \t cards: "+activePlayer.getHand() +
			 // " common: "+cards +
			 // " $$: "+activePlayer.getMoney());
			sleep(400);
		} else {
			//if (potNum == 0)
				//showCardsForWinner();

			// Get best hands for remaining Players
			for (int i = 0; i < numPlayers; i++) {
				player = pot.getPlayer(i);

				if (player == null) continue;

				if (!player.isActive()) continue;

				hand = player.getHand();
				if ( hand != null && cards != null ) {
					bestHand = BestHand.makeBestHand(hand, cards);
					bestHand.setUserHook(player);
					allHands.add(bestHand);
					//if (LOG.isDebugEnabled())
						//LOG.debug("   Player: "+
					  //player.getNick()+
					  //" \tbestHand: "+bestHand);
				}
			}
	
			//if (LOG.isDebugEnabled())
				//LOG.debug("**");

			ArrayList winner = BestHand.findBestHand(allHands);
			winnings = winnings / (double) winner.size();
			double strictWinnings = Math.floor(winnings / _smallestChip ) * _smallestChip;
			int    extra = (int)((winnings - strictWinnings) * winner.size() + 0.5 );
			//if (LOG.isDebugEnabled())
				//LOG.debug("WINNINGS: "+winnings+ " strict: "+strictWinnings + " extra: "+extra);

			// Prepare to deliver pot to winners
			PotWinner potWinners[] = new PotWinner[winner.size()];
			for (int i = 0; i < winner.size(); i++) {
				bestHand = (BestHand) winner.get(i);
				player   = (PlayerInGame) bestHand.getUserHook();
				if ( extra > 0.0 && extra >= _smallestChip ) {
					winnings = strictWinnings + _smallestChip;
					extra -= _smallestChip;
				} else if ( extra > 0.0 ) {
					winnings = strictWinnings + extra;
					extra = 0;
				} else {
					winnings = strictWinnings;
				}
				potWinners[i] = new PotWinner(player, winnings);
				winningSet.add(player);
			}

			// Deliver pot to all winners
			showCardsForWinner(winningSet);
			winningSet.clear();
			winningsFeedback(potNum, potWinners);  // Animate the pot delivery
			for (int i = 0; i < winner.size(); i++) {
				player   = potWinners[i].getPlayer();
				bestHand = (BestHand) winner.get(i);
				player.giveWinnings(potWinners[i].getWinnings());
				String swin =  PotTotal.getChipAmount(potWinners[i].getWinnings());
				_vfeedback.chatMsg("", player.getNick() + " wins pot ("+ /*((potNum == 0) ? "main":"side") +" pot of " +*/ swin+ 
						") with "+bestHand);
				setPlayerAmount(_vfeedback, player.getSeatNum(), player, 0);  // Set visual player amount
				//givePlayerWinnings(player, pot, winnings);
				//if (LOG.isDebugEnabled())
					//LOG.debug("** Winner: "+
				  //player.getNick()+
				  //" $$: "+player.getMoney());
			}
			sleep(400);
		}
	}


	public void bettingFeedback(VisualFeedback vf) {
		if (vf!= null) {
			//if (LOG.isDebugEnabled())
				//LOG.debug("BETTING FEEDBACK");
			// Get Players Bets
			int tableSize = _table.getSize();
			PlayerInGame player = null;
			for (int i = 0; i < tableSize; i++) {
			    player = _table.getPlayer(i);

				if (player == null) continue;

				double bb[] = player.getAllBets();
				double bb2[] = new double[bb.length];
				for (int j = 0; j < bb2.length; j++)
					bb2[j] = bb[j];
				
				vf.setPlayerBets(i, bb2, 0);
				setPlayerAmount(vf, i, player, 0);
			}
		}
	}

	private void setPlayerAmount(VisualFeedback vf, int seat, PlayerInGame player, int delay) {
		if ( !player.isSittingOut() ) {
			String money = String.valueOf(player.getMoney());
			if (money.endsWith(".0"))
				money = money.substring(0, money.length()-2);
			vf.setPlayerAmount(seat, "$"+money, delay);
		} else {
			vf.setPlayerAmount(seat, "Sitting Out", delay); 
		}
	}
	
	public void potFeedback(VisualFeedback vf) {
		potFeedbackNoDelay(vf, true);
		sleep(400);
	}

	public void potFeedbackNoDelay(VisualFeedback vf, boolean doAnimation) {
		if ( vf == null )
			return;
		ArrayList pots = _table.getPots();
		Pot       pot;
		double    vpots[] = new double[pots.size()];

		for (int i = 0; i < pots.size(); i++) {
			pot = (Pot)pots.get(i);
			//if ( pot.getPotSize() > 0.0 ) TODO: Worry about this
			vpots[i] = pot.getPotSize();
			//if (LOG.isDebugEnabled())
				//LOG.debug("POT: "+i+" AMT:"+pot.getPotSize());
		}
		vf.collectBets(0, doAnimation);   // TODO:  First pot or multiple visually?
		vf.setPots(vpots);
		bettingFeedback(vf);
	}

	public void winningsFeedback(int potNum, PotWinner potWinners[]) {
		if (_vfeedback != null) {
			_vfeedback.deliverPot(potNum, potWinners);
			/*
			// Find player
			int tableSize = _table.getSize();
			PlayerInGame curPlayer = null;
			int position = -1;
			for (int i = 0; i < tableSize; i++) {
			    curPlayer = _table.getPlayer(i);

				if (player == null) continue;

				if (player == curPlayer) {
					position = i;
					break;
				}
			}
			setPlayerAmount(position, player, 10);
			*/
		}
	}
	
	public int getNumActivePlayers() {
		int count = 0;
		for (int i = 0; i < _table.getSize(); i++) {
			PlayerInGame player;
			player = _table.getPlayer(i);
			
			if (player == null) continue;
			count++;
		}
		return count;
	}
	
	public void initNewTable(VisualFeedback vf) {
		System.out.println("initNewTable: "+_table.getSize());
		vf.initTable(_table.getSize());
		vf.initRound();
		for (int i = 0; i < _table.getSize(); i++) {
			PlayerInGame player;
			player = _table.getPlayer(i);
						
		    vf.setPlayer(i, player.getNick());
		    //vf.setPlayerCards(i, player.getHand());
		    //vf.setPlayerBets(i, (double [])player.getAllBets().clone(), 0);  // new
			//setPlayerAmount(vf, i, player, 0); // new
		}
		 vf.setDealer(_table.getDealerPosition());
		 //vf.setCommonCards(_table.getCommonCards());
		 //if (commonCardsVisible)
			//vf.setAllShowCards(true);
		 //potFeedback(vf);
		 // other state
	}
	
	public String getNick(int seatNum) {
		if ( seatNum <= _table.getSize() ) {
			PlayerInGame player;
			player = _table.getPlayer(seatNum);
			
			if (player == null)
				return null;

		    return player.getNick();
		}
		return null;
	}
		
	public void finishInitNewTable(VisualFeedback vf) {
		System.out.println("finishInitNewTable: "+_table.getSize());
		vf.initTable(_table.getSize());
		vf.initRound();
		for (int i = 0; i < _table.getSize(); i++) {
			PlayerInGame player;
			player = _table.getPlayer(i);
			
			if (player == null) continue;

		    vf.setPlayer(i, player.getNick());
		    vf.setPlayerCards(i, player.getHand());
		    
			double bb[] = player.getAllBets();
			double bb2[] = new double[bb.length];
			for (int j = 0; j < bb2.length; j++)
				bb2[j] = bb[j];
		    vf.setPlayerBets(i, bb2, 0);  // new
		    //vf.setPlayerBets(i, (double [])player.getAllBets().clone(), 0);  // new
			setPlayerAmount(vf, i, player, 0); // new
		}
		 vf.setDealer(_table.getDealerPosition());
		 vf.setCommonCards(_table.getCommonCards());
		 if (commonCardsVisible)
			vf.setAllShowCards(true);
		 potFeedbackNoDelay(vf, false);
		 // other state
	}
	
	public void sync() {
		_vfeedback.sync();
	}

	public void playRound() {
		commonCardsVisible = false;
		sitPlayersOutIfNoMoney();
		
		initialize();
		if (_playedRound) {
		    advanceButton();
		}
		_playedRound = true;

		sync();
		
		takeAnte();
		addToPot();
		takeBlinds();
		//addToPot();
		
		sync();
		sleep(1200);

		// PreFlop
	  	//if (LOG.isDebugEnabled())
			//LOG.debug("*** PreFlop ***");
		dealCards();
		sleep(2400);

		if ( countPlayersPlaying() > 1 ) {
		    takeBets();
			sleep(500);
			addToPot();
			sleep(500);
		}
	    advanceState();
		if ( countPlayersActive() <= 1 ){
			decideWinner();
			return;
		}
		sleep(2000);

		// Flop
		//if (LOG.isDebugEnabled())
			//LOG.debug("*** Flop ***");
		dealCards();
		sleep(500);
		if ( countPlayersPlaying() > 1 ) {
		    takeBets();
			sleep(500);
			addToPot();
			sleep(500);
		}
	    advanceState();
		if ( countPlayersActive() <= 1 ){
			decideWinner();
			return;
		}
		sleep(1000);

		// Fourth Street
		//if (LOG.isDebugEnabled())
			//LOG.debug("*** Fourth Street ***");
		dealCards();
		sleep(500);
		if ( countPlayersPlaying() > 1 ) {
		    takeBets();
			sleep(500);
			addToPot();
			sleep(500);
		}
	    advanceState();
		if ( countPlayersActive() <= 1 ){
			decideWinner();
			return;
		}
		sleep(800);

		// Fifth Street
		//if (LOG.isDebugEnabled())
			//LOG.debug("*** Fifth Street ***");
		dealCards();
		sleep(500);
		if ( countPlayersPlaying() > 1 ) {
		    takeBets();
			sleep(500);
			addToPot();
			sleep(500);
		}
	    advanceState();
	    sleep(1500);

		decideWinner();
	}
	
	private int stepCount = 0;
	public void step() {
		if (stepCount == 0) {
			commonCardsVisible = false;
			sitPlayersOutIfNoMoney();
		} else if (stepCount == 1) {
			initialize();
		} else if (stepCount == 2) {
			if (_playedRound) {
				advanceButton();
			}
			_playedRound = true;
		} else if (stepCount == 3) {
			sync();
		
			takeAnte();
		} else if (stepCount == 4) {
			addToPot();
		} else if (stepCount == 5) {
			takeBlinds();
		} else if (stepCount == 6) {
			sync();
			//try { Thread.sleep(1500); } catch (InterruptedException ie) {}

			// PreFlop
			//if (LOG.isDebugEnabled())
				//LOG.debug("*** PreFlop ***");
			dealCards();
			try { Thread.sleep(1200); } catch (InterruptedException ie) {}
		} else if (stepCount == 7) {
			if ( countPlayersPlaying() > 1 ) {
				takeBets();
				System.out.println("step 7");
			}		
		} else if (stepCount == 8) {
			System.out.println("step 8a");
			if ( countPlayersPlaying() > 1 ) {
				System.out.println("step 8");
				addToPot();
			}
		} else if (stepCount == 9) {
			advanceState();
		} else if (stepCount == 10) {
			if ( countPlayersActive() <= 1 ){
				decideWinner();
				stepCount = 0;
				return;
			}
		} else if (stepCount == 11) {

			//try { Thread.sleep(4000); } catch (InterruptedException ie) {}

			// Flop
			//if (LOG.isDebugEnabled())
				//LOG.debug("*** Flop ***");
			dealCards();
		} else if (stepCount == 12) {
			if ( countPlayersPlaying() > 1 ) {
				takeBets();
			}
		} else if (stepCount == 13) {
			if ( countPlayersPlaying() > 1 ) {
				addToPot();
			}
		} else if (stepCount == 14) {
			advanceState();
		} else if (stepCount == 15) {

			if ( countPlayersActive() <= 1 ){
				decideWinner();
				stepCount = 0;
				return;
			}
		} else if (stepCount == 16) {

			//try { Thread.sleep(2000); } catch (InterruptedException ie) {}

			// Fourth Street
			//if (LOG.isDebugEnabled())
				//LOG.debug("*** Fourth Street ***");
			dealCards();
		} else if (stepCount == 17) {
			if ( countPlayersPlaying() > 1 ) {
				takeBets();
			}
		} else if (stepCount == 18) {
			if ( countPlayersPlaying() > 1 ) {
				addToPot();
			}
		} else if (stepCount == 19) {
			advanceState();
		} else if (stepCount == 20) {

			if ( countPlayersActive() <= 1 ){
				decideWinner();
				stepCount = 0;
				return;
			}
		} else if (stepCount == 21) {

			//try { Thread.sleep(1500); } catch (InterruptedException ie) {}

			// Fifth Street
			//if (LOG.isDebugEnabled())
				//LOG.debug("*** Fifth Street ***");
			dealCards();
		} else if (stepCount == 22) {
			if ( countPlayersPlaying() > 1 ) {
				takeBets();
			}
		} else if (stepCount == 23) {
			if ( countPlayersPlaying() > 1 ) {
				addToPot();
			}
		} else if (stepCount == 24) {
			advanceState();
		} else if (stepCount == 25) {
	    //try { Thread.sleep(1500); } catch (InterruptedException ie) {}
			decideWinner();
			stepCount = 0;
		}
		stepCount++;
		System.out.println("stepCount: "+stepCount);
	}
	
	private void sleep(long time) {
		//System.out.println("sleep: "+time);
		try { Thread.sleep(time); } catch (InterruptedException ie) {}
	}

}
