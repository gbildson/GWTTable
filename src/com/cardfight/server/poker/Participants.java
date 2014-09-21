
package com.cardfight.server.poker;

import java.util.*;

import com.cardfight.client.poker.PlayerInGame;

public class Participants {
	protected ArrayList participants;

	public Participants() {
		clear();
	}

	public void clear() {
		participants = new ArrayList();
	}

	public ArrayList getParticipants() {
		return participants;
	}

	public void add( PlayerInGame player ) {
		participants.add(player);
	}

	public void addIfNotThere( PlayerInGame player ) {
		if ( !participants.contains(player) )
			participants.add(player);
	}

	public int getSize( ) {
		return participants.size();
	}

	public PlayerInGame get( int playerNum ) {
		return (PlayerInGame) participants.get(playerNum);
	}

	public String toString() {
		PlayerInGame player;
		String str  = "";
	
		for(int i=0; i < participants.size(); i++) {
			player = (PlayerInGame) participants.get(i);
			str += player.getNick() + ": " + player.getMoney();
			if ( i != participants.size()-1 )
			    str += "\n";
		}

		return str;
	}
}
