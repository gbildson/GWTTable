package com.cardfight.client.poker.event;

import com.cardfight.client.poker.VisualFeedback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class VFCollectBets extends VFEvent  {
	private int potNum;
	private boolean doAnim;
	
	public VFCollectBets() {
		this.potNum = -1;
		this.doAnim = false;
		eventName = "VFCollectBets";
	}
	
	public VFCollectBets(int potNum, boolean doAnim) {
		this.potNum = potNum;
		this.doAnim = doAnim;
		eventName = "VFCollectBets";
	}
	
	@Override
	public void handleEvent(VisualFeedback vf) {
		vf.collectBets(potNum, doAnim);
	}

}
