package com.bestapp.yikuair.fragments;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bestapp.yikuair.officialaccount.PicTextList;

public class ChatMsgEntity implements Serializable {

	private static final long serialVersionUID = -1997593361914959431L;

	private String senderId;

	private String receiverId;

	private String userId = "";

	private String sex;

	private String time;

	private String fullTime;

	private String content;

	private String checkStatus;

	private String name;

	private int checkBackground;

	private String date;

	private String photoUrl;

	private String smallPicUrl;

	private String bigPicUrl;

	private String chatState;

	private String second;

	private String voiceUrl;

	private String scheduleTitle;

	private String scheduleBeginTime;

	private String scheduleEndTime;

	private String scheduleAddress;

	private String scheduleTaskId;

	private String scheduleItemId;

	private String beginDate;

	private String endDate;

	private String groupId;

	private String groupIds;

	private String groupNames;

	private int scheduleType;

	private String chatName;

	private int type;

	private int backgroundID;

	private boolean isComing = true;

	private int voiceAnim;

	private String msguuid;

	private int status;

	private int animVisible;

	private int stateVisible;

	private String longitude;

	private String latitude;

	private boolean isAdd = false;

	private boolean isChangeGroupInfo = false;

	private int chatType = 0;// individual or group 0:individual 1: group

	private int messageType;// companynews:5 feedbacknews:6

	private String title; // pic_text title

	private String fromname;

	public List<PicTextList> list = new ArrayList<PicTextList>();

	private String detail;

	public String getDetail() {
		return detail;
	}

	public String getUrl() {
		return url;
	}

	private String url;

	public PicTextList getParentPicText() {
		PicTextList picTextList = new PicTextList();
		picTextList.setDetail(detail);
		picTextList.setUrl(url);
		return picTextList;
	}

	public List<PicTextList> getList() {
		return list;
	}

	public void setList(List<PicTextList> list) {
		this.list.clear();
		this.list.addAll(list);
	}

	public String getFromname() {
		return fromname;
	}

	public void setFromname(String fromname) {
		this.fromname = fromname;
	}

	private List<String> memberIdList = new ArrayList<String>();

	public ChatMsgEntity() {

	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ChatMsgEntity) {
			return this.getUserId().equals(((ChatMsgEntity) o).getUserId());
		}
		return false;

	};

	public ChatMsgEntity(boolean isComing, String senderId, String receiverId,
			String content, String time, String date, String smallImgPath,
			String bigImgPath, int type, int status, String voiceUrl) {
		setIsComing(isComing);
		setSenderId(senderId);
		setReceiverId(receiverId);
		setTime(time);
		setDate(date);
		setContent(content);
		setSmallPicUrl(smallImgPath);
		setBigPicUrl(bigImgPath);
		setType(type);
		setStatus(status);
		setVoiceUrl(voiceUrl);
	}

	public void setIsChangeGroupInfo(boolean isChange) {
		this.isChangeGroupInfo = isChange;
	}

	public boolean getIsChangeGroupInfo() {
		return this.isChangeGroupInfo;
	}

	public void setlongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getlongitude() {
		return this.longitude;
	}

	public void setlatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getlatitude() {
		return this.latitude;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setUserId(String id) {
		this.userId = id;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setBeginDate(String date) {
		this.beginDate = date;
	}

	public String getBeginDate() {
		return this.beginDate;
	}

	public void setEndDate(String date) {
		this.endDate = date;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setScheduleItemId(String itemId) {
		this.scheduleItemId = itemId;
	}

	public String getScheduleItemId() {
		return this.scheduleItemId;
	}

	public void setGroupNames(String names) {
		this.groupNames = names;
	}

	public String getGroupNames() {
		return groupNames;
	}

	public void setGroupIds(String ids) {
		this.groupIds = ids;
	}

	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupId(String id) {
		this.groupId = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setMessageType(int type) {
		this.messageType = type;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setChatType(int type) {
		this.chatType = type;
	}

	public int getChatType() {
		return chatType;
	}

	public void setScheduleTaskId(String taskId) {
		this.scheduleTaskId = taskId;
	}

	public String getScheduleTaskId() {
		return scheduleTaskId;
	}

	public void setIsAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public boolean getIsAdd() {
		return isAdd;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSex() {
		return sex;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPhotoUrl(String url) {
		this.photoUrl = url;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setScheduleType(int type) {
		this.scheduleType = type;
	}

	public int getScheduleType() {
		return scheduleType;
	}

	public void setmemberIdList(List<String> memberIdList) {
		this.memberIdList.clear();
		this.memberIdList.addAll(memberIdList);
	}

	public List<String> getMemberIdList() {
		return memberIdList;
	}

	public void setChatName(String chatName) {
		this.chatName = chatName;
	}

	public String getChatName() {
		return chatName;
	}

	public void setStateVisible(int visible) {
		this.stateVisible = visible;
	}

	public int getStateVisible() {
		return stateVisible;
	}

	public void setAnimVisibile(int animVisible) {
		this.animVisible = animVisible;
	}

	public int getAnimVisible() {
		return animVisible;
	}

	public void setMsguuid(String msguuid) {
		this.msguuid = msguuid;
	}

	public String getMsguuid() {
		return msguuid;
	}

	public void setVoiceAnim(int id) {
		voiceAnim = id;
	}

	public int getVoiceAnim() {
		return voiceAnim;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setBackground(int id) {
		backgroundID = id;
	}

	public String getChatState() {
		return chatState;
	}

	public void setChatState(String state) {
		this.chatState = state;
	}

	public int getBackground() {
		return backgroundID;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String id) {
		this.senderId = id;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String id) {
		this.receiverId = id;
	}

	public String getFullTime() {
		return this.fullTime;
	}

	public void setFullTime(String time) {
		this.fullTime = time;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean getIsComing() {
		return isComing;
	}

	public void setIsComing(boolean isComing) {
		this.isComing = isComing;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getSmallPicUrl() {
		return smallPicUrl;
	}

	public void setSmallPicUrl(String smallPicUrl) {
		this.smallPicUrl = smallPicUrl;
	}

	public String getBigPicUrl() {
		return bigPicUrl;
	}

	public void setBigPicUrl(String bigPicUrl) {
		this.bigPicUrl = bigPicUrl;
	}

	public String getScheduleTitle() {
		return scheduleTitle;
	}

	public void setScheduleTitle(String scheduleTitle) {
		this.scheduleTitle = scheduleTitle;
	}

	public String getScheduleBeginTime() {
		return scheduleBeginTime;
	}

	public void setScheduleBeginTime(String scheduleBeginTime) {
		this.scheduleBeginTime = scheduleBeginTime;
	}

	public String getScheduleEndTime() {
		return scheduleEndTime;
	}

	public void setScheduleEndTime(String scheduleEndTime) {
		this.scheduleEndTime = scheduleEndTime;
	}

	public String getScheduleAddress() {
		return scheduleAddress;
	}

	public void setScheduleAddress(String scheduleAddress) {
		this.scheduleAddress = scheduleAddress;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String status) {
		this.checkStatus = status;
	}

	public int getCheckBackground() {
		return checkBackground;
	}

	public void setCheckBackground(int background) {
		this.checkBackground = background;
	}

}
