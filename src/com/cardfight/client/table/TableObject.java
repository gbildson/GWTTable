
package com.cardfight.client.table;

//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;


public abstract class TableObject {
	protected int x;
	protected int y;

	public TableObject() {
	}

	public void setLoc(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract void display();
	
	public abstract void delete();
}

