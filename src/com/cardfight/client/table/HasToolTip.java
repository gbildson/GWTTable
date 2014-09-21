package com.cardfight.client.table;


interface HasToolTip {
	public boolean inside(int x, int y);
	public String  getText();
	public void    deregister();
}

