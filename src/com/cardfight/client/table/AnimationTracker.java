package com.cardfight.client.table;

//import java.awt.Point;

public class AnimationTracker {
	private double speed;
	private double timeToDeliver;
	private int    xdelta;
	private int    ydelta;
	private Point  source;
	private Point  dest;

	public AnimationTracker(Point source, Point dest, double speed) {
		this.source = source;
		this.dest   = dest;
		this.speed  = speed;
		compute();
	}

	public void updateDest(Point newDest) {
		dest = newDest;
		compute();
	}

	private void compute() {
		xdelta = dest.x - source.x;
		ydelta = dest.y - source.y;
		double distance = Math.sqrt(xdelta * xdelta + ydelta * ydelta);
		timeToDeliver = (distance / speed);
	}

	public Point move(int timeDelta) {
		int maxTimeToDeliver = (int) (timeToDeliver * 0.96);
		if (timeDelta < maxTimeToDeliver) {
			double timeFraction = ((double)timeDelta) / timeToDeliver;
			int x;
			int y;
			x = source.x + ((int) (((double)xdelta) * timeFraction));
			y = source.y + ((int) (((double)ydelta) * timeFraction));
			//System.out.println("tD: "+timeDelta+ " tTD: "+timeToDeliver+ " x: "+x+ " xd: "+ xd+ " y: "+y+ " yd: "+ yd);
			return new Point(x, y);
		} else {
			return dest;
			//System.out.println("FIN - tD: "+timeDelta+ " tTD: "+timeToDeliver+ " x: "+x+ " xd: "+ xd+ " y: "+y+ " yd: "+ yd);
		}
	}

	public int getTimeToDeliver() {
		return (int) (timeToDeliver+0.5);
	}
}
