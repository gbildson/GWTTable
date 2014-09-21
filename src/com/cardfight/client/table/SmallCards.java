
package com.cardfight.client.table;

import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;


public class SmallCards extends TableObject {
	private int seatNum;
	private int numPlayers;
	private int numCards;	
	private Image   image = null;
	private TopControl control;

	private void wrapImage() {
		//Point p = Geometry.instance().getSmallCardLoc(seatNum);
		//System.out.println("ci : "+TableResources.circleImg2);
		if (numCards == 1)
			image = new Image("/img/smallcard.gif");
		else if (numCards == 2)
			image = new Image("/img/twosmall.gif");

		control.addWidget(image, x, y);
	}

	public SmallCards(int seatNum, int numCards, int numPlayers, TopControl control) {
		this.seatNum  = seatNum;
		this.numCards = numCards;
		this.control  = control;
		this.numPlayers = numPlayers;
		Point p = Geometry.instance().getSmallCardLoc(seatNum, numPlayers);
		setLoc(p.x, p.y);
		wrapImage();
		//System.out.println("SmallCards sn:"+seatNum+" nc:"+numCards);
	}

	public void display() {
		/*
		if (numCards > 0)
    		g2.drawImage(TableResources.smallCardImg, x, y);
		if (numCards > 1)
    		g2.drawImage(TableResources.smallCardImg, 
			  x+Geometry.instance().getSCXOffset(1), y+Geometry.instance().getSCYOffset(1));
			  */
	}
	
	public String toString() {
		return "Seat "+seatNum+" - "+numCards+ " cards";
	}
	
	public void delete() {
		control.removeWidget(image);
	}
}
