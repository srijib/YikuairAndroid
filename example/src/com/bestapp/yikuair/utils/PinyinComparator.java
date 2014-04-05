package com.bestapp.yikuair.utils;

import java.util.Comparator;

/**
 *ƴ���Ƚ���
 * 
 * @author ����
 * 
 */
public class PinyinComparator implements Comparator<FriendEntity> {
	public int compare(FriendEntity o1, FriendEntity o2) {
		String str1 = o1.getPinyin();
		String str2 = o2.getPinyin();
		return str1.compareTo(str2);
	}
}