package com.cardfight.client;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.widgetideas.graphics.client.Color;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
//import com.google.gwt.widgetideas.graphics.client.ImageHandle;
//import com.google.gwt.widgetideas.graphics.client.ImageLoader;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Timer;
import com.cardfight.client.poker.event.VFEvent;
import com.cardfight.client.poker.event.VFChatMsg;
import com.cardfight.client.poker.event.VFTakeSeat;
import com.cardfight.client.poker.event.VFLogMsg;
import com.cardfight.client.poker.event.VFRedirect;
import com.cardfight.client.poker.event.VFUserid;
import com.cardfight.client.poker.event.VFQueueName;
import com.cardfight.client.poker.event.VFBetResponse;
import com.cardfight.client.poker.event.VFSetPlayerCards;
import com.cardfight.client.poker.event.VFSetPlayer;
import com.cardfight.client.poker.event.VFInitRound;

import com.cardfight.client.table.*;
//import com.cardfight.client.poker.HoldemSimulation;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.TabPanel;
//import com.google.gwt.user.client.ui.ClickListener;
//import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTTable implements EntryPoint, TopControl, KeyUpHandler, KeyPressHandler, HistoryListener, ClickListener {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

  private AbsolutePanel   aPanel;
  private int             currentNumPlayers = 0;
  private HorizontalPanel playerNum[];
  private VerticalPanel playerNumParent[];
  private HashMap <String, Image>      dealtImages = new HashMap <String, Image>();
  //private static GWTCanvas    canvas   = null;
  private Timer timer;
  private HoldemTable  htable   = null;
  private MyHandler handler     = null;
  private long waitForTime = 0;
  private TopControl control = null;
  private static final int STEP_WAIT_TIME = 50;
  private static int inCount = 0;
  private static int inCountMax = 0;
  private Image activeImage = null;
  
  private HorizontalPanel topLine;
  private Button  loginButton;
  private Button  sitinButton;
  private Button  standUpButton;
  private TextBox userField;
  

  //private TestVFImpl tvfi = null;

  /*private int mode = 0;
  private ArrayList<VFEvent> evtLoop;
  private int evtNum = 0;
  private long priorTime = 0;
  */
  
  public void onKeyPress(KeyPressEvent event) {
	  int amt = 1;
	  if ( event.isShiftKeyDown() )
		  amt = 10;
	  char c = event.getCharCode();
	  c = Character.toLowerCase(c);
	  if ( c == 'h' ) 
		  moveActiveImage(-amt,0);
	  else if ( c == 'j' )
		  moveActiveImage(0,-amt);
	  else if ( c == 'k' )
		  moveActiveImage(0,amt);
	  else if ( c == 'l' )
		  moveActiveImage(amt,0);
  }
  
  public void onClick(Widget w) {
	  if ( w == loginButton ) {
		  handler.setUserid(userField.getText());
		  handler.addChat(userField.getText(), "Hi");
	  } else if ( w == sitinButton ) {
		  takeSeat(htable.getFirstOpenSeatNum());
	  } else if ( w == standUpButton ) {
		  handler.takeSeat(-1);
		  activateSitIn();
	  }
  }
  
  public void takeSeat( int seatNum ) {
	  handler.takeSeat(seatNum);
  }
  
  private void moveActiveImage(int x, int y) {
	  if ( activeImage != null ) {
		  int ix = aPanel.getWidgetLeft(activeImage);
		  int iy = aPanel.getWidgetTop(activeImage);
          String msg = ""+activeImage.getUrl()+ " x: "+ix+" y: "+iy;
          logServer(msg);
		  chatString(msg);
		  aPanel.remove(activeImage);
		  aPanel.add(activeImage,ix+x,iy+y);
	  }
  }
  public void addWidget(Widget w, int x, int y) {
	  //System.out.println("before xy"+x+","+y);
	  aPanel.add(w, x, y);
	  
	  if ( w instanceof Image ) {
		  final Image image = (Image) w;
		  final int ix = x;
		  final int iy = y;
		  image.addClickHandler(new ClickHandler() {
		      public void onClick(ClickEvent event) {
      			  activeImage = image;
      			  moveActiveImage(0,0);
		      }
		    });
	  } 
	  //System.out.println("after xy"+x+","+y);
	  statcontrol = control;
  }
  
  public void removeWidget(Widget w) {
	  aPanel.remove(w);
  }
  
  public void waitUntil(long t) {
	  //System.out.println("waitUntil :"+t);
	  long waitTime = System.currentTimeMillis() + t;
	  if ( waitTime > waitForTime )
		  waitForTime = waitTime;
  }
  
  private static TopControl statcontrol = null;
  public static void logStatic(String log) {
	  statcontrol.logLocally(log);
  }
  public static void logStaticServer(String log) {
	  statcontrol.logServer(log);
  }
  
  public void logServer(String log) {
  	  handler.addLogMsg(log);
  }
  
  private TextArea ta   = null;
  private int taDelay   = 0;
  private String tarr[] = new String[7];
  private int    tloc   = 0;
  public void logLocally(String log) {
	  //System.out.println(log); if (true) return;
	  
	  //String text = "";
	  if (ta == null ) {
		  ta = new TextArea();
		  ta.setCharacterWidth(80);
		  ta.setVisibleLines(7);
	  } else {
		  //text = ta.getText(); 
	  }
	  if (taDelay++ == 440)
		  addWidget(ta, 0, /*410*/0);
	  
	  tarr[tloc] = log;
	  tloc = (tloc + 1) % 7;
	  String nstr = "";
	  for( int i = 0; i < 7; i++ ) {
		  if (tarr[i] == null ) break;
		  nstr += tarr[i] + "\n";
	  }
	  nstr = ta.getText() + "\n"+ log;

	  ta.setText(nstr);
  }
  
  private TextArea      chatBox = null;
  private TextBox       chatField;
  //private TextBox       nameField;
  private DecoratedTabPanel      tabPanel;
  private VerticalPanel chatPanel = null;
  
  public void chatString(String chatMsg) {
	  String text = "";
	  if (chatBox == null) {
		  tabPanel = new DecoratedTabPanel();
		  chatPanel = new VerticalPanel();
		  HorizontalPanel hp = new HorizontalPanel();
		  chatField = new TextBox();
		  chatField.setVisibleLength(55);
		  //nameField = new TextBox();
		  //nameField.setVisibleLength(6);
		  //nameField.setText("unknown");
		  hp.add(chatField);
		  //hp.add(new Label("   Name:"));
		  //hp.add(nameField);
		  chatPanel.add(hp);
		  chatBox = new TextArea();
		  chatBox.setCharacterWidth(46);
		  chatBox.setVisibleLines(4);
		  chatPanel.add(chatBox);
		  chatPanel.setStyleName("chatpanel");
		  chatBox.setStyleName("chatbox");
		  tabPanel.add(chatPanel, "Chat");
		  addWidget(tabPanel, 0, 410);
		  tabPanel.selectTab(0);
		  //chatField.addClickListener(this);
		  chatField.addKeyUpHandler(this);
		  chatField.addKeyPressHandler(this);
		  chatBox.setReadOnly(true);

	  } else {
		  text = chatBox.getText(); 
	  }
	  if (text.length() > 0)
		  chatMsg += "\n";
	  String fullString = chatMsg+text;
	  chatBox.setText(fullString);
	  //chatBox.setCursorPos(fullString.length()-1);
	  //chatBox.setSelectionRange(fullString.length()-2, 2);
  }

 
  
  /*public static void clearCanvas() {
	 canvas.clear();
  }*/
  
  public int stepCount = 0;
  private int widgetCount = 0;
  private Label clabel = null;
  //private Label totlabel = null;
  //private Label memlabel = null;
  private int stepInc = 0;
  private int    inSendCount = 0;
  public void step() {
	  
	  if ( handler != null)
		  handler.delayedAction();
	  //stepInc++;
	  //logLocally("step begin ...\t"+stepInc+"\t"+inSendCount+"\t"+handler.qSize());
	  htable.step();
	  handler.checkForSend();

	  // -------- TESTING CODE
	  /*if ( mode < 2 ) {
		  handler.checkForSend();
		  priorTime = System.currentTimeMillis();
	  } else {
		  long tdelta = System.currentTimeMillis() - priorTime;
		  VFEvent evt;
		  long tbase = 0;

		  if ( evtNum > 0 ) { 
			  evt = evtLoop.get(evtNum-1);
			  tbase = evt.time;
		  }
		  for ( ; evtNum < evtLoop.size(); evtNum++ ) {
			  evt = evtLoop.get(evtNum);
			  if ( evtNum == 0 || (evt.time-tbase) < tdelta ) {
				  handler.addPendingEvent(evt);
			  } else {
				  break;
			  }
		  }
	  }
	  if ( evtLoop != null && evtNum >= evtLoop.size() ) {
		  evtNum = 0;
		  priorTime = System.currentTimeMillis();
	  }*/
	  // -------- TESTING CODE
	  
      handler.processEvents();
      stepCount++;
	  /*if ( (stepCount % 20) == 0 ) {
		  if ( clabel == null ) {
			  clabel = new Label();
			  clabel.addStyleName("playerstatus");
			  //totlabel = new Label();
			  //totlabel.addStyleName("playerstatus");
			  //memlabel = new Label();
			  //memlabel.addStyleName("playerstatus");
			  SimplePanel box = new SimplePanel();
			  box = new SimplePanel();
		      box.addStyleName("pottotal");
		      box.add(clabel);
		      //box.add(memlabel);
		      //box.add(totlabel);
			  addWidget(box, 640, 490);
		  }
		  int widgetCount = aPanel.getWidgetCount();
		  clabel.setText("widgetCount = "+widgetCount+"");
		  //long heapSize = 0;
		  //long heapFreeSize = 0;
		  //totlabel.setText("totMem = "+heapSize+"");
		  //memlabel.setText("freeMem = "+heapFreeSize+"");
	  }*/
	  if ( stepCount >= 400 ) {
		  stepCount = 0;
		  //System.gc();
	  }
	  //logLocally("step end ***\t"+ stepInc+"\t"+inSendCount+"\t"+handler.qSize());
	  //stepInc--;
  }
  
  /*
  public void onClick(Widget sender) {
      if ( sender == chatField ) {
    	  sendChat();
      }
  }*/
  
  public void onKeyUp(KeyUpEvent event) {
	  if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		  sendChat();
	  }
  }
  
  private void sendChat() {
	  String user = userField.getText();
	  if ( "".equals(user) )
		  user = "guest";
	  handler.addChat(user, chatField.getText());
	  chatField.setText("");
  }
  
  public void addBetResponse(double bet) {
	  handler.addBetResponse(bet);
  }
  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    /*final Button sendButton = new Button("Send");
    final TextBox nameField = new TextBox();
    nameField.setText("GWT User");

    // We can add style names to widgets
    sendButton.addStyleName("sendButton");

    // Add the nameField and sendButton to the RootPanel
    // Use RootPanel.get() to get the entire body element
    RootPanel.get("nameFieldContainer").add(nameField);
    RootPanel.get("sendButtonContainer").add(sendButton);*/

    //-------------------------------------
    aPanel = new AbsolutePanel();
    aPanel.setWidth("100%");

    //aPanel.add(flow, 160,300);

    
    aPanel.setSize("800px", "550px");
    aPanel.setStyleName("tableImage");
    control = this;
    
    final Runnable canvasCallback = new Runnable() {
    	
    	private int count = 0;
        public void run() {
        	if ( htable == null ) {
        		//SimplePanel spanel = new SimplePanel();
        		//canvas = new GWTCanvas(800,550);
        		//spanel.add(canvas);
        		//aPanel.add(canvas, 0, 0);
        		htable = new HoldemTable(control);
                //htable.render(canvas);
        		//HoldemSimulation.initGame(true, htable.getVisualFeedback());
        		//HoldemSimulation.initGame(true, htable.getVisualFeedback());
    	        handler.sendNameToServer();  
    	        addEllipse();
        	}
        	/*
            canvas.drawImage(TableResources.tableImg, 0, 0);
            canvas.drawImage(TableResources.circleImg, 10, 10);
            canvas.drawImage(TableResources.dealerImg, 10, 100);
            //canvas.drawImage(fiveChip, 100, 100);
            //canvas.drawImage(fiveChip, 100, 100);
            displayChips(100,100, 999.0d, Geometry.BACKWARD);
            
            canvas.drawImage(TableResources.circleImg, 710, 410);
            canvas.drawImage(TableResources.dealerImg, 710, 400);
            displayChips(450,500, 999.0d, Geometry.BACKWARD);*/
        	step(); 
        	//if ( count++ % 20 == 0 )
        		//HoldemSimulation.step();
        }
    };
    TableResources.init(null);
    System.out.println("loaded="+TableResources.loaded);
    timer = new Timer() {
    	private int count = 0;
    	private boolean notDone = true;

        @Override
        public void run() {
        	inCount++;
        	if ( inCount > inCountMax ) {
        		inCountMax = inCount;
        		logStaticServer("inCount : "+inCount);
        	}
        	//if ( TableResources.loaded == false ) schedule(100);
            //System.out.println(count+" loaded="+TableResources.loaded);
            //count++;
        	//if ( TableResources.loaded && notDone ) {
        		//canvasCallback.run();
        		//notDone = false;
        	//}
        	//if ( count >=  20 ) {
        		
        		canvasCallback.run();
        	    //if ( handler != null && !handler.isCommError() ) 
        	    	//schedule(STEP_WAIT_TIME);
        		/*if ( ! smallCardStep() ) {
                	schedule(1);
        		}*/
        	//} else {
        		//schedule(50);
        	//}
        	inCount--;
        }

      };
      //timer.schedule(10);
      timer.scheduleRepeating(STEP_WAIT_TIME);

    
    /*
    String[] imageUrls = new String[] {"/img/circle.gif", "/img/D.gif", "/img/5.gif"};

    ImageLoader.loadImages(imageUrls, new ImageLoader.CallBack() {
        public void onImagesLoaded(ImageElement[] imageHandles) {
          // Drawing code involving images goes here
          img = imageHandles[0];
          ImageElement dealer = imageHandles[1];
          fiveChip = imageHandles[2];

          
          //timer.schedule(10);


        }
      });*/

      //SliderBar slider = new SliderBar(0.0, 100.0);
      //slider.setStepSize(5.0);
      //slider.setCurrentValue(50.0);
      //addWidget(slider, 200, 200);
    

    //canvas.drawImage(TableResources.circleImg, 5, 5 );
    //canvas.rect(10,10,100, 100);
    //chatString("Starting game ...");

    VerticalPanel   topPanel   = new VerticalPanel();
    topLine    = new HorizontalPanel();
    userField  = new TextBox();
    Label           userValue  = new Label("");
    userField.setVisibleLength(10);
    loginButton  = new Button("Login");
    Button          logoutButton = new Button("Logout");
    sitinButton  = new Button("Sit Down");
    standUpButton  = new Button("Stand Up");
    Button          changeButton = new Button("Change Table");
    Label           loginLabel   = new Label("Userid:");
    Label           tableLabel   = new Label("Table:");

    hlabel = new Label();
    SimplePanel spanel = new SimplePanel();
    spanel.add(hlabel);
    hlabel.setStyleName("topline");
    loginButton.setStyleName("topline");
    logoutButton.setStyleName("topline");
    sitinButton.setStyleName("topline");
    standUpButton.setStyleName("topline");
    changeButton.setStyleName("topline");
    loginLabel.setStyleName("topline");
    tableLabel.setStyleName("topline");
    userField.setStyleName("topline");
    userValue.setStyleName("topline");
    loginButton.addClickListener(this);
    sitinButton.addClickListener(this);
    standUpButton.addClickListener(this);


    

    topLine.add(tableLabel);
    topLine.add(spanel);
    topLine.add(hlabel);
    topLine.add(loginLabel);
    topLine.add(userField);
    topLine.add(loginButton);
    topLine.add(sitinButton);
    //topLine.add(standUpButton);
    
    topPanel.add(topLine);
    topPanel.add(aPanel);
    RootPanel.get("tableContainer").add(topPanel);
    //-------------------------------------
    
    /*
    // Focus the cursor on the name field when the app loads
    nameField.setFocus(true);
    nameField.selectAll();

    // Create the popup dialog box
    final DialogBox dialogBox = new DialogBox();
    dialogBox.setText("Remote Procedure Call");
    dialogBox.setAnimationEnabled(true);
    final Button closeButton = new Button("Close");
    // We can set the id of a widget by accessing its Element
    closeButton.getElement().setId("closeButton");
    final Label textToServerLabel = new Label();
    final HTML serverResponseLabel = new HTML();
    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.addStyleName("dialogVPanel");
    dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
    dialogVPanel.add(textToServerLabel);
    dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
    dialogVPanel.add(serverResponseLabel);
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    dialogVPanel.add(closeButton);
    dialogBox.setWidget(dialogVPanel);

    // Add a handler to close the DialogBox
    closeButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
        sendButton.setEnabled(true);
        sendButton.setFocus(true);
      }
    });
    */



    // Add a handler to send the name to the server
    handler = new MyHandler(this);
    //sendButton.addClickHandler(handler);
    //nameField.addKeyUpHandler(handler);
    
    //hlabel = new Label();
    //SimplePanel spanel = new SimplePanel();
    //spanel.add(hlabel);
    //spanel.setStyleName("bettext");
    //addWidget(spanel, 650, 0);
    initHistorySupport();
    addDebugSupport();

  }
  
  private void activateSitIn(){
	    topLine.add(sitinButton);
	    topLine.remove(standUpButton);
  }
  private void activateStandUp() {
	    topLine.remove(sitinButton);
	    topLine.add(standUpButton);
  }
  
  private Label hlabel;
  private static final String INIT_STATE="initstate";
  //private boolean hinit = false;
  //private String hstring = "";
  /*private void updateHash() {
	  String hash = Window.Location.getHash();
	  if (hash.equals(""))
		  return;
	  else if (hstring.equals(""))
		  hstring = hash;
	  else if ( !hstring.equals(hash) ){
		  Window.Location.reload();
	  }
	  hlabel.setText("\""+hash+"\"");
  }*/
  
  public void onHistoryChanged(String historyToken) {
	  //hinit = true;
	  String oldValue = hlabel.getText();
	  hlabel.setText(historyToken);
	  //logLocally("ov: "+oldValue+" ht: "+historyToken);
	  if ( !oldValue.equals(historyToken) && !oldValue.equals("") && 
		   !historyToken.equals(INIT_STATE) && !oldValue.equals(INIT_STATE) )
		  Window.Location.reload();
	  else if ( oldValue.equals(INIT_STATE) && !historyToken.equals(INIT_STATE) ) {
		  String href = Window.Location.getHref() + "#" +historyToken;
		  Window.Location.assign(href);
	  }
  }
  public void initHistorySupport() {
	  // add the MainPanel as a history listener
	  History.addHistoryListener(this);

	  // check to see if there are any tokens passed at startup via the browser's URI
	  String token = History.getToken();
	  if (token.length() == 0) {
	    onHistoryChanged(INIT_STATE);
	  }
	  else {
	    onHistoryChanged(token);
	  }
  }
  
  private String debugStr;
  private void addDebugSupport() {
	    final Button debugButton = new Button("Debug");
	    addWidget(debugButton, 0, 330);
	    debugButton.addClickHandler(new ClickHandler() {
		      public void onClick(ClickEvent event) {
		        //createDebugDialog(debugStr);
		    	handler.shutDown();
		      }
		    });
  }
  private void createDebugDialog(String txt) {
	    final DialogBox dialogBox = new DialogBox();
	    dialogBox.setText("Debug info");
	    dialogBox.setAnimationEnabled(true);
	    final Button closeButton = new Button("Close");
	    // We can set the id of a widget by accessing its Element
	    closeButton.getElement().setId("closeButton");
		TextArea  ta = new TextArea();
		ta.setCharacterWidth(80);
		ta.setVisibleLines(15);
		ta.setText(txt);
	    VerticalPanel dialogVPanel = new VerticalPanel();
	    dialogVPanel.addStyleName("dialogVPanel");
	    dialogVPanel.add(ta);
	    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
	    dialogVPanel.add(closeButton);
	    dialogBox.setWidget(dialogVPanel);
	    dialogBox.show();
	    
	    // Add a handler to close the DialogBox
	    closeButton.addClickHandler(new ClickHandler() {
	      public void onClick(ClickEvent event) {
	        dialogBox.hide();
	      }
	    });
  }
  
  private void addEllipse() {
	  int numPlayers = 2;
	  double a = 322;
	  double b = 161;
	  double ox = 402;
	  double oy = 216;
	  Image image;
	  /*for (int i = 0; i < 360; i++) {
		  double x = a * Math.cos(i) + ox;
		  double y = b * Math.sin(i) + oy;
		  image = new Image("/img/dot.gif");
		  addWidget(image, ((int)(x+0.5)), ((int)(y+0.5)));
	  }
	  image = new Image("/img/dot.gif");
	  addWidget(image, (int)ox, (int)oy);
	  for (int i = 0; i < numPlayers; i++) {
		  image = new Image("/img/dot.gif");
		  Point p = Geometry.instance().tangentialDealerLoc(i,numPlayers);
		  addWidget(image, p.x, p.y);
	  }*/
	  /*for (int i = 0; i < numPlayers; i++) {
		  image = new Image("/img/D.gif");
		  Point p = Geometry.instance().getDealerLoc(i,numPlayers);
		  addWidget(image, p.x, p.y);
	  }*/
	  for (int i = 0; i < 20; i++) {
		  double angle = Geometry.instance().trueAngleForDist(i,20);
	      angle = angle * Math.PI / 180;
		  double x = a * Math.cos(angle) + ox;
		  double y = b * Math.sin(angle) + oy;
		  image = new Image("/img/dot.gif");
		  addWidget(image, ((int)(x+0.5)), ((int)(y+0.5)));
	  }
	  for (int i = 1; i < 40; i+=2) {
		  double angle = Geometry.instance().trueAngleForDist(i,40);
	      angle = angle * Math.PI / 180;
		  double x = a *0.80 * Math.cos(angle) + ox;
		  double y = b *0.80* Math.sin(angle) + oy;
		  image = new Image("/img/dot.gif");
		  addWidget(image, ((int)(x+0.5)), ((int)(y+0.5)));
	  }
  }
  
  // Create a handler for the sendButton and nameField
  class MyHandler /*implements ClickHandler, KeyUpHandler*/ {
	  private TopControl control;
	  
	  public MyHandler(TopControl control) {
		  this.control = control;
	  }
	  
    /**
     * Fired when the user clicks on the sendButton.
     */
    /*public void onClick(ClickEvent event) {
      sendNameToServer();
    }*/

    /**
     * Fired when the user types in the nameField.
     */
    /*public void onKeyUp(KeyUpEvent event) {
      if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        sendNameToServer();
      }
    }*/
	  

    
    public void processEvents() {
    	if ( needToWait() ) 
    		return;
    	
        ArrayList<VFEvent> activeEvents = new ArrayList<VFEvent>();
        //synchronized(pendingEvents) {
        	while ( pendingEvents.size() > 0 ) 
        		activeEvents.add(pendingEvents.remove(0));
        //}
    	
    	while ( activeEvents.size() > 0 ) {
    		VFEvent result = activeEvents.remove(0);
    		debugStr = result.getEventName() + "\n" + debugStr;
    		if (debugStr.length() > 1000)
    			debugStr.substring(0, 999);
    		//if ( tvfi == null ) 
    			//tvfi = new TestVFImpl();
			//result.handleEvent(tvfi);
			result.handleEvent(htable.getVisualFeedback());

 			if ( delayedAction() )
				return;//step();

    		if ( needToWait() )
    			return;
    	}
    }
    
    private long lastTime = System.currentTimeMillis();
    public  boolean delayedAction() {
    	long thisTime = System.currentTimeMillis();
    	long delta = (thisTime - lastTime);
    	if ( delta > STEP_WAIT_TIME ){
    		lastTime = thisTime;
    		return true;
    	}
    	return false;
    }
    
    public int qSize() {
        //synchronized(pendingEvents) {
        	return pendingEvents.size();
        //}
    }
    
    public boolean isCommError() {
    	return errorStatus;
    }
    
    public void addPendingEvent( VFEvent evt ) {
        //synchronized(pendingEvents) {
        	pendingEvents.add(evt);
        //}
    }
    
    private ArrayList<VFEvent> pendingEvents = new ArrayList<VFEvent>();
    //private ArrayList<VFEvent> storedEvents = null;
    public boolean needToWait() {
    	if ( true ) return false;
      	if ( waitForTime != 0 ) {
      		long thisTime = System.currentTimeMillis();
      		if (thisTime >= waitForTime) {
      			waitForTime = 0;
      			return false;
      		} else
      			return true;
      	}
      	return false;
    }
    
    private long   lastSendTime = -1;
    private long   lastResponseTime = -1;
    private long   startTime2 = System.currentTimeMillis();
    private boolean errorStatus = false;
    private boolean notSentErrorStatus = true;
    private ArrayList<VFEvent> toSend = new ArrayList<VFEvent>();
    private String id = "";
    private String userid = "unknown";
    private String queueID = "";
    private int    lastMsgID = -1;
    private int    pendingSeat = -1;

    private String getID() {
    	if ( id.equals("") ) {
    		String hid = hlabel.getText();
    		if ( !hid.equals("") )
    			id = hid;
    		while ( id.equals("") )
    			id = (""+Random.nextInt());
    		//String sid = (""+id);
    		//if (sid.length() > 6)
    			//sid = sid.substring(sid.length()-6);
    		//nameField.setText(sid);
    	}
    	return id;
    }
    
    public void checkForSend() {
    	if ( errorStatus ) {
    		if ( notSentErrorStatus ) {
    			logLocally("errorStatus set - not scheduling send");
    			notSentErrorStatus = false;
    		}
          	return;
    	}
    	
    	if ( lastSendTime == -1 )
    		return;
    	
      	long   thisTime = System.currentTimeMillis();
      	if ( (thisTime - lastSendTime) > 400 && (thisTime - lastResponseTime) < 480 ) {
          	//logLocally("scheduling  \tt: "+((thisTime-startTime2)/1000)+"\tb1 " + "\ttdelta: "+(thisTime-lastSendTime));
      		//lastSendTime = thisTime;
      		sendNameToServer();
      	} else if ( (thisTime - lastResponseTime) > 5000 && lastResponseTime != -1 ) {
      		lastSendTime = thisTime;
          	//logLocally("scheduling  t: "+((thisTime-startTime2)/1000)+" b2 ");
      		sendNameToServer();
      	}
    }
    
    public void setUserid(String nick) {
		if ( !userid.equals(nick) ) {
			userid = nick;
			userField.setText(userid);
			VisualFeedbackImpl vfi =  htable.getVisualFeedback();
			vfi.setActivePlayer(nick);
			logServer("activePlayer : "+nick);
		}
    }
    
    public void addChat(String nick, String msg) {
		setUserid(nick);
    	
        synchronized(toSend) {
        	//logLocally("ChatResp: "+nick+": "+msg);
        	toSend.add(new VFChatMsg(nick,msg));
        }
    }
    
    public void takeSeat(int seatNum) {
        synchronized(toSend) {
        	//logLocally("ChatResp: "+nick+": "+msg);
        	pendingSeat = seatNum;
        	toSend.add(new VFTakeSeat(seatNum));
        }
    }
    
    public void addLogMsg(String msg) {
        synchronized(toSend) {
        	toSend.add(new VFLogMsg(msg));
        }
    }
    
    public void addBetResponse(double bet) {
        synchronized(toSend) {
        	//logLocally("BetResp: "+bet);
        	toSend.add(new VFBetResponse(bet));
        }
        sendNameToServer();
    }
    
    private boolean deactivate = false;
    public void shutDown() {
    	deactivate = true;
    }
    
int cc=0;
    
    /**
     * Send the name from the nameField to the server and wait for a response.
     */
    public void sendNameToServer() {
    	if ( deactivate ) return;
    	
      	long   thisTime = System.currentTimeMillis();
      	//logLocally("sendNameToServer\tt: "+((thisTime-startTime2)/1000)+"\ttdelta: "+(thisTime-lastSendTime));
      	lastSendTime = thisTime;
      //sendButton.setEnabled(false);
      //String textToServer = nameField.getText();
      //textToServerLabel.setText(textToServer);
      //serverResponseLabel.setText("");
      inSendCount++;

      /*synchronized(toSend)*/ {
      greetingService.greetServer(String.valueOf(getID()), userid, queueID, lastMsgID, toSend, new AsyncCallback<ArrayList<VFEvent>>() {
        public void onFailure(Throwable caught) {
        	errorStatus = true;
          // Show the RPC error message to the user
          //dialogBox.setText("Remote Procedure Call - Failure");
          //serverResponseLabel.addStyleName("serverResponseLabelError");
          //serverResponseLabel.setHTML(SERVER_ERROR);
          //dialogBox.center();
          //closeButton.setFocus(true);
          System.out.println("Server Failure: " +caught);
          control.logLocally("Server Failure: " +caught);
        }

        public void onSuccess(ArrayList<VFEvent> elist) {
          	long   thisTime = System.currentTimeMillis();
          	
          	//logLocally("onSuccess t: "+((thisTime-startTime2)/1000)+" tdelta: "+(thisTime-lastSendTime)+ " evt: "+elist.size());
          	lastResponseTime = thisTime;
        	for ( VFEvent result : elist ) {
        		/*
        		if ( mode == 0 && result instanceof VFInitRound ) {
        			mode = 1;
        			evtLoop = new ArrayList<VFEvent>();
        		}
        		if ( mode == 1 ) {
        			if ( evtLoop.size() > 0 && result instanceof VFInitRound ) {
                		System.out.println("Hit the end");
                		cc++;
                		if ( cc >= 2 ) {
        				    mode = 2;
        				    shutDown();
        				    return;
                		}
        			} 
        			if( cc < 2 ) {
        				result.time = thisTime;
        				evtLoop.add(result);
                		System.out.println("# "+evtLoop.size()+" cc: "+cc+" evt :"+result);
        			}
        		}*/
        		
        		int mid = result.getMsgID();
        		if ( mid >= 0 )
        			lastMsgID = mid;
        		if (result instanceof VFChatMsg) {
        			VFChatMsg chatMsg = (VFChatMsg) result;
        			String msg = chatMsg.getMsg();
        			if ( !"".equals(chatMsg.getNick()) )
        				msg = chatMsg.getNick()+": "+msg;
        			chatString(msg);
        		} else if (result instanceof VFRedirect) {
        			VFRedirect redir = (VFRedirect) result;
        			if ( id.equals(INIT_STATE)) {
        				id = redir.getTableID();
        				onHistoryChanged(redir.getTableID());
        			}
        		} else if (result instanceof VFUserid) {
        			VFUserid euserid = (VFUserid) result;
        			String userid = euserid.getUserid();
        			setUserid(userid);
        			//nameField.setText(userid);
        		} else if (result instanceof VFQueueName) {
        			VFQueueName eqid = (VFQueueName) result;
        			queueID = eqid.getQueueID();
        		} else if (result instanceof VFSetPlayer) {
        			VFSetPlayer vfsp = (VFSetPlayer) result;
        			if ( vfsp.getNick().equals(userid) ) 
        				activateStandUp();
        			addPendingEvent(result);
        		} else 
        			addPendingEvent(result);
        	}
        	//step();
          	/*
			if ( needToWait() ) {
				if (storedEvents != null)
					storedEvents.addAll(elist);
				else
					storedEvents = elist;
				step();
				return;
			}
			//System.out.println(HoldemTable.getTotTime()+" server: "+elist.size());
			processResults(elist);
    		htable.step();

	        sendNameToServer();
          //dialogBox.setText("Remote Procedure Call");
          //serverResponseLabel.removeStyleName("serverResponseLabelError");
          //serverResponseLabel.setHTML(result);
          //dialogBox.center();
          //closeButton.setFocus(true);
          
           */
        }
      });
      }
      toSend.clear();
      inSendCount--;
    }
  }
}
