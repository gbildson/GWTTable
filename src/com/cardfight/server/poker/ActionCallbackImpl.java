package com.cardfight.server.poker;

import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.poker.ActionCallback;


public class ActionCallbackImpl implements ActionCallback {

	private double bet = -1.0d;
	private VisualFeedback _vfeedback;
	private boolean        hasResponded = false;
	
	public ActionCallbackImpl(VisualFeedback vf) {
		_vfeedback = vf;
	}
	
	public void deliverBettingResponse(double bet) {
		this.bet = bet;
		responded();
	}
	
	public double getBettingResponse() {
		return bet;
	}
	

	public void responded() {
		synchronized(this) {
			notify();
			hasResponded = true;
		}
	}


	public void waitForResponse(long time) {
		synchronized(this) {
			((VisualFeedbackMultiplexor)_vfeedback).setWaiting(true);
			long now = System.currentTimeMillis();
			//System.out.println("wait :"+time);
			if ( !hasResponded ) {
				try { wait(time);  
				} catch (InterruptedException ie) {
					//System.out.println("timeout");
					_vfeedback.actionTimeout();
				}
			}
			((VisualFeedbackMultiplexor)_vfeedback).setWaiting(false);
			long now2 = System.currentTimeMillis();
			if ( (now2-now) >= time )
				_vfeedback.actionTimeout();
			//System.out.println("done wait: "+(now2 -now));
		}
	}

}
