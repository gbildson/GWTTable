package com.cardfight.client;

import com.google.gwt.user.client.ui.Widget;

public interface TopControl {
	  public void addWidget(Widget w, int x, int y);
	  public void removeWidget(Widget w);
	  public void waitUntil(long t);	  
	  public void logLocally(String log);
	  public void logServer(String log);
	  public void addBetResponse(double bet);
	  public void takeSeat(int seatNum);
}
