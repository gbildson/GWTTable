package com.cardfight.client.table;

/*import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;*/
import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Button;

public class TakeSeat extends TableObject {
	private int             playerNum;
	private int             numPlayers;
	private TopControl      control;
    private Button          button;
	
	private void wrapImage() {
		button  = new Button("Take<br>Seat");
	    button.setStyleName("seatbutton");
		button.addClickHandler(new ClickHandler() {
		      public void onClick(ClickEvent event) {
      			  control.takeSeat(playerNum);
		      }
		 });
		control.addWidget(button, x, y);
	}
	
	public TakeSeat(int playerNum, int numPlayers, TopControl control) {
		Point p = Geometry.instance().getPlayerLoc(playerNum, numPlayers);
		p.x += 12;
		p.y += 14;
		setLoc(p.x, p.y);
		this.playerNum = playerNum;
		this.control = control;
		this.numPlayers = numPlayers;
		wrapImage();
	}

	public void display() {
	}
	
	public void delete() {
		control.removeWidget(button);
	}
}

