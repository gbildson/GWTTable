package com.cardfight.client.table;

import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.user.client.ui.Image;
import java.util.ArrayList;
//import java.text.NumberFormat;

public class Bet extends ChipObject /*implements HasToolTip*/ {
	private double amts[];
	private int displayType;
	private AnimationTracker tracker;
	//private ToolTipHelper tthelper;
	private ArrayList<Image> images;
	private TopControl control;

	public Bet(int seatNumber, double amts[], int numPlayers, TopControl control) {
		super(control);
		this.amts = amts;
		this.control = control;
		Point p = Geometry.instance().getBetLoc(seatNumber, numPlayers);
		displayType = Geometry.instance().getBetDirection(seatNumber, numPlayers);
		setLoc(p.x, p.y);
		//tthelper = new ToolTipHelper(this, computeTrueX(x,amts,displayType), p.y, computeWidth(amts), 100);  //TODO: replace 100
		images = displayBets(x, y, amts, displayType);
		//System.out.println("Bets create()");

	}

	public void collect(int potNumber, double speed) {
		Point  source = new Point(x,y);
		Point  dest   = Geometry.instance().getPotLoc(potNumber);
		tracker = new AnimationTracker(source, dest, speed);

		int   timeToVicinity = tracker.getTimeToDeliver() * 94 / 100;
		Point vicinity       = tracker.move(timeToVicinity);
		tracker.updateDest(vicinity);
	}

	public void move(int timeDelta) {
		Point newLoc = tracker.move(timeDelta);
		setLoc(newLoc.x, newLoc.y);
		delete();
		images = displayBets(x, y, amts, displayType);
	}

	public int getTimeToDeliver() {
		return tracker.getTimeToDeliver();
	}

	public void display() {
		//displayBets(g2, x, y, amts, displayType);
	}

	// Make room if you are displaying backwards from a point
	private int computeTrueX(int x, double[] amts, int displayType) {
		if ( displayType == Geometry.BACKWARD ) {
			x -= (amts.length - 1) * Geometry.CHIP_HORIZONTAL;
		}
		return x;
	}

	private int computeWidth(double[] amts) {
		return amts.length * Geometry.CHIP_HORIZONTAL;
	}

	private ArrayList<Image> displayBets(int x, int y, double[] amts, 
			int displayType) { 
		ArrayList<Image> images = new ArrayList<Image>();

		x = computeTrueX(x, amts, displayType);
		for ( int i = 0; i < amts.length; i++ ) {
			images.addAll(displayChips(x, y, amts[i], Geometry.INPLACE));
			x += Geometry.CHIP_HORIZONTAL;
		}
		return images;
	}

	//public boolean inside( int x, int y) {
		//if (getAmountTotal() != 0.0d)
			//return tthelper.inside(x, y);
		//else
			//return false;
	//}

	public String getText() {
		double amtTot = getAmountTotal();
		//return NumberFormat.getNumberInstance().format(amtTot);
		return String.valueOf(amtTot);
	}
	
	private double getAmountTotal() {
		double amtTot = 0;
		for (int i = 0; i < amts.length; i++){
			amtTot += amts[i];
		}
		return amtTot;
	}

	//public void deregister() {
		//tthelper.deregister();
	//}
	
	public String toString() {
		return "bet amt: "+getText()+ "anim: "+tracker;
	}
	
	public void delete() {
		//System.out.println("Bets delete()");
		for (Image img : images) 
			control.removeWidget(img);
	}
}
