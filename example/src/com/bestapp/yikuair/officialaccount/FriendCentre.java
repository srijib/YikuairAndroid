package com.bestapp.yikuair.officialaccount;

import java.util.ArrayList;

import com.bestapp.yikuair.utils.AccountInfomation;


public class FriendCentre {

	private ArrayList<AccountInfomation> list;

	public ArrayList<AccountInfomation> getPeopleList() {
		return null;
	}

	public boolean isLikeMe(int id) {

		return list.get(id).isLike();
	}
}
