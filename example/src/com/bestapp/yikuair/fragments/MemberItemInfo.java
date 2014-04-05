package com.bestapp.yikuair.fragments;

import java.io.Serializable;

public class MemberItemInfo implements Serializable {

	private static final long serialVersionUID = -940334105102606403L;
	private String name;
	private String headUrl;
	private String sex;
	private int type;
	private String dbId;
	private String[] memberName;
	private String[] memberId;

	public MemberItemInfo() {

	}

	public MemberItemInfo(String dbId, String name, String sex, String headUrl,
			int tp) {
		this.setDbId(dbId);
		this.setName(name);
		this.setSex(sex);
		this.setHeadUrl(headUrl);
		this.setType(tp);
	}

	public void setDbId(String id) {
		this.dbId = id;
	}

	public String getDbId() {
		return this.dbId;
	}

	public void setType(int tp) {
		this.type = tp;
	}

	public int getType() {
		return this.type;
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

}
