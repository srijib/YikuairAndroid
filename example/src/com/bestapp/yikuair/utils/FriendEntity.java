package com.bestapp.yikuair.utils;

import java.io.Serializable;

public class FriendEntity implements Serializable {

	private static final long serialVersionUID = 5171599688918631323L;

	private String phone;

	private String id;

	private String signature;

	private String duty;

	private String realName;

	private String mobile;

	private String headUrl;

	private String departmentName;

	private String team;

	private String alpha;

	private String email;

	private String companyId;

	private boolean isChecked;

	private String sex;

	private String dbId;

	private String groupId;

	private String pinyin;

	private String searchIndex;

	public FriendEntity() {

	}

	public FriendEntity(String id, String realName, String phone,
			String mobile, String email, String duty, String departmentName,
			String headUrl, String signature, String companyId,
			String alphaName, String team, String sex, String dbId,
			String pinyin, String searchInd) {
		setPhone(phone);
		setID(id);
		setSignature(signature);
		setDuty(duty);
		setRealName(realName);
		setMobile(mobile);
		setHeadUrl(headUrl);
		setDepartmentName(departmentName);
		setAlpher(alphaName);
		setEmail(email);
		setCompanyId(companyId);
		setTeam(team);
		setSex(sex);
		setDbId(dbId);
		setPinyin(pinyin);
		setSearchIndex(searchInd);
	}

	public void setSearchIndex(String index) {
		this.searchIndex = index;
	}

	public String getSearchIndex() {
		return this.searchIndex;
	}

	public void setPinyin(String py) {
		this.pinyin = py;
	}

	public String getPinyin() {
		return this.pinyin;
	}

	public void setGroupId(String id) {
		this.groupId = id;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public String getDbId() {
		return this.dbId;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSex() {
		return this.sex;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getTeam() {
		return this.team;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean getIsChecked() {
		return this.isChecked;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyId() {
		return this.companyId;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setID(String Id) {
		this.id = Id;
	}

	public String getId() {
		return id;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String name) {
		this.realName = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String url) {
		this.headUrl = url;
	}

	public void setDepartmentName(String name) {
		this.departmentName = name;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setAlpher(String alpha) {
		this.alpha = alpha;
	}

	public String getAlpha() {
		return alpha;
	}

}
