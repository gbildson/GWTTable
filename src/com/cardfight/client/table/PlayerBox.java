package com.cardfight.client.table;

//import java.awt.Color;
//import java.awt.Font;

import com.cardfight.client.TopControl;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;


public class PlayerBox extends TableObject {
	private String nickname;
	private String amount;
	private Label  toplabel;
	private Label  botlabel;
	private HorizontalPanel topbox;
	private VerticalPanel box;
	private TopControl    control;
	private int           numPlayers;
	private HTML          html;

	public PlayerBox(int seatNumber, String nickname, String amount, int numPlayers, TopControl control) {
		this.nickname = nickname;
		this.amount   = getChipAmount(amount);
		this.control  = control;
		this.numPlayers = numPlayers;
		Point p = Geometry.instance().getBoxLoc(seatNumber, numPlayers);
		setLoc(p.x, p.y);
		toplabel = new Label(nickname);
		botlabel = new Label(amount);
		toplabel.addStyleName("narrowlabel");
		botlabel.addStyleName("narrowlabel");
		
		box = new VerticalPanel();
		box.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		box.addStyleName("playerbox");
		box.add(toplabel);
		html = new HTML("<hr>");
		html.addStyleName("narrowany");
		box.add(html);
		box.add(botlabel);
		topbox = new HorizontalPanel();
		topbox.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topbox.addStyleName("playerframe");
		topbox.add(box);
		control.addWidget(topbox, p.x, p.y);
	}

	public void setAmount(String amount) {
		this.amount   = getChipAmount(amount);
		botlabel.setText(amount);
	}

	public void display() {
		//label.setText(nickname+"\n"+amount);
		/*
		int w = 90;
		int h = 34;
		int gutter  = 3;
		g2.setColor(Color.black);
		g2.fillRect(x,y,w,h);	
		g2.setColor(Color.white);
		g2.drawRect(x,y,w,h);	
		g2.setColor(Color.gray);
		g2.drawLine(x+1,y+h/2,x+w-1,y+h/2);	
		g2.setFont(TableResources.myfont);
		Font f = (Font) g2.getFont();
		g2.setColor(Color.lightGray);
		int sw;
		if ( nickname != null ) {
			sw = g2.getFontMetrics(f).stringWidth(nickname);
			g2.drawString(nickname, (int) (x+w/2-sw/2),y+h/2-gutter);
		}
		if ( amount != null ) {
			sw = g2.getFontMetrics(f).stringWidth(amount);
			g2.drawString(amount, (int) (x+w/2-sw/2),y+h-gutter);
		}
		g2.setColor(Color.magenta);*/
	}
	
	private String getChipAmount(String amount) {
		if (amount.endsWith(".0"))
			amount = amount.substring(0, amount.length()-2);
		return amount;
	}
	
	public String toString() {
		return "Nick: "+nickname+" amt: "+amount;
	}
	
	public void delete() {
		control.removeWidget(topbox);
	}
}

