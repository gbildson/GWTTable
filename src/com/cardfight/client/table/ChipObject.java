package com.cardfight.client.table;

import com.cardfight.client.GWTTable;
import com.cardfight.client.TopControl;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.user.client.ui.Image;
import java.util.ArrayList;

public abstract class ChipObject extends TableObject {
	private TopControl control;
	public ChipObject(TopControl control) {
		this.control = control;
	}
	
	protected ArrayList<Image> displayChips(int x, int y, double amt, int  displayType) {
		int    cnt;
		int    cno;
		double val;
		ArrayList<Image> images = new ArrayList<Image>();
		
		//System.out.println("Amt:"+amt);
		for ( int i = 14; i>=0; i-- ) {
			val = TableResources.chipVal[i];
			if (amt >= val) {
				cnt = (int) (amt/val);
				//System.out.print(val+":"+cnt+", ");
				amt -= ((double)cnt) * val;
				images.addAll(displayChipStack(x,y,i,cnt));
				if (displayType == Geometry.FORWARD) {
					x += Geometry.CHIP_HORIZONTAL;
				} else if (displayType == Geometry.BACKWARD) {
					x -= Geometry.CHIP_HORIZONTAL;
				} else if  (displayType == Geometry.INPLACE) {
					y -= Geometry.CHIP_VERTICAL * cnt;
				}
			}
		}
		//System.out.println("");
		return images;
	}

	protected ArrayList<Image> displayChipStack(int x,int y,int cno,int cnt) {
		ArrayList<Image> images = new ArrayList<Image>();
		for (int i = 0; i < cnt; i++) {
			//g2.drawImage(TableResources.chips[cno], x, y-Geometry.CHIP_VERTICAL*i);
			Image img = new Image(TableResources.chipURL[cno]);
			control.addWidget(img, x, y-Geometry.CHIP_VERTICAL*i);
			images.add(img);
			//System.out.println("chipVal : "+TableResources.chipURL[cno] + " x: "+x+" y: "+y );
		}
		return images;
	}
}
