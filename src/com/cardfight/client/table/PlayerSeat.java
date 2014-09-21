package com.cardfight.client.table;

/*import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;*/
import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class PlayerSeat extends TableObject {
	private static final long CLEAR_STATUS_TIME = 1600;

	private String          statusString = null;
	private Label           statusLabel  = null;
	private HorizontalPanel statusBox    = null;
	private long            statusLastSetTime = -1;
	private Image           image = null;
	private int             playerNum;
	private int             numPlayers;
	private TopControl      control;
	//private GWTCanvas canvas   = null;
	
	private void wrapImage() {
		Point p = Geometry.instance().getPlayerLoc(playerNum, numPlayers);
		//System.out.println("ci : "+TableResources.circleImg2);
		image = new Image("/img/circle.gif");
		control.addWidget(image, p.x, p.y);
		//canvas   = null
	}
	
	public PlayerSeat(int playerNum, int numPlayers, TopControl control) {
		Point p = Geometry.instance().getPlayerLoc(playerNum, numPlayers);
		setLoc(p.x, p.y);
		this.playerNum = playerNum;
		this.control = control;
		this.numPlayers = numPlayers;
		wrapImage();
	}

	public void display() {
		//wrapImage();
		//g2.drawImage(TableResources.circleImg, x, y);

		// Display a status line if desired
		if (statusString != null) {
			/*g2.setColor(Color.black);
			g2.setFont(TableResources.cuteFont);
			Font f = (Font) g2.getFont();
			g2.setColor(Color.lightGray);
			int sw;
			sw = g2.getFontMetrics(f).stringWidth(statusString);
			g2.setColor(Color.black);
			g2.drawString(statusString, (int) (x+40-sw/2),y+42);*/
		}
	}

	public void setStatusText(String text) {
		statusString = text;
		if ( statusBox != null ) {
			control.removeWidget(statusBox);
		}
		statusLabel  = new Label(text);
		//statusLabel.addStyleName("playerstatus");
		statusBox = new HorizontalPanel();
		statusBox.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		statusBox.addStyleName("playerstatus");
		statusBox.add(statusLabel);
		//int sw = statusLabel.getOffsetWidth();
		int sw = 10*text.length();
		control.addWidget(statusBox, (x+42-sw/2), y+30);
		//System.out.println("sw = "+sw);
		statusLastSetTime = System.currentTimeMillis();
	}

	public boolean checkToClearStatusString(long now) {
		if (statusLastSetTime > 0  && statusLastSetTime < (now - CLEAR_STATUS_TIME)) {
			statusString = null;
			control.removeWidget(statusBox);
			statusLabel = null;
			statusBox = null;
			statusLastSetTime = -1;
			return true;
		}
		return false;
	}
	
	public String toString() {
		return statusString;
	}
	
	public void delete() {
		control.removeWidget(image);
	}
}

