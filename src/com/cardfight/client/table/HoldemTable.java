
package com.cardfight.client.table;

/*import java.awt.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.PopupFactory;
import javax.swing.Popup;
import javax.swing.SwingUtilities;*/

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

//import java.awt.image.ImageObserver;
//import java.awt.event.MouseAdapter;
//import java.awt.event.*;
//import java2d.*;
import java.util.*;
import java.io.*;
//import java.text.NumberFormat;

import com.cardfight.client.TopControl;
import com.cardfight.client.poker.Card;
import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.PlayerState;
import com.cardfight.client.poker.Suit;
import com.cardfight.client.poker.TableState;
//import com.cardfight.server.poker.*;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.cardfight.client.poker.ActionCallback;

/**
 * Animated gif with a transparent background.
 */
public class HoldemTable /*extends AnimatingSurface implements ImageObserver,
  Runnable/*, MouseMotionListener*/ {

	//private static final Log LOG = LogFactory.getLog(HoldemTable.class);
	
	//private Geometry geometry;

 
    //---------------------------
    private VisualFeedbackImpl 		  vf;
	private TableAnimation    		  cardAnimation;
	private ArrayList<TableAnimation> potsAnimation;
	private TableAnimation     		  betsAnimation;
	//private Timer              		  localTimer;
	private ActionPanel               actionPanel;
	private Image                     tableImg;
	private TopControl                control;


	/*
	 *  Assumes single threaded access
     */  
	public VisualFeedbackImpl getVisualFeedback() {
		if (vf == null)
           vf = new VisualFeedbackImpl(this, control); 
		return vf;
	}
	//------------------------------------

    public HoldemTable(TopControl control) {
    	this.control = control;
		//htable = this;

		//geometry = new Geometry();
    	actionPanel = ActionPanel.create(control);
		potsInMotion  = new PartialPot[10][];
		potsAnimation = new ArrayList<TableAnimation>();
		//localTimer = new Timer(true);
		//localTimer.scheduleAtFixedRate(new ClearPlayerSeatStatus(), 1000l, 200l);
		//localTimer.scheduleAtFixedRate(new ShowToolTip(), 3000l, 200l);
		//addMouseMotionListener(this);
		//startClock();
		
		//tableImg = new Image("/img/table.jpg");
		//GWTTable.addWidget(tableImg, 0, 0);
    }

	public void startAnim() {
		//start();
	}

	public void stopAnim() {
		//if (cardAnimation == null && potsAnimation == null && betsAnimation == null) 
			//stop();
	}


    public void reset(int w, int h) { 
		//System.out.println("Reset time = " + System.currentTimeMillis());
    }

    private static long startTime = System.currentTimeMillis();
    private static long lastTime = System.currentTimeMillis();
    public static String getTotTime() {
    	long thisTime = System.currentTimeMillis();
		return ""+(thisTime - startTime);
    }

    public void step() { 
        ArrayList<TableAnimation> animList2 = new ArrayList<TableAnimation>();
        for ( TableAnimation anim : animList )
            animList2.add(anim);
        
        for( TableAnimation anim : animList2)
        	anim.step();
        
		//for (int i = 0; i < animList.size(); i++) {
			//TableAnimation anim = animList.get
			//anim.step();
		//}
    	/*if (cardAnimation != null) 
			cardAnimation.step();
		if (potsAnimation.size() > 0) {
			for (TableAnimation pa : potsAnimation)
				pa.step();
		}
		if (betsAnimation != null) 
			betsAnimation.step();
		*/
        long thisTime = System.currentTimeMillis();
    	clearPlayerSeatStatus(thisTime);
    	
		//System.out.println("tot time: " + (thisTime - startTime) + " incr time: "+(thisTime - lastTime) );
		lastTime = thisTime;
		repaint();
	}
    
    private ArrayList<TableAnimation> animList = new ArrayList<TableAnimation>();
    public synchronized void addAnimation(TableAnimation anim){
    	animList.add(anim);
    }
    public synchronized void removeAnimation(TableAnimation anim){
    	animList.remove(anim);
    }

	//public Image getImage(String filename) {
		//return super.getImage(filename);
	//}


	private TableState table = null; //new TableState(10);
	private int             numPlayers = 0;
	private PlayerSeat      playerSeats[];
	private TakeSeat        takeSeat[];
	private PlayerBox       playerBoxes[];
	private DealerMarker    dealerMarker;
	private Pot             pots[];
	private CommonCards     commonCards;
	private PlayersCards    playersCards[];
	private SmallCard       cardInMotion;
	private PartialPot      potsInMotion[][];
	private SmallCards      smallCards[];
	private Bet             bets[];
	private PotTotal        potTotal;
	//private HoldemTable     htable;
	private int             activePlayerSeat = -1;
	
	public void createPlayerSeats( int numPlayers ){
		playerSeats  = new PlayerSeat[numPlayers];
		takeSeat  = new TakeSeat[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			playerSeats[i] = new PlayerSeat(i, numPlayers, control);
		}
	}
	
	public void clearPlayerSeatStatus(long now) {
		if ( playerSeats == null ) return;
		for (int i = 0; i < playerSeats.length; i++) {
			if (playerSeats[i].checkToClearStatusString(now))
				;//repaint();
		}
	}
	
	public void setPlayerSeatStatus(int playerNum, String state) {
		if ( activePlayerSeat != playerNum )
			playerSeats[playerNum].setStatusText(state);
	}
	
	public void createPlayerBoxes(int numPlayers){
		playerBoxes  = new PlayerBox[numPlayers];
	}
	
	public void setActivePlayer(String nick) {
		int seatNum = -1;
		if ( table == null || table.players == null ) {
			activePlayerSeat = -1;
			return;
		}
		for ( int i = 0; i < table.players.length; i++ ) {
			if ( table.players[i].sitting == true && nick.equals(table.players[i].nickname) ) { 
				activePlayerSeat = i;
				return;
			}
		}
		activePlayerSeat = -1;
		return;
	}
	
	public int getFirstOpenSeatNum() {
		int seatNum = -1;
		if ( table == null || table.players == null )
			return seatNum;
		for ( int i = 0; i < table.players.length; i++ ) {
			if ( table.players[i].sitting == false )
				return i;
		}
		return seatNum;
	}
	
	public void setPlayerBox(int playerNum, String nickname, String amt) {
		table.players[playerNum].nickname = nickname;
		table.players[playerNum].sitting  = true;
		playerBoxes[playerNum] = new PlayerBox(playerNum, nickname, amt, numPlayers, control);
	}
	
	public void removePlayerBox(int playerNum) {
		table.players[playerNum].nickname = "";
		table.players[playerNum].sitting  = false;
		if ( playerBoxes[playerNum] != null ) {
			playerBoxes[playerNum].delete();
			playerBoxes[playerNum] = null;
		}
	}
	
	public void setTakeSeat(int playerNum, int numPlayers) {
		takeSeat[playerNum] = new TakeSeat(playerNum, numPlayers, control);
	}
	
	public void removeTakeSeat(int playerNum) {
		if ( takeSeat[playerNum] != null ) {
			takeSeat[playerNum].delete();
			takeSeat[playerNum] = null;
		}
	}

	public void setPlayerAmount(int playerNum, String amount) {
		table.players[playerNum].amount = amount;
		playerBoxes[playerNum].setAmount(amount);
	}
	
	public void createDealerMarker(int location){
		dealerMarker = new DealerMarker(location, numPlayers, control);
	}


	public void moveDealer(int dealerLoc) {
		table.dealer = dealerLoc;
		dealerMarker.moveDealer(dealerLoc);
	}
	
	public void setPots(double dpots[]) {
		table.pots  = dpots;
		if ( pots != null) {
			//for (int i = 0; i < pots.length; i++) 
				//ToolTipHelper.clearToolTip(pots[i]);
		}
		deletePots();
		pots = new Pot[dpots.length];
		for (int i = 0; i < dpots.length; i++) {
			pots[i] = new Pot(i, dpots[i], control);
		}
	}
	
	private void deletePots() {
		if ( pots != null ) {
			for (Pot pot: pots) {
				if (pot != null)
					pot.delete();
			}
		}
	}
	
	public void clearPot(int potNum) {
		Pot oldPot = pots[potNum];
		if (oldPot != null)
			oldPot.delete();
		//ToolTipHelper.clearToolTip(oldPot);
		pots[potNum] = null;
	}
	
	public void clearPots() {
		table.pots  = new double[0];
		//if (pots != null) {
			//for(int i = 0; i < pots.length; i++) 
				//ToolTipHelper.clearToolTip(pots[i]);
		//}
		deletePots();
		pots = null;	
	}
	
    public void createCommonCards(int numActive) {
		if ( commonCards != null )
			commonCards.delete();
		commonCards = new CommonCards(table.cards, numActive, control);
    }
    
    public void clearCommonCards(){
		table.cards = new int[0];
		if ( commonCards != null )
			commonCards.delete();
		commonCards = null;
    }
    
    public void setNumActiveCommonCards(int num) {
    	//if (LOG.isDebugEnabled())
			//LOG.debug("ACTIVE COMMONCARDS:"+num);
    	if (commonCards != null)
    		commonCards.setNumActive(num);
    }
    
    public void createPlayersCards( int numPlayers ) {
		playersCards = new PlayersCards[numPlayers];
    }
    
    public void createPlayerCards( int playerNum, int cards[]) {
    	if ( playersCards[playerNum] != null )
    		playersCards[playerNum].delete();
		playersCards[playerNum] = new PlayersCards(playerNum, cards, numPlayers, control);
    }
    
    public void clearPlayersCards(){
		playersCards = null;
    }
    
    public void clearPlayerCards(int playerNum) {
    	if ( playersCards[playerNum] != null )
    		playersCards[playerNum].delete();
    	playersCards[playerNum] = null;
    }
    
    public void setPlayerCardState(int playerNum) {
		table.players[playerNum].cards = new int[0];
		table.players[playerNum].showCards = false;
    }
    
	public void setCardInMotion(SmallCard card) {
		cardInMotion   = card;
	}
	
	public void moveCardInMotion(int timeDelta) {
		cardInMotion.move(timeDelta);
	}
	
	public void clearCardInMotion() {
		cardInMotion  = null;
	}
	
	private void deletePotsInMotion(PartialPot[] partialPots) {
		if ( partialPots != null ) {
			for (PartialPot pot: partialPots) {
				if (pot != null)
					pot.delete();
			}
		}
	}
    
	public void setPotsInMotion(int potNum, PartialPot[] partialPots) {
		//if (potsInMotion != null) {
			//throw new RuntimeException("bla");
		//}
		deletePotsInMotion(potsInMotion[potNum]);
		potsInMotion[potNum]  = partialPots;
	}
	
	public void clearPotsInMotion(int potNum) {
		//for (int i = 0; i < potsInMotion[potNum].length; i++) {
			//ToolTipHelper.clearToolTip(potsInMotion[potNum][i]);
		//}
		deletePotsInMotion(potsInMotion[potNum]);
		potsInMotion[potNum]  = null;
	}
	
	public void createSmallCards(int numPlayers) {
		smallCards   = new SmallCards[numPlayers];
	}
	
	public void setSmallCards(int playerNum, int numCards) {
		if ( smallCards[playerNum] != null )
			smallCards[playerNum].delete();
		smallCards[playerNum] = new SmallCards(playerNum, numCards, numPlayers, control);
	}
	
	public void clearSmallCards(int playerNum){
		if ( smallCards[playerNum] != null )
			smallCards[playerNum].delete();
		smallCards[playerNum] = null;
	}
	
	public void createBets(int numPlayers) {
		bets = new Bet[numPlayers];
	}
	
	private void deletePlayerBets(int playerNum) {
		if ( bets[playerNum] != null ) {
			bets[playerNum].delete();
		}
	}
	public void setPlayerBets(int playerNum, double dbets[]) {
		table.players[playerNum].bets = dbets;
		deletePlayerBets(playerNum);
		//ToolTipHelper.clearToolTip(bets[playerNum]);
		if (dbets.length > 0) {
			//if (LOG.isDebugEnabled())
				//LOG.debug("setting bet for :"+playerNum+" dbets:"+reportDoubles(dbets));
			bets[playerNum] = new Bet(playerNum, dbets, numPlayers, control);
		} else {
			bets[playerNum] = null;
		}
	}
	private String reportDoubles(double val[]) {
		String res = "";
		for (int i = 0; i < val.length; i++) {
			res += ""+i+": AMT:"+val[i]+ "  ";
		}
		return res;
	}
	
	public boolean noActiveBet() {
		return (bets == null || bets.length == 0);
	}
	
	public int setupBetCollection(int potNum) {
		int endTime = 0;
		for (int i = 0; i < bets.length; i++) {
			if ( bets[i] == null )
				continue;
			bets[i].collect(potNum, Geometry.BET_SPEED);
			int dTime = bets[i].getTimeToDeliver();
			endTime = Math.max(endTime, dTime);
		}
		return endTime;
	}
	
	public void moveBets(int timeDelta) {
		for (int i = 0; i < bets.length; i++) {
			if ( bets[i] == null )
				continue;
			bets[i].move(timeDelta);
		}
	}
	
	public void clearBets() {
		for (int i = 0; i < bets.length; i++) {
			clearBet(i);
			//ToolTipHelper.clearToolTip(bets[i]);
			//bets[i] = null;
		}
	}
	
	public void clearBet( int playerNum ) {
		table.players[playerNum].bets = new double[0];
		//ToolTipHelper.clearToolTip(bets[playerNum]);
		deletePlayerBets(playerNum);
		bets[playerNum] = null;
		//if (LOG.isDebugEnabled())
			//LOG.debug("Clearing bet for: "+playerNum);
	}
	
	public void setPotTotal( double dpotTotal ){
		if ( potTotal != null ) {
			potTotal.setAmount(dpotTotal);
		} else {
			potTotal = new PotTotal(dpotTotal, control);
		}
	}
	
	public void createTableState(int numPlayers) {
		this.numPlayers = numPlayers;
		table = new TableState(numPlayers);
	}
	
	public void setCommonCardsState(CardCollection cards) {
		table.cards = makeVisualCards(cards);
	}
	
	public int getNumCommonCards() {
		if (table.cards == null)  
			return 0;
		
		return table.cards.length;
	}
	
	public void setPlayerCards(int playerNum, CardCollection cards) {
		table.players[playerNum].cards = makeVisualCards(cards);
		setVisualCardsAppropriately(playerNum);
	}
	
	private int[] makeVisualCards(CardCollection cards) {
		int vcards[] = new int[cards.size()];

		Card card;
		int  id;
		int  suit = -1;
		for (int i = 0; i < cards.size(); i++) {
			card = cards.get(i);
			// Map suit to visual representation
			switch(card.getSuit()) {
			  case Suit.HEARTS:
				suit = 3;
				break;
			  case Suit.DIAMONDS:
				suit = 2;
				break;
			  case Suit.CLUBS:
				suit = 1;
				break;
			  case Suit.SPADES:
				suit = 0;
				break;
			} 
			id   = card.getRank() + suit*13;
			vcards[i] = id;
		}
		return vcards;
	}
	
	public void setAllShowCards(boolean showCards) {
		for (int i = 0; i < table.players.length; i++) {
			setPlayerShowCards(i,showCards);
		}
	}

	public void setPlayerShowCards(int playerNum, boolean showCards) {
		table.players[playerNum].showCards = showCards;
		setVisualCardsAppropriately(playerNum);
	}
	
	private void setVisualCardsAppropriately(int playerNum) {
		boolean showCards = table.players[playerNum].showCards;
		if ( showCards || playerNum == activePlayerSeat ) {
			//playersCards[playerNum] = 
			  //new PlayersCards(playerNum, table.players[playerNum].cards);
			createPlayerCards(playerNum, table.players[playerNum].cards);
			clearSmallCards(playerNum); //smallCards[playerNum] = null;
		} else {
			//playersCards[playerNum] = null;
			clearPlayerCards(playerNum);
			if ( table.players[playerNum].cards != null &&
			     table.players[playerNum].cards.length > 0 ) {
				setSmallCards(playerNum, table.players[playerNum].cards.length);
				//smallCards[playerNum] = 
				  //new SmallCards(playerNum, table.players[playerNum].cards.length);
			} else {
				clearSmallCards(playerNum); //smallCards[playerNum] = null;
			}
		}
	}
	
	public int getNumPlayers() {
		return table.players.length;
	}

	public void setTableActive() {
		table.active = true;
	}
	
	public void setCardAnimation(TableAnimation cardAnimation) {
		//if (LOG.isDebugEnabled())
			//LOG.debug("setCardAnimation:"+cardAnimation);
		this.cardAnimation = cardAnimation;
	}

	public void addPotsAnimation(TableAnimation animation) {
		synchronized(potsAnimation) {
			ArrayList<TableAnimation> npotsAnimation = clonePotsAnimation();
		    npotsAnimation.add(animation);
		    potsAnimation = npotsAnimation;
		}
	}
	
	public void removePotsAnimation(TableAnimation animation) {
		synchronized(potsAnimation) {
			ArrayList<TableAnimation> npotsAnimation = clonePotsAnimation();
			npotsAnimation.remove(animation);
		    potsAnimation = npotsAnimation;
		}
	}
	
	private ArrayList<TableAnimation> clonePotsAnimation() {
		ArrayList<TableAnimation> npotsAnimation = new ArrayList<TableAnimation>();
		for (TableAnimation pa : potsAnimation) 
			npotsAnimation.add(pa);
		return npotsAnimation;
	}
	
	public void setBetsAnimation(TableAnimation betsAnimation) {
		this.betsAnimation = betsAnimation;
	}
	
	
	public void takeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
			  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit, 
			  double minNonLimitRaise, ActionCallback ac) {
		actionPanel.takeAction(bettingMode, nick, playerBet, toCall, callable, bigBlind, maxRaiseIfNoLimit, raiseIfLimit, 
			maxRaiseIfPotLimit, minNonLimitRaise, ac);
	}
	
	public void actionTimeout() {
		actionPanel.actionTimeout();
	}
	
	/*
	GWTCanvas canvas = null;
    public void render(GWTCanvas g2) {
    	
    	if (true) return;

    	if (g2 != null) 
    		canvas = g2;
    	else 
    		g2 = canvas;
		//TableResources.init(null);
		//doState();

		if (TableResources.tableImg == null) {
			//repaint(500);
			return;
		}	
		//printTime("render start");
		GWTTable.clearCanvas();
        //g2.drawImage(TableResources.tableImg, 0, 0);
		//printTime("tableImg");

		if ( !table.active ) return;

		PlayerState player;
		for (int i = 0; i < table.players.length; i++) {
			if ( playerSeats != null && playerSeats[i] != null )
			playerSeats[i].display(g2);

			player = table.players[i];
			if (player.sitting) {  // TODO: this looks wrong
			    if ( smallCards != null && smallCards[i] != null )
				    smallCards[i].display(g2);
			    //if ( playerBoxes != null && playerBoxes[i] != null )
				    //playerBoxes[i].display(g2);
				
				if (player.showCards) {
					if ( playersCards != null && playersCards[i] != null )
						playersCards[i].display(g2);
				}
			    if ( bets != null && bets[i] != null )
				    bets[i].display(g2);
			}
			//printTime("for "+i);
		}
		//printTime("forloop");

		if ( dealerMarker != null )
		    dealerMarker.display(g2);
		if ( commonCards != null )
		    commonCards.display(g2);
		if ( pots != null ) {
			for (int i = 0; i < pots.length; i++) {
			    if ( pots[i] != null )
				    pots[i].display(g2);
			}
		}
		//if ( potTotal != null )
		    //potTotal.display(g2);
		//if ( cardInMotion != null )
		    //cardInMotion.display(g2);
		//printTime("cardsInMotion");
		for (int j = 0; j < 10; j++) {
			if ( potsInMotion[j] != null ) {
				for (int i = 0; i < potsInMotion[j].length; i++) {
					potsInMotion[j][i].display(g2);
				}
			}
		}
		//printTime("end render");
	}*/
    
    public void printState() {
    	System.out.println("HoldemTable state:");
		PlayerState player;
		for (int i = 0; i < table.players.length; i++) {
			if ( playerSeats != null && playerSeats[i] != null )
			System.out.println("playerSeat: "+playerSeats[i]);

			player = table.players[i];
			if (player.sitting) {  // TODO: this looks wrong
			    if ( smallCards != null && smallCards[i] != null )
				    System.out.println("smallCards: "+smallCards[i]);
			    if ( playerBoxes != null && playerBoxes[i] != null )
				    System.out.println("playerBoxes: "+playerBoxes[i]);
				
				if (player.showCards) {
					if ( playersCards != null && playersCards[i] != null )
						System.out.println("playerCards: "+ playersCards[i]);
				}
			    if ( bets != null && bets[i] != null )
			    	System.out.println("bets: "+bets[i]);
			}
		}
		if ( dealerMarker != null )
			System.out.println("dealerMarker: "+dealerMarker);
		if ( commonCards != null )
			System.out.println("commonCards: "+commonCards);
		if ( pots != null ) {
			for (int i = 0; i < pots.length; i++) {
			    if ( pots[i] != null )
			    	System.out.println("pots: "+pots[i]);
			}
		}
		if ( potTotal != null )
			System.out.println("potTotal: "+potTotal);
		if ( cardInMotion != null )
			System.out.println("cardInMotion: "+cardInMotion);
		for (int j = 0; j < 10; j++) {
			if ( potsInMotion[j] != null ) {
				for (int i = 0; i < potsInMotion[j].length; i++) {
					System.out.println("potsInMotion: "+potsInMotion[j][i]);
				}
			}
		}
    }


    /*public boolean imageUpdate(Image img, int infoflags,
                int x, int y, int width, int height)
    {
        if ( (infoflags & ALLBITS) != 0)
            repaint();
        if ( (infoflags & FRAMEBITS) != 0)
            repaint();
        return isShowing();
    }*/



	//----------------------------------------------------------------


    // TODO: I don't think this should be here.
	public double getPotTotal() {
		if ( table.pots == null ) return 0.0;

		double pots = 0.0;

		for (int i = 0; i < table.pots.length; i++) {
			pots += table.pots[i];
		}
		PlayerState player;
		for (int i = 0; i < table.players.length; i++) {
			player = table.players[i];
			if ( player.bets == null ) continue;
			if (player.sitting) {
				for ( int j = 0; j < player.bets.length; j++ ) {
					pots += player.bets[j];
				}
			}
		}
		return pots;
	}

	
	/*public class ClearPlayerSeatStatus extends TimerTask {
		public void run() {
			long now = System.currentTimeMillis();
			clearPlayerSeatStatus(now);
			//for (int i = 0; i < playerSeats.length; i++) {
				//playerSeats[i].checkToClearStatusString(now);
			//}
		}
	}*/
	
	/*
	public class ShowToolTip extends TimerTask {
		public void run() {
			if (tipProcessed)
				return;
			long now = System.currentTimeMillis();
			if ( now - mouseMoveTime > TIP_START_SHOWING_WAIT ) {
				if (tipShowing)
				    hideTipWindow();
				addToolTip(mouseX, mouseY);
				tipProcessed = true;
			}
		}
	}
	
	public class ClearToolTip extends TimerTask {
		public void run() {
			hideTipWindow();
		}
	}
	
	
    public class ToolTipAdapter extends MouseAdapter {
        public void mouseExited(MouseEvent event) {
        	hideTipWindow();
        }
    }
    
    private Popup tipWindow;
    private ToolTipAdapter ttadapter = new ToolTipAdapter();
    private boolean tipShowing = false;
    private boolean tipProcessed = false;
    private static long TIP_START_SHOWING_WAIT = 750l;
    private Window window = null;
    public void addToolTip(int x, int y) {
    	// Make sure there is something to display
    	String displayString = ToolTipHelper.mouseHoverEvent(x, y);
    	if (displayString == null) return; 
    	
    	// Fire up a tool tip
    	JToolTip tip;
    	PopupFactory  popupFactory = PopupFactory.getSharedInstance();
        tip = createToolTip();
        //tip.setTipText("<html> sometext<br>line2<br>line3</html>");
        tip.setTipText(displayString);
        //tip.setTipText("<html> sometext</html>");
        Dimension d;
        d = tip.getSize();
        int xoff = (int)d.getWidth()+6;
        int yoff = (int)d.getHeight()+12;
        //Point p = geometry.getPotLoc(1);
        tipWindow = popupFactory.getPopup(this, tip, x+xoff,y+yoff);
        synchronized(tipWindow) {
        	tipWindow.show();
        	tipShowing = true;
        	localTimer.schedule(new ClearToolTip(), 7000l);
        
        	Window componentWindow = SwingUtilities.windowForComponent(this);

            window = SwingUtilities.windowForComponent(tip);
        	if (window != null && window != componentWindow) {
        		window.addMouseListener(ttadapter);
        	} else {
        		window = null;
        	}
        }
    }
    
    private void hideTipWindow() {
    	if (!tipShowing)
    		return;
    	
    	synchronized(tipWindow) {
    		if (window != null)
    			window.removeMouseListener(ttadapter);
			tipWindow.hide();
			tipWindow = null;
			tipShowing = false;
		}
    }
    
    private long mouseMoveTime = 0l;
    private int  mouseX = 0;
    private int  mouseY = 0;
    public void mouseMoved(MouseEvent e) {
    	mouseX = e.getX();
    	mouseY = e.getY();
    	mouseMoveTime = System.currentTimeMillis();
    	tipProcessed = false;
        //System.out.println("Mouse moved "+ e);
     }

     public void mouseDragged(MouseEvent e) {
    	 //System.out.println("Mouse dragged"+ e);
     }
     */
	

	
	private long lastPrintTime = 0;
	private void printTime(String marker) {
		long after = System.currentTimeMillis();
		System.out.println(marker + ": "+(after-lastPrintTime));
		lastPrintTime = after;
	}

	
	boolean dirty = false;
	long lastPaintTime = 0;
	int cc = 0;
	public void repaint() {
		/*
		dirty = true;
		//System.out.println("repaint");

		if ( canvas != null && dirty) {
			long newPaintTime = System.currentTimeMillis();
			if ( (newPaintTime - lastPaintTime) > 300 ) {
				//if (cc++ > 22 && cc % 2 == 0 ) return;

				render(null);
				long after = System.currentTimeMillis();

				System.out.println("render "+(newPaintTime - lastPaintTime)+ " - "+(after - newPaintTime));
				lastPaintTime = after;
			}
		}*/
	}
}




