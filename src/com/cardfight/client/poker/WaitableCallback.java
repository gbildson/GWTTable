package com.cardfight.client.poker;

public interface WaitableCallback {
	public void waitForResponse(long time); 
	public void responded();
}
