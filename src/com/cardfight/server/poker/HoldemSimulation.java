
package com.cardfight.server.poker;

import com.cardfight.client.poker.Player;
import com.cardfight.client.poker.PlayerInGame;
import com.cardfight.client.poker.VisualFeedback;

public class HoldemSimulation {
	private TexasHoldem game;
	private int numPlayers = 0;
	private VisualFeedback vf;
	private boolean realPlayers = false;
	private static final int DEFAULT_NUM_PLAYERS = 9;
	
	public static void main(String args[]) {
		boolean automated = (args.length > 0 && args[0].equals("-a"));
		HoldemSimulation hs = new HoldemSimulation();
		hs.initGame(automated, null);
	}

	public void initGame(boolean automated, VisualFeedback vf) {
		this.vf = vf;
		numPlayers = DEFAULT_NUM_PLAYERS;
		realPlayers = false;
	    //Player p1 = new Player(15.0, 0.0, "player1");
	    Player p1 = new Player(3000.0, 0.0, "player1");
	    Player p2 = new Player(3000.0, 0.0, "player2");
	    //Player p2 = new Player(25.0, 0.0, "player2");
	    //Player p3 = new Player(16.0, 0.0, "player3");
	    Player p3 = new Player(4000.0, 0.0, "player3");
	    Player p4 = new Player(4000.0, 0.0, "player4");
	    Player p5 = new Player(4000.0, 0.0, "player5");
	    Player p6 = new Player(2000.0, 0.0, "player6");
	    Player p7 = new Player(3000.0, 0.0, "player7");
	    Player p8 = new Player(3000.0, 0.0, "player8");
	    Player p9 = new Player(3000.0, 0.0, "player9");
	    Player p10 = new Player(3000.0, 0.0, "player10");
		PlayerInGame player1 = new PlayerInGame(p1, p1.getPlayMoney());
		PlayerInGame player2 = new PlayerInGame(p2, p2.getPlayMoney());
		PlayerInGame player3 = new PlayerInGame(p3, p3.getPlayMoney());
		PlayerInGame player4 = new PlayerInGame(p4, p4.getPlayMoney());
		PlayerInGame player5 = new PlayerInGame(p5, p5.getPlayMoney());
		PlayerInGame player6 = new PlayerInGame(p6, p6.getPlayMoney());
		PlayerInGame player7 = new PlayerInGame(p7, p7.getPlayMoney());
		PlayerInGame player8 = new PlayerInGame(p8, p8.getPlayMoney());
		PlayerInGame player9 = new PlayerInGame(p9, p9.getPlayMoney());
		PlayerInGame player10 = new PlayerInGame(p10, p10.getPlayMoney());
		
		game = new TexasHoldem(numPlayers, 10.0, 20.0, 0.0, 1.0, TexasHoldem.NO_LIMIT); 
		//game = new TexasHoldem(10, 0.01d, 0.02d, 0.0d, 0.01d, TexasHoldem.NO_LIMIT);
		
		game.setAutomatedBetting(automated);
		game.setVisualFeedback(vf);
		if (vf instanceof VisualFeedbackMultiplexor) {  // TODO: Clean this up.
			((VisualFeedbackMultiplexor) vf).setSyncCallback(game);
		}
		if ( vf != null ) {
			System.out.println("initGame: "+numPlayers);
			vf.initTable(numPlayers);
			vf.initRound();
		}
		
		game.takeSeat(0, player1);
		game.takeSeat(1, player2);
		if ( numPlayers > 2 )
			game.takeSeat(2, player3);
		if ( numPlayers > 3 )
			game.takeSeat(3, player4);
		if ( numPlayers > 4 )
			game.takeSeat(4, player5);
		if ( numPlayers > 5 )
			game.takeSeat(5, player6);
		if ( numPlayers > 6 )
			game.takeSeat(6, player7);
		if ( numPlayers > 7 )
			game.takeSeat(7, player8);
		if ( numPlayers > 8 )
			game.takeSeat(8, player9);
		if ( numPlayers > 9 )
			game.takeSeat(9, player10);
		if ( vf != null ) {
			vf.update();
		}
		playGame();
	}
	
	private void playGame() {
		while (true) {
			//System.out.println("round");
			if ( game.getNumActivePlayers() < 2 ) 
				return;
			game.playRound();
			//game.playRound();

			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {}
		}
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public void step() {
		game.step();
	}
	
	public void initNewTable(VisualFeedback vf) {
		game.initNewTable(vf);
	}
	
	//
	// Handle interactive mode 
	//
	
	public void initEmptyGame(boolean automated, VisualFeedback vf) {
		this.vf = vf;
		numPlayers = DEFAULT_NUM_PLAYERS;
		realPlayers = true;
		
		game = new TexasHoldem(numPlayers, 10.0, 20.0, 0.0, 1.0, TexasHoldem.NO_LIMIT); 
		//game = new TexasHoldem(10, 0.01d, 0.02d, 0.0d, 0.01d, TexasHoldem.NO_LIMIT);
		
		game.setAutomatedBetting(automated);
		game.setVisualFeedback(vf);
		if (vf instanceof VisualFeedbackMultiplexor) {  // TODO: Clean this up.
			((VisualFeedbackMultiplexor) vf).setSyncCallback(game);
		}
		if ( vf != null ) {
			System.out.println("initGame: "+numPlayers);
			vf.initTable(numPlayers);
			vf.initRound();
		}
	}
	
	public void awaitPlayers(int numPlayersToStart) {
		boolean gameNotStarted = true;
		//activate Game 1 minute after receiving numPlayersToStart
		while (true) {
			try { Thread.sleep(1000); } catch (InterruptedException ie) {}
			game.sync();
			if ( game.getNumActivePlayers() >= numPlayersToStart ) {
				if (gameNotStarted )
					vf.chatMsg("Dealer", "Game will start in 15 seconds");
				else 
					vf.chatMsg("Dealer", "Game will restart in 15 seconds");
				gameNotStarted = false;
				try { Thread.sleep(15000); } catch (InterruptedException ie) {}
				playGame();
			}
		}
	}
	
	public void addPlayer(String nick, int seat) {
	    Player p = new Player(3000.0, 0.0, nick);
		PlayerInGame pin = new PlayerInGame(p, p.getPlayMoney());
		game.takeSeat(seat, pin);
	    pin.setState(PlayerInGame.OUT_OF_HAND);
	}
	
	public void removePlayer(String nick) {
		game.removePlayer(nick);
	}
	
	public boolean getRealPlayerMode() {
		return realPlayers;
	}
	
	public String getNick(int seatNum) {
		return game.getNick(seatNum); 
	}

}
