package com.cardfight.client.table;

import java.util.ArrayList;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class ToolTipHelper {
	
	//private static final Log LOG = LogFactory.getLog(ToolTipHelper.class);
	
	private HasToolTip component;
	private int x, y, width, height;

	private static ArrayList<ToolTipHelper> components = new ArrayList<ToolTipHelper>();  

	public static String mouseHoverEvent(int x, int y) {
		for ( ToolTipHelper comp: components ) {
			if ( comp.component.inside(x, y) ) {
				//if (LOG.isDebugEnabled())
					//LOG.debug("Num TT:"+components.size());
				return comp.getText();
			}
		}
		return null;
	}

	public static void clearToolTip(HasToolTip component){
		if (component != null)
			component.deregister();
	}

	public ToolTipHelper(HasToolTip component, int x, int y, int width, int height) {
		this.component = component;
		reposition(x,y,width,height);
		register();
	}

	public void register() {
		components.add(this);
		//if (LOG.isDebugEnabled())
			//LOG.debug("comp add: "+instanceID()+" val:"+component.getText()+" s:" + components.size());
	}

	public void deregister() {
		components.remove(this);
		//if (LOG.isDebugEnabled())
			//LOG.debug("comp remove: "+instanceID()+" val:"+component.getText()+" s:"  + components.size());
	}
	
	public static void dump() {
		for (ToolTipHelper tth : components) {
			//if (LOG.isDebugEnabled())
				//LOG.debug("-- "+tth.instanceID()+ " val:"+tth.component.getText());
		}
	}

	private String instanceID() {
		if ( component instanceof PartialPot )
			return "PartialPot";
		if ( component instanceof Pot )
			return "Pot";
		if ( component instanceof Bet )
			return "Bet";
		return "unknown";
	}

	public void reposition(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void reposition(int x, int y){
		this.x = x;
		this.y = y;
	}

	public boolean inside(int x, int y) {
		x -= this.x;
		if ( x < 0 || x > this.width ) return false;
		y -= this.y;
		if ( y < 0 || y > this.height ) return false;
		return true;
	}

	public String getText() {
		return component.getText();
	}
}


