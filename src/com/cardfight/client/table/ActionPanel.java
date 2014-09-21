package com.cardfight.client.table;

//import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.*;
import com.cardfight.client.poker.*;
import com.google.gwt.user.client.ui.ChangeListener;
import com.cardfight.client.TopControl;


public class ActionPanel implements KeyUpHandler, ChangeListener  {
	
	public static final int LIMIT     = 1;
	public static final int NO_LIMIT  = 2;
	public static final int POT_LIMIT = 3;
	
	FlowPanel cards; //a panel that uses CardLayout
	
	TextBox jtextfield;
	SliderBar jslider;
	ActionCallback actionCallback;
	String raiseLanguage;
	double  raiseAmount;
	Button b3;
	double  callableAmount;
	double  playerBetAmount;
    final static String BUTTONPANEL = "Card with JButtons";
    final static String TEXTPANEL = "Card with JTextField";
    private TopControl control;
    private boolean notInitialized = true;
    
    public ActionPanel(TopControl control) {
    	this.control = control;
    }
	
    public void addComponentsToPane() {
    	System.out.println("addComp");
        //parent.setLayout(null);
        
        //if (LOG.isDebugEnabled())
			//LOG.debug("***+++ TrackWidth: "+ UIManager.get( "Slider.trackWidth" ));
        //if (LOG.isDebugEnabled())
			//LOG.debug("***+++ TickLength: "+UIManager.get( "Slider.majorTickLength" ));
        
        //Create the panel that contains the "cards".
        cards = new FlowPanel();
        //cards.setOpaque(false);
    }
    
    public void finalInitialization() {
    	System.out.println("finalInit");
        //Insets insets = parent.getInsets();
        //JPanel tpane = this;
        //tpane.setLayout(new BorderLayout());
        control.addWidget(cards, 405, 470);
        //tpane.setBounds(405+insets.left, 450+insets.top, 350, 80);
        //tpane.setOpaque(false);
        //parent.add(tpane);
        //parent.validate();
        notInitialized = false;
    	System.out.println("done finalInit");
    }
	
	public void takeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
			  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit, double minNonLimitRaise, ActionCallback ac){
		final int _bettingMode = bettingMode;
		final String _nick = nick;
		final double _playerBet = playerBet;
		final double _toCall = toCall;
		final double _callable = callable;
		final double _bigBlind = bigBlind;
		final double _maxRaiseIfNoLimit = maxRaiseIfNoLimit;
		final double _raiseIfLimit = raiseIfLimit;
		final double _maxRaiseIfPotLimit = maxRaiseIfPotLimit;
		final double _minNonLimitRaise = minNonLimitRaise;
		final ActionCallback _ac = ac;
		
    	System.out.println("ap takeAction");

		
        //Schedule a job for the event-dispatching thread:
        //javax.swing.SwingUtilities.invokeLater(new Runnable() {
            //public void run() {
            	if (notInitialized)
            		addComponentsToPane();
            	real_takeAction(_bettingMode, _nick, _playerBet, _toCall, _callable, _bigBlind,
            	  _maxRaiseIfNoLimit, _raiseIfLimit, _maxRaiseIfPotLimit, _minNonLimitRaise, _ac);
            	if (notInitialized)
            		finalInitialization();
            //}
        //});
    }
	
	private void real_takeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
			  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit, 
			  double minNonLimitRaise, ActionCallback ac) {
		actionCallback = ac;
		
    	System.out.println("real takeAction");
		
		Label  playerLabel = new Label("***** "+nick+" ");
		Button b1;
		Button b2;
		//JButton b3 = null;

		b1 = new Button("Fold");
		b1.setStyleName("betbutton");
	    b1.addClickListener(new ClickListener() {
	      public void onClick(Widget sender) {
				//actionCallback.deliverBettingResponse(-1.0d);
				control.addBetResponse(-1.0d);
				clearPanel();
	      }
	    });

		if ( toCall == 0.0d ) {
			b2 = new Button("Check");
			b2.setStyleName("betbutton");
		    b2.addClickListener(new ClickListener() {
			      public void onClick(Widget sender) {
						//actionCallback.deliverBettingResponse(0.0d);
						control.addBetResponse(0.0d);
						clearPanel();
			      }
			    });
		} else{
			b2 = new Button("Call "+getChipAmount(callable));
			b2.setStyleName("betbutton");
			callableAmount = callable;
			playerBetAmount = playerBet;
		    b2.addClickListener(new ClickListener() {
			      public void onClick(Widget sender) {
						//actionCallback.deliverBettingResponse(callableAmount+playerBetAmount);
						control.addBetResponse(callableAmount+playerBetAmount);
						clearPanel();
			      }
			    });
		}
		
		HorizontalPanel raiseControl = null;
		// If you have enough money to raise more than the minimum
		//System.out.println("********* mR:"+maxRaiseIfNoLimit+" rL:"+raiseIfLimit);
		if ( maxRaiseIfNoLimit > raiseIfLimit+0.001d ) {
			if (bettingMode == /*TexasHoldem.*/LIMIT) 
				raiseAmount = raiseIfLimit+toCall+playerBet; 
			else {
				raiseAmount = minNonLimitRaise+toCall+playerBet;
				//System.out.println("mNLR:"+minNonLimitRaise+" toC:"+toCall+" raiseAmount:"+raiseAmount);

				// Create a sliding raise control with text input
				raiseControl = new HorizontalPanel();
				raiseControl.add(jtextfield = new TextBox());
				jtextfield.setText(getChipAmount(raiseAmount));
				jtextfield.setVisibleLength(5);
				jtextfield.addKeyUpHandler(this);
				jtextfield.setStyleName("bettext");


				//card2.add(jtextfield = new JTextField("0", 6));card2.add(jtextfield = new JTextField("0", 6));
				double maxRaise = ( bettingMode == /*TexasHoldem.*/POT_LIMIT ? maxRaiseIfPotLimit : maxRaiseIfNoLimit);
				maxRaise += toCall;
				//if (LOG.isDebugEnabled())
					//LOG.debug("RA:"+((int)raiseAmount)+" MR:"+((int)maxRaise));
				
				//control.logLocally("RA:"+((int)raiseAmount)+" MR:"+((int)maxRaise));

				raiseAmount = Math.min(raiseAmount, maxRaise);  // If a raise puts you all in then don't exceed that.
				
				jslider = new SliderBar(raiseAmount, maxRaise);  // TODO: Max won't work without+1
				jslider.setMaxValue(maxRaise);
				jslider.setMinValue(raiseAmount);
				jslider.setStepSize( bigBlind);
				jslider.setCurrentValue(raiseAmount, false);

				//jslider.setUI(new GBSliderUI());
				//jslider.setStepSize((int) raiseAmount / 2);
				//jslider.setMajorTickSpacing((int) raiseAmount);
				//jslider.setExtent((int) bigBlind);


				jslider.addChangeListener(this);
				//JPanel holder = new JPanel();
				//holder.setOpaque(false);
				//holder.setPreferredSize(new Dimension(130,20));
				raiseControl.add(jslider);
				//raiseControl.add(holder);
				//raiseControl.setOpaque(false);
			}
		} else {
			// raise only what you can
			raiseAmount = raiseIfLimit+toCall+playerBet; 
		}
		//System.out.println("********* rA:"+raiseAmount);

		if (raiseAmount > 0.001d) {
			
			if ( toCall == 0.0d )
				raiseLanguage = "Bet ";
			else
				raiseLanguage = "Raise to ";
			b3 = new Button(getRaiseText()+"   ");
			b3.setStyleName("betbutton");

			//raiseAmount = raiseIfLimit+toCall;
			b3.addClickListener(new ClickListener() {
			      public void onClick(Widget sender) {
						//System.out.println("RAISEAMOUNT:"+raiseAmount);
						//actionCallback.deliverBettingResponse(raiseAmount);
						control.addBetResponse(raiseAmount);
						clearPanel();
			      }
			    });
		}

		DockPanel topPane    = new DockPanel();
		//topPane.setOpaque(false);
		DockPanel centerPane = new DockPanel();
		//FlowPanel centerPane = new FlowPanel();

		//centerPane.setOpaque(false);
		centerPane.add(playerLabel, DockPanel.WEST);
		
		if (raiseControl != null) {
			centerPane.add(raiseControl, DockPanel.EAST);
			centerPane.setWidth("100%");
		}
		topPane.add(centerPane, DockPanel.CENTER);
		//topPane.setWidth("100%");
		//topPane.setWidth("500px");
		topPane.setWidth("100%");


		
		DockPanel actionPane = new DockPanel();
		//actionPane.setLayout(new FlowLayout());
		//actionPane.setBackground(Color.gray);
		//actionPane.setOpaque(false);
		//actionPane.add(playerLabel);
		actionPane.add(b1, DockPanel.WEST);
		actionPane.add(b2, DockPanel.CENTER);
		//System.out.println("********* rL:"+raiseIfLimit);
		if (raiseIfLimit > 0.001d){ 
			actionPane.add(b3, DockPanel.EAST);
			//System.out.println("Added b3 text:"+b3.getText());
		}
		topPane.add(actionPane, DockPanel.SOUTH);
		
		
		//cards.setSize("500px", "100px");
		cards.setHeight("100px");
		cards.setWidth("100%");
		cards.add(topPane);
		cards.setStyleName("betpanel");

		//cards.add(topPane, nick);
		//CardLayout cl = (CardLayout)(cards.getLayout());
		//cl.show(cards, nick);
		//parent.validate();  // TODO: Is this necessary or correct?
		//cards.repaint();
	}
	
	  public void onKeyUp(KeyUpEvent event) {
		  if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				raiseAmount = Double.valueOf(jtextfield.getText());
				b3.setText(getRaiseText());
		  }
	  }
	  

	
	private String getRaiseText() {
		return raiseLanguage + getChipAmount(raiseAmount);
	}
	
	private String getChipAmount(double amt) {
		//long lamt = (long)(amt * 100);
		//amt = (double)(lamt / 100);
		int idx;
		String amount = "" + amt;
		if (amount.endsWith(".0")) {
			amount = amount.substring(0, amount.length()-2);
		} else if ( (idx = amount.indexOf(".")) > 0 ) {
			amount = amount.substring(0, idx+3);
		}
		return amount;
	}
	
	public void actionTimeout() {
		//control.logLocally("actionTimeout");
        //javax.swing.SwingUtilities.invokeLater(new Runnable() {
           // public void run() {
            	clearPanel();
           // }
        //});
	}
	
	private void clearPanel() {
		cards.clear();
        //control.removeWidget(cards);
	}
	
    //public void itemStateChanged(ItemEvent evt) {
    	//TODO need to rebuild panel no doubt
        //CardLayout cl = (CardLayout)(cards.getLayout());
        //cl.show(cards, (String)evt.getItem());
        //if (LOG.isDebugEnabled())
			//LOG.debug("***+++ Slider Width: "+jslider.getWidth());

    //}
    
    /** Listen to the slider. */
	public void onChange(Widget sender) {
		//control.logLocally("SliderBar change");
        SliderBar source = (SliderBar)sender;
        //if (!source.getValueIsAdjusting()) {
            double fps = source.getCurrentValue();
            double inc = source.getTotalRange();
            //if (LOG.isDebugEnabled())
    			//LOG.debug("inc = "+inc+" fps="+fps);
            /*if ( fps % inc > 0 && fps % inc < (inc/2) ) {
            	fps = ((fps / inc) * inc)+inc;
            	source.setCurrentValue(fps, false);
            } else if ( fps % inc > 0 && fps % inc >= (inc/2) ) {
            	fps = ((fps / inc) * inc);
            	source.setCurrentValue(fps, false);
            }*/
            jtextfield.setText(""+fps);
            //if (LOG.isDebugEnabled())
    			//LOG.debug("+++ vvvv:" + fps);
			//control.logLocally("+++ vvvv:" + fps);

            raiseAmount = Double.valueOf(fps);
			b3.setText(getRaiseText());
        //}
        //if (LOG.isDebugEnabled())
			//LOG.debug("+++ width:"+source.getWidth()+" height:" + source.getHeight());
    }
	
	public static ActionPanel create(TopControl control) {
		final TopControl      fpane       = control;
		final ActionPanel actionPanel = new ActionPanel(fpane);
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //javax.swing.SwingUtilities.invokeLater(new Runnable() {
        //    public void run() {
        //    	actionPanel.addComponentsToPane();
        //    }
        //});
        return actionPanel;
    }
	
	/*private void createSlider() {
		SliderBar slider = new SliderBar(0.0, 100.0);
		slider.setStepSize(5.0);
		slider.setCurrentValue(50.0);
		//addWidget(slider, 200, 200);
	}*/
}




