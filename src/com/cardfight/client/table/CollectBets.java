package com.cardfight.client.table;

class CollectBets implements TableAnimation {
	private HoldemTable vtable;
	private long        startTime;
	private int         endTime;

	public CollectBets(HoldemTable vtable, int potNum) {
		this.vtable = vtable;
		startTime   = System.currentTimeMillis();
		//endTime         = 0;
		//for (int i = 0; i < bets.length; i++) {
		//if ( bets[i] == null )
		//continue;
		//bets[i].collect(potNum, BET_SPEED);
		//int dTime = bets[i].getTimeToDeliver();
		//endTime = Math.max(endTime, dTime);
		//}
		endTime = vtable.setupBetCollection(potNum);
		//betsAnimation = this;
		vtable.setBetsAnimation(this);
		vtable.startAnim();
		vtable.addAnimation(this);
	}

	public void step() {
		int timeDelta = (int)(System.currentTimeMillis() - startTime);
		//System.out.println("tD="+timeDelta);

		//for (int i = 0; i < bets.length; i++) {
		//if ( bets[i] == null )
		//continue;
		//bets[i].move(timeDelta);
		//}
		vtable.moveBets(timeDelta);
		if (timeDelta > (endTime+10)) {
			done();
		}
		vtable.repaint();
	}

	public void done() {
		//betsAnimation = null;
		vtable.setBetsAnimation(null);

		//for (int i = 0; i < htable.bets.length; i++) {
		//ToolTipHelper.clearToolTip(htable.bets[i]);
		//htable.bets[i] = null;
		//}
		vtable.clearBets();
		vtable.stopAnim();
		vtable.removeAnimation(this);
		//System.out.println("**Out of Done** Bets ");
	}

	public int maxDelay() {
		return (endTime+80);
	}
}

