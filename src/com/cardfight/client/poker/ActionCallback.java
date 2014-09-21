package com.cardfight.client.poker;

import com.cardfight.client.poker.VisualFeedback;

public interface ActionCallback  extends WaitableCallback {

	//private double bet = -1.0d;
	//private VisualFeedback _vfeedback;
	
	//public ActionCallback(VisualFeedback vf) {
	//	_vfeedback = vf;
	//}
	
	public void deliverBettingResponse(double bet);
	
	public double getBettingResponse();
	

	public void responded();


	public void waitForResponse(long time);
}
