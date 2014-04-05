package com.bestapp.yikuair.fragments;

import java.io.Serializable;

public class gridItemInfo{
	private String menuName;
	private int menuPic;
	
	public gridItemInfo(String menuName, int menuPic)
	{
		this.setMenuName(menuName);
		this.setMenuImg(menuPic);
	}
	
	public void setMenuImg(int menuPic) {
		this.menuPic = menuPic;
	}

	public int getMenuImg() {
		return menuPic;
	}
	
	public void setMenuName(String name) {
		this.menuName = name;
	}
	
	public String getMenuName() {
		return menuName;
	}
}
