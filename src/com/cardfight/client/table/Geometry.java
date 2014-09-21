
package com.cardfight.client.table;

//import java.awt.Point;

public class Geometry {
	public static final int FORWARD  = 1;
	public static final int BACKWARD = -1;
	public static final int INPLACE  = 0;
	public static final int CHIP_VERTICAL   = 5;
	public static final int CHIP_HORIZONTAL = 23;
	public static final int START_ANGLE = -72;
	public static final int start_angle[] = { 0, -10, -44, -71, -59, -72, -72, -72, -72};
	public static final int MAX_ANGLE   = (360-6);
	private static final int MAX_EXACT_LOCATIONS = 5;
	public static double CARD_SPEED = 1.5;
	public static double POT_SPEED  = 0.4;
	public static double BET_SPEED  = 0.6;
	private Point   commonCardLoc;
	private Point   dealLoc;
	private Point   potTotalLoc;
	private Point[] potLocs;

	private Point[] playerLocs;
	private Point[] boxLocs;
	private Point[] dealerLocs;
	private Point[] smallCardLocs;
	private Point[] betLocs;
	private int[]   betDirection;
	private PlayerGeometry[][] playerGeom;
	
	// D 5 - 3 x: 139 y: 274
	
	private static Geometry instance;
	
	public static Geometry instance() {
		if ( instance == null )
			instance = new Geometry();
		return instance;
	}
	
	public Geometry() {
		
		// Player box rules - based on horizon angle
		// -5 to 190  - below unless
		// 110 to 150 - to right (down a little - edge of table)
		// 190 to 270 - to left
		// 270 to 355 - to right
		// Dealer Marker - should be to the left at table edge.
		// Small Cards therefore on right at table edge.
		// Bets ??
		// Player Circle radius = 39 => PlayerLoc = (xo+(322+39-10)*cos, yo+(161+39-10)*sin)
		// Angle = -75 + (360-50)*playerNum /numPlayers.
		
		playerGeom = new PlayerGeometry[10][];

		// Define PlayerGeometry for 2 player table
		PlayerGeometry twoPlayer[] = new PlayerGeometry[2];
		PlayerGeometry pg = new PlayerGeometry();
		pg.playerLocs = new Point(698,189);
		pg.boxLocs    = new Point(698,189+78);
		pg.dealerLocs = new Point(673,236);
		pg.smallCardLocs = new Point(672,196);
		pg.betLocs       = new Point(672-26,196+12);
		pg.betDirection = BACKWARD;
		twoPlayer[0] = pg;
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(30,189);
		pg.boxLocs    = new Point(30,189+78);
		pg.dealerLocs = new Point(99,186);
		pg.smallCardLocs = new Point(110,221);
		pg.betLocs       = new Point(110+30,221+12);
		pg.betDirection = FORWARD;
		twoPlayer[1] = pg;
		playerGeom[2] = twoPlayer;
		
		// Define PlayerGeometry for 3 player table
		PlayerGeometry threePlayer[] = new PlayerGeometry[3];
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(698,189);
		pg.boxLocs    = new Point(698,189+78);
		pg.dealerLocs = new Point(673,236);
		pg.smallCardLocs = new Point(672,196);
		pg.betLocs       = new Point(672-26,196+12);
		pg.betDirection = BACKWARD;
		threePlayer[0] = pg;
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(351,343);
		pg.boxLocs    = new Point(351,343+78);
		pg.dealerLocs = new Point(336,335);
		pg.smallCardLocs = new Point(407,314);
		pg.betLocs       = new Point(407,314-36);
		pg.betDirection = FORWARD;
		threePlayer[1] = pg;
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(30,189);
		pg.boxLocs    = new Point(30,189+78);
		pg.dealerLocs = new Point(99,186);
		pg.smallCardLocs = new Point(110,221);
		pg.betLocs       = new Point(110+30,221+12);
		pg.betDirection = FORWARD;
		threePlayer[2] = pg;
		playerGeom[3] = threePlayer;
		
		// Define PlayerGeometry for 4 player table
		PlayerGeometry fourPlayer[] = new PlayerGeometry[4];
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(629,74);
		pg.boxLocs    = new Point(629+78+2,74);
		pg.dealerLocs = new Point(662,154);
		pg.smallCardLocs = new Point(604,121);
		pg.betLocs       = new Point(604-26,121+12);
		pg.betDirection = BACKWARD;
		fourPlayer[0] = pg;
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(629,273);
		pg.boxLocs    = new Point(629,273+78);
		pg.dealerLocs = new Point(605,294);
		pg.smallCardLocs = new Point(610,253);
		pg.betLocs       = new Point(610-26,253+12);
		pg.betDirection = BACKWARD;
		fourPlayer[1] = pg;
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(102,273);
		pg.boxLocs    = new Point(102+78,273+42);
		pg.dealerLocs = new Point(119,255);
		pg.smallCardLocs = new Point(175,253);
		pg.betLocs       = new Point(175+30,253+12);
		pg.betDirection = FORWARD;
		fourPlayer[2] = pg;
		pg = new PlayerGeometry();
		pg.playerLocs = new Point(102,74);
		pg.boxLocs    = new Point(102-86,74);
		pg.dealerLocs = new Point(184,108);
		pg.smallCardLocs = new Point(175,134);
		pg.betLocs       = new Point(175+30,134+12);
		pg.betDirection = FORWARD;
		fourPlayer[3] = pg;
		playerGeom[4] = fourPlayer;
		
		// Define PlayerGeometry for 5 player table
		PlayerGeometry fivePlayer[] = new PlayerGeometry[5];
		fivePlayer[0] = fourPlayer[0];
		fivePlayer[1] = fourPlayer[1];
		fivePlayer[2] = threePlayer[1];
		fivePlayer[3] = fourPlayer[2];
		fivePlayer[4] = fourPlayer[3];
		playerGeom[5] = fivePlayer;

		
		

		// Player locations
		playerLocs = new Point[10];
		playerLocs[0] = new Point(480, 7);
		playerLocs[1] = new Point(620, 65);
		playerLocs[2] = new Point(700, 170);
		
		playerLocs[3] = new Point(600, 280);
		playerLocs[4] = new Point(450, 330);
		playerLocs[5] = new Point(270, 330);
		//playerLocs[6] = new Point(115, 280);
		playerLocs[6] = new Point(102, 270);
		playerLocs[7] = new Point(20, 170);
		playerLocs[8] = new Point(100, 65);
		playerLocs[9] = new Point(240, 7);

		// Player Information locations
		boxLocs = new Point[10];
		boxLocs[0] = new Point(555,15);
		boxLocs[1] = new Point(695,80);
		boxLocs[2] = new Point(700,245);
		boxLocs[3] = new Point(590,360);
		boxLocs[4] = new Point(445,410);
		boxLocs[5] = new Point(270,410);
		//boxLocs[6] = new Point(110,360);
		boxLocs[6] = new Point(175,315);
		boxLocs[7] = new Point(10,250);
		boxLocs[8] = new Point(10,80);
		boxLocs[9] = new Point(150,15);

		// Dealer button locations
		dealerLocs = new Point[10];
		dealerLocs[0] = new Point(510, 90);
		dealerLocs[1] = new Point(650, 150);
		dealerLocs[2] = new Point(680, 230);
		dealerLocs[3] = new Point(580, 305);
		dealerLocs[4] = new Point(530, 330);
		//dealerLocs[5] = new Point(250, 330);
		dealerLocs[5] = new Point(270, 310);
		//dealerLocs[6] = new Point(190, 310);
		dealerLocs[6] = new Point(130, 248);
		dealerLocs[7] = new Point(100, 225);
		dealerLocs[8] = new Point(130, 150);
		dealerLocs[9] = new Point(270, 90);

		// Small card locations
		smallCardLocs = new Point[10];
		smallCardLocs[0] = new Point(540, 90);
		smallCardLocs[1] = new Point(600, 115);
		smallCardLocs[2] = new Point(675, 190);
		smallCardLocs[3] = new Point(588, 270);
		smallCardLocs[4] = new Point(427, 335);
		smallCardLocs[5] = new Point(352, 335);
		smallCardLocs[6] = new Point(187, 270);
		smallCardLocs[7] = new Point(100, 190);
		smallCardLocs[8] = new Point(176, 115);
		smallCardLocs[9] = new Point(237, 90);

		// Bet locations
		betLocs = new Point[10];
		betLocs[0] = new Point(510, 125); // BACKWARD
		betLocs[1] = new Point(615, 160); // BACKWARD
		betLocs[2] = new Point(640, 210); // BACKWARD
		betLocs[3] = new Point(560, 275); // BACKWARD
		betLocs[4] = new Point(495, 310); // BACKWARD
		//betLocs[5] = new Point(285, 310);
		betLocs[5] = new Point(295, 310);
		betLocs[6] = new Point(215, 275);
		betLocs[7] = new Point(130, 210);
		betLocs[8] = new Point(160, 160);
		betLocs[9] = new Point(260, 125);
		
		// Direction for bet display
		betDirection = new int[10];
		betDirection[0] = BACKWARD;
		betDirection[1] = BACKWARD;
		betDirection[2] = BACKWARD;
		betDirection[3] = BACKWARD;
		betDirection[4] = BACKWARD;
		betDirection[5] = FORWARD;
		betDirection[6] = FORWARD;
		betDirection[7] = FORWARD;
		betDirection[8] = FORWARD;
		betDirection[9] = FORWARD;

		// Pot locations
		potLocs = new Point[10];
		potLocs[0] = new Point(340,245);
		potLocs[1] = new Point(380,265);
		potLocs[2] = new Point(300,265);
		potLocs[3] = new Point(340,300);
		potLocs[4] = new Point(380,340);
		potLocs[5] = new Point(300,340);
		potLocs[6] = new Point(340,380);
		potLocs[7] = new Point(380,380);
		potLocs[8] = new Point(300,380);
		potLocs[9] = new Point(340,200);

		commonCardLoc = new Point(270, 160);

		// Point from which dealer deals
		dealLoc = new Point(300, 50);

		// Pot Total Ceter Location
		potTotalLoc = new Point(364, 12);
	}

	private static final int [][] PLAYERLOC = { {0}, {1}, {2,7}, {2,5,7}, {1,3,6,8}, {1,3,4,6,8}, {0,2,4,5,7,9}, {0,2,4,5,7,8,9},  
	  {0,2,3,4,5,7,8,9}, {0,1,2,3,4,5,6,7,9}, {0,1,2,3,4,5,6,7,8,9}
	};
	private int adjustPlayerNumForTableSize(int playerNum, int numPlayers) {
		/*if ( numPlayers == 2 ) {
			if ( playerNum == 0 )
				playerNum = 2;
			else if ( playerNum == 1 )
				playerNum = 7;
			return playerNum;
		}*/
		playerNum = PLAYERLOC[numPlayers][playerNum];
		return playerNum;
	}
	
	private double trueTotDist() {
		double a = 322;
		double b = 161;
		double lastX = -1;
		double lastY = -1;
		double totDist = 0;
		for ( int i = 0; i < 360; i++) {
			double angle = (i) * Math.PI / 180;
			double x = (a) * Math.cos(angle);
			double y = (b) * Math.sin(angle);
			
			if ( lastX != -1 ) {
				double dx = x - lastX;
				double dy = y - lastY;
				double dist = Math.sqrt( dx * dx + dy * dy);
				totDist += dist;
			}
			lastX = x;
			lastY = y;
		}
		return totDist;
	}
	
	public double trueAngleForDist(int fraction, int tot ) {
		double totDist = trueTotDist();
		double fractDist = totDist * (((double) fraction))/ ((double) tot);
		int i = 0;
		double a = 322;
		double b = 161;
		double lastX = -1;
		double lastY = -1;
		double sumDist = 0;
		for (; i < 360; i++) {
			double angle = (i) * Math.PI / 180;
			double x = (a) * Math.cos(angle);
			double y = (b) * Math.sin(angle);
			
			if ( lastX != -1 ) {
				double dx = x - lastX;
				double dy = y - lastY;
				double dist = Math.sqrt( dx * dx + dy * dy);
				sumDist += dist;
				if (sumDist >= fractDist)
					return (double)(i);
			}
			lastX = x;
			lastY = y;
		}
		return (double)(i);
	}
	
	private double totDist() {
		double a = 322;
		double b = 161;
		double lastX = -1;
		double lastY = -1;
		double totDist = 0;
		for ( int i = 0; i < MAX_ANGLE; i++) {
			double angle = (i+START_ANGLE) * Math.PI / 180;
			double x = (a) * Math.cos(angle);
			double y = (b) * Math.sin(angle);
			
			if ( lastX != -1 ) {
				double dx = x - lastX;
				double dy = y - lastY;
				double dist = Math.sqrt( dx * dx + dy * dy);
				totDist += dist;
			}
			lastX = x;
			lastY = y;
		}
		return totDist;
	}
	
	private double angleForDist(int playerNum, int numPlayers, double offset) {
		double totDist = totDist();
		double fractDist = totDist * (((double) playerNum)+offset)/ ((double) numPlayers);
		int i = 0;
		double a = 322;
		double b = 161;
		double lastX = -1;
		double lastY = -1;
		double sumDist = 0;
		for (; i < MAX_ANGLE; i++) {
			double angle = (i+start_angle[numPlayers-2]) * Math.PI / 180;
			double x = (a) * Math.cos(angle);
			double y = (b) * Math.sin(angle);
			
			if ( lastX != -1 ) {
				double dx = x - lastX;
				double dy = y - lastY;
				double dist = Math.sqrt( dx * dx + dy * dy);
				sumDist += dist;
				if (sumDist >= fractDist)
					return (double)(i + start_angle[numPlayers-2]);
			}
			lastX = x;
			lastY = y;
		}
		return (double)(i+start_angle[numPlayers-2]);
	}
	
	// Player Circle radius = 39 => PlayerLoc = (xo+(322+39-10)*cos, yo+(161+39-10)*sin)
	// Angle = -75 + (360-50)*playerNum /numPlayers.
	private Point newAdjustedPlayerLoc(int playerNum, int numPlayers) {
		double a = 322;
		double b = 161;
		double ox = 402;
		double oy = 216;
		Point newPoint = null;
		double angle = angleForDist(playerNum, numPlayers, 0.0d);//START_ANGLE + MAX_ANGLE * playerNum / numPlayers;
		angle = angle * Math.PI / 180;
		double x = ox + (a+39-22-4) * Math.cos(angle);
		double y = oy + (b+39-22-4) * Math.sin(angle);
		int ix = ((int)(x+0.5));
		int iy = ((int)(y+0.5));
		newPoint = new Point( ix-39, iy-39-6  );
		return newPoint;
	}
	
	// Player Circle radius = 39 => PlayerLoc = (xo+(322+39-10)*cos, yo+(161+39-10)*sin)
	// Angle = -75 + (360-50)*playerNum /numPlayers.
	private Point newEdgePlayerLoc(int playerNum, int numPlayers) {
		double a = 322;
		double b = 161;
		double ox = 402;
		double oy = 216;
		Point newPoint = null;
		double angle = angleForDist(playerNum, numPlayers, 0.0d);//START_ANGLE + MAX_ANGLE * playerNum / numPlayers;
		angle = angle * Math.PI / 180;
		double x = ox + (a) * Math.cos(angle);
		double y = oy + (b) * Math.sin(angle);
		int ix = ((int)(x+0.5));
		int iy = ((int)(y+0.5));
		newPoint = new Point( ix, iy  );
		return newPoint;
	}
	
	public Point tangentialDealerLoc(int playerNum, int numPlayers) {
		//Point ploc = newEdgePlayerLoc(playerNum, numPlayers);
		Point ploc = newAdjustedPlayerLoc(playerNum, numPlayers);

		double ox = 402;
		double oy = 216;
		double x  = ploc.x;
		double y  = ploc.y;
		double dx = x - ox;
		double dy = y - oy;
		double len = Math.sqrt(dx*dx+dy*dy);
		
		//ploc.x = (int) ((-dy*39/len)+0.5+x);		
		//ploc.y = (int) ((dx*39/len)+0.5+y);
		
		//ploc = newAdjustedPlayerLoc(playerNum, numPlayers);
		ploc.x = (int) ((-dx*45/len)+0.5+x);		
		ploc.y = (int) ((-dy*45/len)+0.5+y);
		ploc.x += 39;
		ploc.y += (39);

		return ploc;
	}
	
	public Point newtangentialDealerLoc(int playerNum, int numPlayers) {
		//Point ploc = newEdgePlayerLoc(playerNum, numPlayers);
		Point ploc = newAdjustedPlayerLoc(playerNum, numPlayers);

		double ox = 402;
		double oy = 216;
		double x  = ploc.x+39;
		double y  = ploc.y+39+6;
		double dx = x - ox;
		double dy = y - oy;
		double len = Math.sqrt(dx*dx+dy*dy);
		
		//ploc.x = (int) ((-dy*39/len)+0.5+x);		
		//ploc.y = (int) ((dx*39/len)+0.5+y);
		
		//ploc = newAdjustedPlayerLoc(playerNum, numPlayers);
		//ploc.x = (int) ((-dx*45/len)+0.5+x);		
		//ploc.y = (int) ((-dy*45/len)+0.5+y);
		//ploc.x += 39;
		//ploc.y += (39);
		
		double dvx = (-dx*42/len);		
		double dvy = (-dy*42/len);
		double angle = angleForDist(playerNum, numPlayers, 0.0d);
		double origAngle = angle;
		if ( angle < 0 ) {
			angle += 180;
		}
		if ( angle > 160 ) {
			angle -= 180;
		}
		
		if ( angle < 30) {
			angle = 9 + (30 - angle) * 3 / 4 ;
		} else if ( origAngle < 180) {
			angle = (angle) * 2 / 5 /*+ (angle-30) * 2 / 9*/;
		} else {
			angle = angle * 2 / 5;
			com.cardfight.client.GWTTable.logStaticServer("***** GOT HERE **********");
		}
		com.cardfight.client.GWTTable.logStaticServer("angle = "+origAngle+" nangle = "+angle);
		angle = -angle;

		com.cardfight.client.GWTTable.logStaticServer("before  x= "+dvx+" y = "+dvy);
		angle = angle * Math.PI / 180;
		double dnvx = dvx * Math.cos(angle) - dvy * Math.sin(angle);
		double dnvy = dvy * Math.cos(angle) + dvx * Math.sin(angle);
		com.cardfight.client.GWTTable.logStaticServer("after  x= "+dnvx+" y = "+dnvy);
		ploc.x = (int) (dnvx+0.5+x);		
		ploc.y = (int) (dnvy+0.5+y);
		//ploc.x += 39;
		//ploc.y += (39+6);
		if ( origAngle < 165 && origAngle >= 0 ) {
			ploc.x -= 22;
			ploc.y -= 20;
		} else if ( origAngle >= 165 && origAngle < 190 ) {
			//ploc.x += 22;
			ploc.y -= 20;
		}
		ploc.y -= 6;
		
		//Point ploc2 = newEdgePlayerLoc(playerNum, numPlayers);
		//double dx2 = ploc2.x - ploc.x;
		//double dy2 = ploc2.y - ploc.y;
		//double len2 = 39 - Math.sqrt(dx2*dx2+dy2*dy2);
		//double rx = Math.sqrt(39*39 - len2 * len2)+6;
		
		//ploc2.x = (int) ((-dy*(rx)/len)+0.5+ploc2.x);		
		//ploc2.y = (int) ((dx*(rx)/len)+0.5+ploc2.y);
		
		return ploc;
	}
	
	public Point truetangentialDealerLoc(int playerNum, int numPlayers) {
		//Point ploc = newEdgePlayerLoc(playerNum, numPlayers);
		Point ploc = newAdjustedPlayerLoc(playerNum, numPlayers);

		double ox = 402;
		double oy = 216;
		double x  = ploc.x;
		double y  = ploc.y;
		double dx = x - ox;
		double dy = y - oy;
		double len = Math.sqrt(dx*dx+dy*dy);
		
		ploc.x = (int) ((-dy*45/len)+0.5+x);		
		ploc.y = (int) ((dx*45/len)+0.5+y);
		
		//ploc = newAdjustedPlayerLoc(playerNum, numPlayers);
		//ploc.x = (int) ((-dx*45/len)+0.5+x);		
		//ploc.y = (int) ((-dy*45/len)+0.5+y);
		ploc.x += 39;
		ploc.y += (39);
		
		x  = ploc.x;
		y  = ploc.y;
		dx = x - ox;
		dy = y - oy;
		len = Math.sqrt(dx*dx+dy*dy);
		ploc.x = (int) ((-dx*45/len)+0.5+x);		
		ploc.y = (int) ((-dy*45/len)+0.5+y);

		return ploc;
	}
	
	// For dealer loc try + 6 degrees on PlayerLoc
	private Point newAdjustedDealerLoc(int playerNum, int numPlayers) {
		double a = 322;
		double b = 161;
		double ox = 402;
		double oy = 216;
		Point newPoint = null;
		double angle = angleForDist(playerNum, numPlayers, 0.26d);//START_ANGLE + 8 + MAX_ANGLE * playerNum / numPlayers;
		angle = angle * Math.PI / 180;
		double x = ox + (a-22-5-4-0) * Math.cos(angle);
		double y = oy + (b-20-5-4-0) * Math.sin(angle);
		int ix = ((int)(x+0.5));
		int iy = ((int)(y+0.5));
		newPoint = new Point( ix-11, iy-18-2 );
		return newPoint;
	}
	
	// For dealer loc try + 6 degrees on PlayerLoc
	private Point newAdjustedCardLoc(int playerNum, int numPlayers) {
		double a = 322;
		double b = 161;
		double ox = 402;
		double oy = 216;
		Point newPoint = null;
		double offset = -0.012 * numPlayers - 0.04 / numPlayers;
		double angle = angleForDist(playerNum, numPlayers, offset);//START_ANGLE - 7 + MAX_ANGLE * playerNum / numPlayers;
		if ( playerNum == 0 ) {
			angle -= 10 / (numPlayers-1);
		}
		angle = angle * Math.PI / 180;
		double x = ox + (a-22-5-12-4) * Math.cos(angle);
		double y = oy + (b-22-9-17+4-4) * Math.sin(angle);
		int ix = ((int)(x+0.5));
		int iy = ((int)(y+0.5));
		newPoint = new Point( ix-14+2, iy-25+1+2 );
		return newPoint;
	}
	
	public Point getPlayerLoc(int playerNum, int numPlayers) {
		//playerNum = adjustPlayerNumForTableSize(playerNum, numPlayers);
		if ( numPlayers <= MAX_EXACT_LOCATIONS )
			return playerGeom[numPlayers][playerNum].playerLocs;
		if ( numPlayers == -10 )
			return playerLocs[playerNum];
		return 	newAdjustedPlayerLoc(playerNum, numPlayers);
	}

	public Point getDealerLoc(int playerNum, int numPlayers) {
		//playerNum = adjustPlayerNumForTableSize(playerNum, numPlayers);
		if ( numPlayers <= MAX_EXACT_LOCATIONS )
			return playerGeom[numPlayers][playerNum].dealerLocs;
		if ( numPlayers == -10 )
			return dealerLocs[playerNum];
		//return newAdjustedDealerLoc(playerNum, numPlayers);
		return newtangentialDealerLoc(playerNum, numPlayers);
	}

	public Point getSmallCardLoc(int playerNum, int numPlayers) {
		//playerNum = adjustPlayerNumForTableSize(playerNum, numPlayers);
		if ( numPlayers <= MAX_EXACT_LOCATIONS )
			return playerGeom[numPlayers][playerNum].smallCardLocs;
		if ( numPlayers == -10 )
			return smallCardLocs[playerNum];
		return newAdjustedCardLoc(playerNum, numPlayers);

	}

	public int getSCXOffset(int cardNum) {
    		return cardNum*5;
	}

	public int getSCYOffset(int cardNum) {
    		return cardNum*3;
	}

	public Point getDealLoc() {
		return dealLoc;
	}

	public Point getCommonCardLoc() {
		return commonCardLoc;
	}

	public Point getPotLoc(int potNumber) {
		return potLocs[potNumber];
	}

	public Point getBetLoc(int seatNum, int numPlayers) {
		if ( numPlayers <= MAX_EXACT_LOCATIONS )
			return playerGeom[numPlayers][seatNum].betLocs;
		//seatNum = adjustPlayerNumForTableSize(seatNum, numPlayers);
		//return betLocs[seatNum];
		//double angle = angleForDist(seatNum, numPlayers, 0.0d);
		Point p = newAdjustedCardLoc(seatNum, numPlayers);
		int locRatio = (seatNum*1000/numPlayers);
		/*if (  locRatio < 440 ) {
			p.x -= 26;
		} else {
			p.x += 26;
		}*/
		if ( numPlayers == 2) {
			if ( locRatio < 500 ) {
				p.x -= 26;
				p.y += 12;
			} else {
				p.x += 30;
				p.y += 12;
			}
		} else if ( locRatio < 180 ) {
			p.y += 31+16;
		} else if ( locRatio < 280 ) {
			p.x -= 26;
			p.y += 12;
		} else if ( locRatio < 680 ) {
			p.y -= (31-8);
		} else if ( locRatio < 780 ) {
			p.x += 30;
			p.y += 12;
		} else {
			p.y += 31+16;
		}
		return p;
	}

	public int getBetDirection(int seatNum, int numPlayers) {
		//seatNum = adjustPlayerNumForTableSize(seatNum, numPlayers);
		if ( numPlayers <= MAX_EXACT_LOCATIONS )
			return playerGeom[numPlayers][seatNum].betDirection;
		if ( numPlayers == -10 )
			return betDirection[seatNum];
		int locRatio = (seatNum*1000/numPlayers);
		if (  locRatio < 440 )
			return BACKWARD;
		else
			return FORWARD;
	}

	public Point getBoxLoc(int seatNum, int numPlayers) {
		//seatNum = adjustPlayerNumForTableSize(seatNum, numPlayers);
		if ( numPlayers <= MAX_EXACT_LOCATIONS )
			return playerGeom[numPlayers][seatNum].boxLocs;
		if ( numPlayers == -10 )
			return boxLocs[seatNum];
		Point p = newAdjustedPlayerLoc(seatNum, numPlayers);
		int locRatio = (seatNum*1000/numPlayers);
		if ( numPlayers == 2 ) {
			p.y += 78 + 0;
		} else if ( locRatio < 100 ) {
			p.x += 78;
			p.y += 15;
		} else if ( locRatio < 180 ) {
			p.x += 52;
			p.y += 78 - 6;
		} else if ( locRatio < 500 ) {
			p.y += 78 + 0;
		} else if ( locRatio < 600 ) {
			p.x += 78;
			p.y += 24;
		} else if ( locRatio < 700 ) {
			p.y += 78 + 0;
		} else if ( locRatio < 800 ) {
			p.x -= 48;
			p.y += 78;
		} else {
			p.x -= (82+8);
			p.y += 15;
		}
			
		return p;
	}

	public Point getPotTotalLoc() {
		return potTotalLoc;
	}
}
