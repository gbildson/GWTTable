package com.cardfight.server.poker;

public interface WaitableCallback {
	public void waitForResponse(long time); 
	public void responded();
}
