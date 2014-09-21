package com.cardfight.client.table;

import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;


public class CommonCards extends TableObject {
	
	public static final int COMMON_CARD_OFFSET = 55;
	public static final int BOTTOM_OFFSET      = 50;

	
	private int cards[];
	private int numActive;
	private HorizontalPanel ccPanel;
	private Image cardImages[];
	private TopControl control;
	
	private void wrapImage() {
		//Point p = Geometry.instance().getPlayerLoc(seatNum);
		//System.out.println("PlayersCards seat: "+seatNum +" cards :"+numCards+" cards[]: "+cards);
		if ( cards == null || cards.length == 0 )
			return;
		
		cardImages = new Image[cards.length];
		ccPanel = new HorizontalPanel();
		ccPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		ccPanel.addStyleName("commoncards");
		//ccPanel.add(box);
		control.addWidget(ccPanel, x, y);
		
		//image = new Image("/img/W"+TableResources.cardNames[cards[0]]+TableResources.cardNames[cards[1]]+".png");
		//GWTTable.addWidget(image, x, y);
		for ( int i = 0; i < cardImages.length; i++ ) {
			cardImages[i] = new Image("/img/"+TableResources.cardNames[cards[i]]+"50.gif");
			if ( numActive > i )
				ccPanel.add(cardImages[i]);
		}
		
		//canvas   = null

	}
	
	public CommonCards(int cards[], int numActive, TopControl control) {
		this.cards      = cards;   //WARNING
		this.numActive  = numActive;
		this.control    = control;
		Point p = Geometry.instance().getCommonCardLoc();
		setLoc(p.x, p.y);
		wrapImage();
	}

	public void display() {
		//for (int i = 0; i < this.cards.length && i < numActive; i++) {
			//canvas.drawImage(TableResources.cards[this.cards[i]], x+COMMON_CARD_OFFSET*i, y);
		//}
	}
	
	public void setNumActive(int num) {
		if ( cardImages != null ) {
			for ( int i = 0; i < cardImages.length; i++ ) {
				if ( numActive <= i && num > i )
					ccPanel.add(cardImages[i]);
			}
		}
		numActive = num;
	}
	
	public String toString() {
		String cardStr = " cards:";
		for (int i = 0; i < cards.length; i++) 
			cardStr += " "+cards[i];
		return cardStr;
	}
	
	public void delete() {
		control.removeWidget(ccPanel);
	}
}

