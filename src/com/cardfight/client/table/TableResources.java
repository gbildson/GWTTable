package com.cardfight.client.table;

//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.dom.client.ImageElement;
//import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
//import com.google.gwt.widgetideas.graphics.client.ImageLoader;


public class TableResources {
	public static ImageElement tableImg;
	public static ImageElement circleImg;
	public static Image        circleImg2;

	public static ImageElement smallCardImg;
	public static ImageElement dealerImg;
	public static ImageElement cards[];
	public static String       cardNames[];
	public static ImageElement chips[];
	public static String       chipURL[];
	public static double chipVal[] = 
	  {0.01,
	   0.05,
	   0.25,
	   1,
	   5,
	   25,
	   100,
	   500,
	   1000,
	   5000,
	   25000,
	   100000,
	   500000,
	   1000000,
	   5000000};
	private static boolean firstPass=true;
	private static String parentDir = "/img/";
	private static String newRes = "/img/";
	private static String suit[] = {"s", "c", "d", "h"};
	private static String rank[] = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
	private static Runnable callback = null;
	public static boolean loaded = false;

	public static void init(Runnable cb) {
		if (!firstPass) return;

		circleImg2 = new Image(parentDir+"circle.gif");
		callback = cb;
		
		//Toolkit defToolkit = Toolkit.getDefaultToolkit();
		String name;
		int count=0;
		cardNames = new String[52];
		for (int i=0; i < suit.length; i++) {
			for (int j=0; j < rank.length; j++) {
				name = parentDir + rank[j] + suit[i] + "50" + ".gif";
				addToImages(name);
				cardNames[count] = rank[j]+suit[i];
				//cards[count] = new Image(name);
				count++;
			}
		}
		//Image.prefetch(newRes+"1c.gif");
		//Image.prefetch(newRes+"5c.gif");
		//Image.prefetch(newRes+"25c.gif");
		//Image.prefetch(newRes+"1.gif");
		//Image.prefetch(newRes+"5.gif");
		//Image.prefetch(newRes+"25.gif");
		//Image.prefetch(newRes+"100.gif");
		//Image.prefetch(newRes+"500.gif");
		//Image.prefetch(newRes+"1K.gif");
		//Image.prefetch(newRes+"5K.gif");
		//Image.prefetch(newRes+"25K.gif");
		//Image.prefetch(newRes+"100K.gif");
		//Image.prefetch(newRes+"500K.gif");
		//Image.prefetch(newRes+"1M.gif");
		//Image.prefetch(newRes+"5M.gif");
		
		addToImages(newRes+"1c.gif");
		addToImages(newRes+"5c.gif");
		addToImages(newRes+"25c.gif");
		addToImages(newRes+"1.gif");
		addToImages(newRes+"5.gif");
		addToImages(newRes+"25.gif");
		addToImages(newRes+"100.gif");
		addToImages(newRes+"500.gif");
		addToImages(newRes+"1K.gif");
		addToImages(newRes+"5K.gif");
		addToImages(newRes+"25K.gif");
		addToImages(newRes+"100K.gif");
		addToImages(newRes+"500K.gif");
		addToImages(newRes+"1M.gif");
		addToImages(newRes+"5M.gif");
		addToImages(newRes+"D.gif");
		addToImages(newRes+"table.jpg");
		addToImages(newRes+"circle.gif");
		addToImages(newRes+"smallcard.gif");
		addToImages(newRes+"twosmall.gif");

		String[] imageUrls = new String[imgs.size()];
		imageUrls = imgs.toArray(imageUrls);
		TableResources.storeImages();

		/*ImageLoader.loadImages(imageUrls, new ImageLoader.CallBack() {

			public void onImagesLoaded(ImageElement[] imageHandles) {
				// Drawing code involving images goes here
				iElts = imageHandles;
				TableResources.storeImages();

			}
		});*/
		firstPass=false;
	}
	
	private static ArrayList<String>       imgs    = new ArrayList<String>();
	//private static HashMap<String,Integer> imgMap  = new HashMap<String,Integer>();
	//private static ImageElement[]          iElts   = null;
	
	private static void addToImages(String url) {
		Image.prefetch(url);
		//imgMap.put(url,imgs.size());
		imgs.add(url);
	}
	
	private static ImageElement storeImageElement(String url) {
		//int elt = imgMap.get(url);
		//return iElts[elt];
		return null;
	}

	public static void storeImages() {
		//cards = new ImageElement[52];
		String name;
		int count=0;

		for (int i=0; i < suit.length; i++) {
			for (int j=0; j < rank.length; j++) {
				name = parentDir + rank[j] + suit[i] + "50" + ".gif";
				//cards[count] = storeImageElement(name);
				count++;
			}
		}
		
		chipURL    = new String[15];
		chipURL[0] = new String(newRes+"1c.gif");
		chipURL[1] = new String(newRes+"5c.gif");
		chipURL[2] = new String(newRes+"25c.gif");
		chipURL[3] = new String(newRes+"1.gif");
		chipURL[4] = new String(newRes+"5.gif");
		chipURL[5] = new String(newRes+"25.gif");
		chipURL[6] = new String(newRes+"100.gif");
		chipURL[7] = new String(newRes+"500.gif");
		chipURL[8] = new String(newRes+"1K.gif");
		chipURL[9] = new String(newRes+"5K.gif");
		chipURL[10] = new String(newRes+"25K.gif");
		chipURL[11] = new String(newRes+"100K.gif");
		chipURL[12] = new String(newRes+"500K.gif");
		chipURL[13] = new String(newRes+"1M.gif");
		chipURL[14] = new String(newRes+"5M.gif");
		
		/*chips = new ImageElement[15];
		chips[0] = storeImageElement(newRes+"1c.gif");
		chips[1] = storeImageElement(newRes+"5c.gif");
		chips[2] = storeImageElement(newRes+"25c.gif");
		chips[3] = storeImageElement(newRes+"1.gif");
		chips[4] = storeImageElement(newRes+"5.gif");
		chips[5] = storeImageElement(newRes+"25.gif");
		chips[6] = storeImageElement(newRes+"100.gif");
		chips[7] = storeImageElement(newRes+"500.gif");
		chips[8] = storeImageElement(newRes+"1K.gif");
		chips[9] = storeImageElement(newRes+"5K.gif");
		chips[10] = storeImageElement(newRes+"25K.gif");
		chips[11] = storeImageElement(newRes+"100K.gif");
		chips[12] = storeImageElement(newRes+"500K.gif");
		chips[13] = storeImageElement(newRes+"1M.gif");
		chips[14] = storeImageElement(newRes+"5M.gif");
		//dealerImg = storeImageElement(newRes+"D.gif");
		tableImg  = storeImageElement(newRes+"table.jpg");
		//circleImg = storeImageElement(newRes+"circle.gif");
		//smallCardImg = storeImageElement(newRes+"smallcard.gif");*/
		loaded = true;
		if ( callback != null )
			callback.run();
	}
	
	/*
	public static Font myfont;
	public static Font cuteFont;
	public static Color LIGHT_TAN;
	static { 
		myfont = new Font("dialog", Font.BOLD, 13); 
		cuteFont = new Font("Times Roman", Font.BOLD, 15); 
		LIGHT_TAN = new Color(255, 220, 160);
	}
	*/
}
