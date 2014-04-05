package com.bestapp.yikuair.fragments;

import java.io.Serializable;

public class ScheduleItemInfo implements Serializable {

	private static final long serialVersionUID = 9017303727999497086L;
	private String sponsorName;
	private String scheduleContent;
	private String scheduleEndTime;
	private String scheduleBeginTime;
	private String[] memberNameStr;
	private String[] memberIdStr;
	private String id;
	private int type;
	private String itemId;
	private boolean isDel;
	private String address;
	private boolean isFromChat = false;
	private String taskId;
	private String groupId;

	public ScheduleItemInfo(String sponsorName, String scheduleContent,
			String scheduleBeginTime, String scheduleEndTime, String[] nameStr,
			String[] idStr, String Id, int type, String itemId, boolean isDel,
			String address, String taskid, String groupId) {
		this.setSponsorName(sponsorName);
		this.setScheduleContent(scheduleContent);
		this.setScheduleBeginTime(scheduleBeginTime);
		this.setScheduleEndTime(scheduleEndTime);
		this.setMemberName(nameStr);
		this.setMemberId(idStr);
		this.setId(Id);
		this.setType(type);
		this.setItemId(itemId);
		this.setIsDel(isDel);
		this.setAddress(address);
		this.setTaskId(taskid);
		this.setGroupId(groupId);
	}

	public ScheduleItemInfo() {
	}

	public void setGroupId(String id) {
		this.groupId = id;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setTaskId(String id) {
		this.taskId = id;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setIsFromChat(boolean isFrom) {
		this.isFromChat = isFrom;
	}

	public boolean getIsFromChat() {
		return isFromChat;
	}

	public void setAddress(String addr) {
		this.address = addr;
	}

	public String getAddress() {
		return address;
	}

	public void setItemId(String id) {
		this.itemId = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setIsDel(boolean isDel) {
		this.isDel = isDel;
	}

	public boolean getIsDel() {
		return isDel;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setMemberName(String[] name) {
		this.memberNameStr = name;
	}

	public String[] getMemberName() {
		return memberNameStr;
	}

	public void setMemberId(String[] id) {
		this.memberIdStr = id;
	}

	public String[] getmemberId() {
		return memberIdStr;
	}

	public void setSponsorName(String name) {
		this.sponsorName = name;
	}

	public String getSponsorName() {
		return sponsorName;
	}

	public void setScheduleContent(String content) {
		this.scheduleContent = content;
	}

	public String getScheduleContent() {
		return scheduleContent;
	}

	public void setScheduleEndTime(String time) {
		this.scheduleEndTime = time;
	}

	public String getScheduleEndTime() {
		return scheduleEndTime;
	}

	public void setScheduleBeginTime(String time) {
		this.scheduleBeginTime = time;
	}

	public String getScheduleBeginTime() {
		return scheduleBeginTime;
	}
}
