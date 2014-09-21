package com.cardfight.client.table;

import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class PlayersCards extends TableObject {
	private int seatNum;
	private int numCards;
	private int cards[];
	private int numPlayers;
	//private Image     image = null;
	private Image     cardImg1 = null;
	private Image     cardImg2 = null;

	private TopControl control;
	
	private void wrapImage() {
		//Point p = Geometry.instance().getPlayerLoc(seatNum);
		//System.out.println("PlayersCards seat: "+seatNum +" cards :"+numCards+" cards[]: "+cards);
		if ( cards == null || cards.length < 2 )
			return;
		//image = new Image("/img/W"+TableResources.cardNames[cards[0]]+TableResources.cardNames[cards[1]]+".png");
		cardImg1 = new Image("/img/"+TableResources.cardNames[cards[0]]+"50.gif");
		cardImg2 = new Image("/img/"+TableResources.cardNames[cards[1]]+"50.gif");
		//control.addWidget(image, x, y);
		control.addWidget(cardImg1, x, y);
		control.addWidget(cardImg2, x+16, y+4);
		//canvas   = null
	}
	
	public PlayersCards(int seatNum, int cards[], int numPlayers, TopControl control) {
		this.seatNum = seatNum;
		this.numCards   = cards.length;
		this.cards      = cards;  //WARNING
		this.numPlayers = numPlayers;
		this.control    = control;
		Point p = Geometry.instance().getPlayerLoc(seatNum, numPlayers);
		setLoc(p.x + 7, p.y + 3);
		wrapImage();
	}

	public void display() {
		//if (numCards > 0)
			//g2.drawImage(TableResources.cards[cards[0]], x, y);
		//if (numCards > 1)
			//g2.drawImage(TableResources.cards[cards[1]], x+16, y+4);
	}
	
	public String toString() {
		String cardStr = "";
		for( int i = 0; i < cards.length; i++)
			cardStr += " " +cards[i];
		return "Seat "+seatNum+" - "+numCards+ " cards"+ " card ints:"+ cardStr;
	}
	
	public void delete() {
		//if ( image != null )
			//control.removeWidget(image);
		if ( cardImg1 != null )
			control.removeWidget(cardImg1);
		if ( cardImg2 != null )
			control.removeWidget(cardImg2);
		cardImg1 = null;
		cardImg2 = null;
		//System.out.println("PlayersCards delete");
	}
}
