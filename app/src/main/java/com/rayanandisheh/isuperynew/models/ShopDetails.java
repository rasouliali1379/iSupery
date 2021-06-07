package com.rayanandisheh.isuperynew.models;

import com.google.gson.annotations.SerializedName;

public class ShopDetails{

	@SerializedName("iSuperyApp")
	private int iSuperyApp;

	@SerializedName("strName")
	private String strName;

	@SerializedName("strToken2")
	private String strToken2;

	@SerializedName("strURL")
	private String strURL;

	@SerializedName("strToken1")
	private String strToken1;

	@SerializedName("strIcon")
	private String strIcon;

	@SerializedName("iOrder")
	private int iOrder;

	@SerializedName("bActive")
	private boolean bActive;

	public int getISuperyApp(){
		return iSuperyApp;
	}

	public String getStrName(){
		return strName;
	}

	public String getStrToken2(){
		return strToken2;
	}

	public String getStrURL(){
		return strURL;
	}

	public String getStrToken1(){
		return strToken1;
	}

	public String getStrIcon(){
		return strIcon;
	}

	public int getIOrder(){
		return iOrder;
	}

	public boolean isBActive(){
		return bActive;
	}
}