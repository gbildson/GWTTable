package com.cardfight.client.table;

/*import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;*/
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
//import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;


public class PotTotal extends TableObject {
	private double amount;
	private Label  label;
	private HorizontalPanel topbox;
	private HorizontalPanel box;
	private TopControl control;

	public PotTotal(double amount, TopControl control) {
		this.amount   = amount;
		this.control  = control;
		Point p = Geometry.instance().getPotTotalLoc();
		setLoc(p.x, p.y);
		String total = "POT $"+getChipAmount(amount);
		label = new Label(total);
		box = new HorizontalPanel();
		box.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		box.addStyleName("pottotal");
		box.add(label);
		topbox = new HorizontalPanel();
		topbox.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topbox.addStyleName("potframe");
		topbox.add(box);
		control.addWidget(topbox, p.x, p.y);
	}
	
	public void setAmount(double amount) {
		String total = "POT $"+getChipAmount(amount);
		label.setText(total);
	}

	public void display() {
		//String total = "POT $"+getChipAmount(amount);
		//label.setText(total);
		// Compute the required width
		//g2.setFont(TableResources.myfont);
		//Font f = (Font) g2.getFont();
		//int sw = g2.getFontMetrics(f).stringWidth(total);

		//int w = 10 + total.length()*14;
		//int h = 19;
		//int gutter  = 4;
		//g2.setColor(TableResources.LIGHT_TAN);
		//g2.fillRect(x,y,w,h);	
		//g2.setColor(Color.black);
		//g2.fillRect(x,y,w,h);	
		//g2.setColor(Color.black);
		//g2.drawString(total, (int) (x+w/2-sw/2),y+h-gutter);
		//g2.setColor(Color.magenta);
	}
	
	public static String getChipAmount(double amt) {
		String amount = "" + amt;
		if (amount.endsWith(".0"))
			amount = amount.substring(0, amount.length()-2);
		return amount;
	}
	
	public String toString() {
		return "amount : "+amount;
	}
	
	public void delete() {
		control.removeWidget(topbox);
	}
}
