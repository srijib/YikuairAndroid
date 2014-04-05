package com.bestapp.yikuair.utils;

import java.text.SimpleDateFormat;
import java.util.Random;

public class StringWidthWeightRandom {
	private int length;
	private char[] chars;
	private Random random = new Random();
	private final static int UUID_LENGTH = 36;
	public StringWidthWeightRandom(char[] chars, int length) {
		this.chars = chars;
		this.length = length;
	}
	
	public static String getNextString(){	
		SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		Random random = new Random();
		char[] chars = (UserInfo.mac_address + date).toCharArray();
		char[] data = new char[UUID_LENGTH];
		
		for(int i = 0;i < UUID_LENGTH; i++){
			int index = random.nextInt(chars.length);
			data[i] = chars[index];
		}
		String s = new String(data);
		return s;
	}

	
}
