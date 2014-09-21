package com.cardfight.client.table;


//import java.text.NumberFormat;

import java.util.ArrayList;

import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class Pot extends ChipObject implements HasToolTip {
	private double amt;
	protected ToolTipHelper tthelper;
	private ArrayList<Image> images;
	private TopControl control;
	//protected Pot() {
	//}

	public Pot(int potNumber, double amt, TopControl control) {
		super(control);
		this.amt = amt;
		this.control = control;
		Point p = Geometry.instance().getPotLoc(potNumber);
		setLoc(p.x, p.y);
		tthelper = new ToolTipHelper(this, p.x, p.y, Geometry.CHIP_HORIZONTAL, 100);  //TODO: replace 100
		images = displayChips(x, y, amt, Geometry.FORWARD);
		//System.out.println("Pot create()");
	}

	protected void movePot() {
		delete();
		images = displayChips(x, y, amt, Geometry.FORWARD);
	}
	
	public void display() {
		//displayChips(g2, x, y, amt, Geometry.FORWARD);
	}

	public boolean inside( int x, int y) {
		if (amt != 0.0d)
			return tthelper.inside(x, y);
		else
			return false;
	}

	public String getText() {
		//return NumberFormat.getNumberInstance().format(amt);
		//long lamt = (long) amt * 100;
		//if ( lamt >= 100 )
		return String.valueOf(amt);
	}

	public void deregister(){
		tthelper.deregister();
	}
	
	public String toString() {
		return "pot: "+getText();
	}
	
	public void delete() {
		if (images == null) return;
		//System.out.println("Pot delete()");
		for (Image img : images) {
			control.removeWidget(img);
			//System.out.println("Pot delete - "+img);
		}
	}
}
