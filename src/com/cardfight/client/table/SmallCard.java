package com.cardfight.client.table;

//import java.awt.Graphics2D;
//import java.awt.Point;
//import java.awt.image.ImageObserver;
import com.cardfight.client.TopControl;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class SmallCard extends TableObject {
	private Point  source;
	private Point  dest;
	private AnimationTracker tracker;
	private Image     image = null;
	private TopControl control;
	private void wrapImage() {
		image = new Image("/img/smallcard.gif");
		control.addWidget(image, x, y);
	}
	
	/*
	 * Create a card for a player.
	 */
	public SmallCard(int seatNum, double speed, int cardNum, int numPlayers, TopControl control) {
		this.control = control;
		source = Geometry.instance().getDealLoc();
		Point p2 = Geometry.instance().getSmallCardLoc(seatNum, numPlayers);
		source = Geometry.instance().getDealLoc();
		dest = new Point(p2.x + Geometry.instance().getSCXOffset(cardNum), 
				p2.y + Geometry.instance().getSCYOffset(cardNum));
		tracker = new AnimationTracker(source, dest, speed);
		setLoc(source.x, source.y);
		wrapImage();
	}
	
	/*
	 * Create a common card.
	 */
	public SmallCard(int cardNum, double speed, TopControl control) {
		this.control = control;
		source = Geometry.instance().getDealLoc();
		
		Point p = Geometry.instance().getCommonCardLoc();
		dest = new Point(p.x+CommonCards.COMMON_CARD_OFFSET*cardNum, p.y+CommonCards.BOTTOM_OFFSET);
		
		tracker = new AnimationTracker(source, dest, speed);
		setLoc(source.x, source.y);
		wrapImage();
	}

	public void move(int timeDelta) {
		Point newLoc = tracker.move(timeDelta);
		setLoc(newLoc.x, newLoc.y);
		delete();
		wrapImage();
	}

	public int getTimeToDeliver() {
		return tracker.getTimeToDeliver() / 2;
	}

	public void display() {
		//System.out.println("sn: "+sn+" x: "+x+" y: "+y);
		//canvas.drawImage(TableResources.smallCardImg, x, y);
	}
	
	public void delete() {
		control.removeWidget(image);
	}
}
