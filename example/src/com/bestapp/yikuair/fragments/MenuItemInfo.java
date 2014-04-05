package com.bestapp.yikuair.fragments;

import java.io.Serializable;

public class MenuItemInfo {
	private String menuName;
	private int num;
	private int numVisibility;
	private int backgroundId;

	public MenuItemInfo(String menuName, int num, int visible, int RId) {
		this.setMenuName(menuName);
		this.setNum(num);
		this.setNumVisible(visible);
		this.setIcon(RId);
	}

	public void setIcon(int Id) {
		this.backgroundId = Id;
	}

	public int getIcon() {
		return backgroundId;
	}

	public void setNumVisible(int visibility) {
		this.numVisibility = visibility;
	}

	public int getNumVisible() {
		return numVisibility;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getNum() {
		return num;
	}

	public void setMenuName(String name) {
		this.menuName = name;
	}

	public String getMenuName() {
		return menuName;
	}
}
