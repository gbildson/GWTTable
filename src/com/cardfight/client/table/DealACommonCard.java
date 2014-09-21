package com.cardfight.client.table;


import com.cardfight.client.TopControl;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.server.poker.*;

class DealACommonCard implements TableAnimation {
	private HoldemTable    vtable;
	private VisualFeedback vf;
	private int            cardNum;
	//private Card 		   commonCard;
	private long           startTime;
	private SmallCard      card;

	public DealACommonCard(HoldemTable vtable, VisualFeedback vf, int cardNum/*, Card commonCard*/, TopControl control) {
		this.vtable     = vtable;
		this.vf         = vf;
		this.cardNum    = cardNum;
		//this.commonCard = commonCard;
		startTime       = System.currentTimeMillis();
		card            = new SmallCard(cardNum, Geometry.CARD_SPEED, control);
		vtable.setCardInMotion(card); // Reusing this animation for common cards
		vtable.setCardAnimation(this);
		vtable.startAnim();
		vtable.addAnimation(this);
		//System.out.println("DealACommonCard");
	}

	public void step() {
		if (card == null) return;
		int timeDelta = (int)(System.currentTimeMillis() - startTime);
		//System.out.println("** tD="+timeDelta);

		//vtable.moveCardInMotion(timeDelta); //cardInMotion.move(timeDelta);
			card.move(timeDelta); //cardInMotion.move(timeDelta);

		if (timeDelta > (card.getTimeToDeliver()+10)) {
			done();
		}
		vtable.repaint();
	}


	public void done() {
		vtable.clearCardInMotion(); // cardInMotion  = null;
		//cardAnimation = null;
		vtable.setCardAnimation(null);
		vtable.stopAnim();
		vtable.setNumActiveCommonCards(cardNum);
		vtable.removeAnimation(this);
		card.delete();
		card = null;
	}

	public int maxDelay() {
		return ((card.getTimeToDeliver()+100));
	}
}
