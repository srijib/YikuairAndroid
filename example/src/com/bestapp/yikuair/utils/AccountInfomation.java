package com.bestapp.yikuair.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class AccountInfomation implements Parcelable {
	/**
	 * 
	 */
	private String birthday;
	private String sex;
	private String phone;
	private String count;
	private String offmessage;
	private String com_id;
	private String headurl;
	private String password;
	private String cardid;
	private String id;
	private String username;
	private String token;
	private String duty;
	private String age;
	private String entrytime;
	private String pinyin;
	private String signature;
	private String longAlt;
	private String hometown;
	private String nickname;
	private String machinetype;
	private String star;
	private String de_name;
	private String user_no;
	private String interfaces;
	private String address;
	private String email;
	private String own;
	private String realname;
	private String mobile;
	private String information;
	private int infor_num = 0;
	private String time = "";
	private boolean clear = false;
	private String far;
	private boolean like = false;
	private String distance;

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}

	public String getFar() {
		return far;
	}

	public void setFar(String far) {
		this.far = far;
	}

	public boolean isClear() {
		return clear;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}

	public AccountInfomation() {
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof AccountInfomation) {
			return this.getId().equals(((AccountInfomation) o).getId());
		}
		return false;
	};

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public int getInfor_num() {
		return infor_num;
	}

	public void setInfor_num(int infor_num) {
		if (infor_num == 0) {
			this.infor_num = 0;
		} else {
			this.infor_num = this.infor_num + infor_num;
		}
	}

	private boolean subStyle;

	public boolean isSub() {
		return subStyle;
	}

	public void setSub(boolean subStyle) {
		this.subStyle = subStyle;
	}

	public boolean isSubStyle() {
		return subStyle;
	}

	public void setSubStyle(boolean subStyle) {
		this.subStyle = subStyle;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getOffmessage() {
		return offmessage;
	}

	public void setOffmessage(String offmessage) {
		this.offmessage = offmessage;
	}

	public String getCom_id() {
		return com_id;
	}

	public void setCom_id(String com_id) {
		this.com_id = com_id;
	}

	public String getHeadurl() {
		return headurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getEntrytime() {
		return entrytime;
	}

	public void setEntrytime(String entrytime) {
		this.entrytime = entrytime;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getLongAlt() {
		return longAlt;
	}

	public void setLongAlt(String longAlt) {
		this.longAlt = longAlt;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMachinetype() {
		return machinetype;
	}

	public void setMachinetype(String machinetype) {
		this.machinetype = machinetype;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getDe_name() {
		return de_name;
	}

	public void setDe_name(String de_name) {
		this.de_name = de_name;
	}

	public String getUser_no() {
		return user_no;
	}

	public void setUser_no(String user_no) {
		this.user_no = user_no;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOwn() {
		return own;
	}

	public void setOwn(String own) {
		this.own = own;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String[] toStringArray() {
		String stringArray = " ;" + getRealname() + ";" + getPhone() + ";"
				+ getMobile() + ";" + getEmail() + ";" + getDuty() + "; ;"
				+ getHeadurl() + ";" + getSignature() + ";" + getCom_id()
				+ "; ; ;" + getSex() + ";" + getId() + ";" + getPinyin() + "; ";

		return stringArray.split(";");
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.address);
		dest.writeString(this.age);
		dest.writeString(this.birthday);
		dest.writeString(this.cardid);
		dest.writeString(this.com_id);
		dest.writeString(this.count);
		dest.writeString(this.de_name);
		dest.writeString(this.duty);
		dest.writeString(this.email);
		dest.writeString(this.entrytime);
		dest.writeString(this.headurl);
		dest.writeString(this.hometown);
		dest.writeString(this.id);
		dest.writeString(this.information);
		dest.writeString(this.interfaces);
		dest.writeInt(this.infor_num);
		dest.writeString(this.longAlt);
		dest.writeString(this.machinetype);
		dest.writeString(this.mobile);
		dest.writeString(this.nickname);
		dest.writeString(this.offmessage);
		dest.writeString(this.own);
		dest.writeString(this.password);
		dest.writeString(this.phone);
		dest.writeString(this.pinyin);
		dest.writeString(this.realname);
		dest.writeString(this.sex);
		dest.writeString(this.signature);
		dest.writeString(this.star);
		dest.writeValue(this.subStyle);
		dest.writeString(this.time);
		dest.writeString(this.token);
		dest.writeString(this.user_no);
		dest.writeString(this.username);
		dest.writeString(this.far);
		dest.writeValue(this.like);
		dest.writeString(this.distance);

	}

	private AccountInfomation(Parcel in) {
		this.address = in.readString();
		this.age = in.readString();
		this.birthday = in.readString();
		this.cardid = in.readString();
		this.com_id = in.readString();
		this.count = in.readString();
		this.de_name = in.readString();
		this.duty = in.readString();
		this.email = in.readString();
		this.entrytime = in.readString();
		this.headurl = in.readString();
		this.hometown = in.readString();
		this.id = in.readString();

		this.information = in.readString();
		this.interfaces = in.readString();
		this.infor_num = in.readInt();
		this.longAlt = in.readString();
		this.machinetype = in.readString();
		this.mobile = in.readString();
		this.nickname = in.readString();
		this.offmessage = in.readString();
		this.own = in.readString();
		this.password = in.readString();
		this.phone = in.readString();
		this.pinyin = in.readString();
		this.realname = in.readString();
		this.sex = in.readString();
		this.signature = in.readString();
		this.star = in.readString();
		this.subStyle = (Boolean) in.readValue(Boolean.class.getClassLoader());
		this.time = in.readString();
		this.token = in.readString();
		this.user_no = in.readString();
		this.username = in.readString();
		this.far = in.readString();
		this.like = (Boolean) in.readValue(Boolean.class.getClassLoader());
		this.distance = in.readString();
	}

	public static final Parcelable.Creator<AccountInfomation> CREATOR = new Parcelable.Creator<AccountInfomation>() {

		@Override
		public AccountInfomation createFromParcel(Parcel arg0) {

			return new AccountInfomation(arg0);
		}

		@Override
		public AccountInfomation[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new AccountInfomation[arg0];
		}

	};

}
