package com.cardfight.client.table;


import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;

class DealACard implements TableAnimation {
	private HoldemTable    vtable;
	private VisualFeedback vf;
	private int            playerNum;
	private CardCollection cards;
	private long           startTime;
	private SmallCard      card;
	private TopControl     control;

	public DealACard(HoldemTable vtable, VisualFeedback vf, int playerNum, CardCollection cards, int numPlayers, TopControl control) {
		this.vtable    = vtable;
		this.vf        = vf;
		this.playerNum = playerNum;
		this.cards     = cards;
		this.control   = control;
		startTime      = System.currentTimeMillis();
		card           = new SmallCard(playerNum, Geometry.CARD_SPEED, cards.size()-1, numPlayers, control);
		//vtable.setCardInMotion(card); //cardInMotion   = card;
		//vtable.setCardAnimation(this);
		//vtable.startAnim();
		vtable.addAnimation(this);
		//System.out.println("DealACard");
		//int calc = 0;
		//for (int i = 0; i < 100000; i++)
			//calc += 2;
	}

	public void step() {
		if ( card == null ) return;
		int timeDelta = (int)(System.currentTimeMillis() - startTime);
		//System.out.println("tD="+timeDelta);

		//vtable.moveCardInMotion(timeDelta); //cardInMotion.move(timeDelta);
		card.move(timeDelta);
		if (timeDelta > (card.getTimeToDeliver()+10)) {
			done();
		}
		vtable.repaint();
	}

	public void done() {
		//System.out.println("done DealACard : "+playerNum + " cards: "+cards);
		//vtable.clearCardInMotion(); // cardInMotion  = null;
		//cardAnimation = null;
		//vtable.setCardAnimation(null);
		//vtable.stopAnim();
		vf.setPlayerCards(playerNum, cards);
		vtable.removeAnimation(this);
		//System.out.println("out DealACard");
		card.delete();
		//control.logLocally("DealACard done : "+playerNum+" cards: "+cards + " start: "+startTime+ " max: "+maxDelay());
		card = null;
	}

	public int maxDelay() {
		return ((card.getTimeToDeliver()+10));
	}
}
