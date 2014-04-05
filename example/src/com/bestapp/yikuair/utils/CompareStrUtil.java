package com.bestapp.yikuair.utils;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class CompareStrUtil {

	public static List toArrayList(String[] temp) {
		List templist = new ArrayList();

		for (int i = 0; i < temp.length; i++) {
			templist.add(temp[i]);

		}
		return templist;
	}

	public static List romove(List lista, List listb) {
		lista.removeAll(listb);
		return lista;
	}
}