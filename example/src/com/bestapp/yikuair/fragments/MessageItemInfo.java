package com.bestapp.yikuair.fragments;

import java.io.Serializable;

import android.view.View;

public class MessageItemInfo implements Serializable {

	private static final long serialVersionUID = 7773086931333744679L;
	private String Id;
	private String groupId;
	private String content;
	private String time;
	private String fullTime;
	private String name;
	private String headUrl;
	private String sex;
	private String[] memberName;
	private String[] memberId;
	private int isMessageNumVisible;
	private int messageNum;
	private boolean isAdd = false;
	private int isDelShow = View.GONE;

	public MessageItemInfo() {

	}

	public MessageItemInfo(String Id, String content, String time, String name,
			String sex, String headUrl, int isShow, String fulltime, String groupid) {
		this.setId(Id);
		this.setContent(content);
		this.setTime(time);
		this.setName(name);
		this.setSex(sex);
		this.setHeadUrl(headUrl);
		this.setIsDelShow(isShow);
		this.setFullTime(fulltime);
		this.setGroupId(groupid);
	}

	public void setFullTime(String time) {
		this.fullTime = time;
	}

	public String getFullTime() {
		return this.fullTime;
	}

	public void setIsDelShow(int isShow) {
		this.isDelShow = isShow;
	}

	public int getIsDelShow() {
		return this.isDelShow;
	}

	public void setSex(String userSex) {
		this.sex = userSex;
	}

	public String getSex() {
		return this.sex;
	}

	public void setHeadUrl(String headurl) {
		this.headUrl = headurl;
	}

	public String getHeadUrl() {
		return this.headUrl;
	}

	public void setIsAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public boolean getIsAdd() {
		return this.isAdd;
	}

	public void setMessageNum(int num) {
		this.messageNum = num;
	}

	public int getMessageNum() {
		return messageNum;
	}

	public void setIsMessageNumVisible(int visible) {
		this.isMessageNumVisible = visible;
	}

	public int getIsMessageNumVisible() {
		return isMessageNumVisible;
	}

	public void setMemberId(String[] id) {
		this.memberId = id;
	}

	public String[] getMemberId() {
		return memberId;
	}

	public void setMemberName(String[] name) {
		this.memberName = name;
	}

	public String[] getMemberName() {
		return memberName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setGroupId(String Id) {
		this.groupId = Id;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setId(String Id) {
		this.Id = Id;
	}

	public String getId() {
		return Id;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}
}
