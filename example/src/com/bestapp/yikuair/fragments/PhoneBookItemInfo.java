package com.bestapp.yikuair.fragments;

public class PhoneBookItemInfo{
	private String staffName;
	private String staffNumber;
	private String staffDepartment;
	private String alpha;
	
	public PhoneBookItemInfo(String name, String number, String department, String alpha)
	{
		this.setStaffName(name);		
		this.setStaffNumber(number);
		this.setStaffDepartment(department);
		this.setAlpha(alpha);
	}
	
	public void setStaffName(String name) {
		this.staffName = name;
	}
	
	public String getStaffName() {
		return staffName;
	}
	
	public void setStaffNumber(String number) {
		this.staffNumber = number;
	}
	
	public String getStaffNumber() {
		return staffNumber;
	}
	
	public void setStaffDepartment(String department) {
		this.staffDepartment = department;
	}
	
	public String getStaffDepartment() {
		return staffDepartment;
	}
	
	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}
	
	public String getAlpha() {
		return alpha;
	}
}
