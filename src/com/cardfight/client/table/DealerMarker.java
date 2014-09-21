package com.cardfight.client.table;

import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class DealerMarker extends TableObject {
	private int playerNum;
	private int numPlayers;
	private Image   image = null;
	private TopControl control;
	
	private void wrapImage() {
		image = new Image("/img/D.gif");
		//Point p = Geometry.instance().getDealerLoc(playerNum);
		control.addWidget(image, x, y);
	}
	public DealerMarker(int playerNum, int numPlayers, TopControl control) {
		Point p = Geometry.instance().getDealerLoc(playerNum, numPlayers);
		setLoc(p.x, p.y);
		this.playerNum  = playerNum;
		this.numPlayers = numPlayers;
		this.control    = control;
		wrapImage();
	}

	public void moveDealer(int playerNum) {
		this.playerNum = playerNum;
		Point p = Geometry.instance().getDealerLoc(playerNum, numPlayers);
		x = p.x;
		y = p.y;
		delete();
		image = new Image("/img/D.gif");
		control.addWidget(image, x, y);
	}

	public void display() {
		//g2.drawImage(TableResources.dealerImg, x, y);
	}
	
	public String toString() {
		return "Dealer: "+playerNum;
	}
	
	public void delete() {
		control.removeWidget(image);
	}
}

