package com.bestapp.yikuair.utils;

import android.util.Log;

public  class DBlog {

	private static boolean isPrint = true;

	public static void e(String Tag, String info) {
		if (isPrint) {
			StringBuffer infos = new StringBuffer();
			infos.append(getFileLineMethod());
			infos.append(info);
			Log.e(Tag, infos.toString());
		}
	}

	public static void d(String Tag, String info) {
		if (isPrint) {
			StringBuffer infos = new StringBuffer();
			infos.append(getFileLineMethod());
			infos.append(info);
			Log.e(Tag, infos.toString());
		}
	}

	public static void i(String Tag, String info) {
		if (isPrint) {
			StringBuffer infos = new StringBuffer();
			infos.append(getFileLineMethod());
			infos.append(info);
			Log.i(Tag, infos.toString());
		}
	}

	public static void setPrintable(boolean print) {
		isPrint = print;
	}

	public static boolean isPrint() {
		return isPrint;
	}

	public static String getFileLineMethod() {
		StackTraceElement[] traces = ((new Throwable()).getStackTrace());
		StackTraceElement traceElement = traces[2];
		StringBuffer toStringBuffer = new StringBuffer("[")
				.append(traceElement.getFileName()).append(" | ")
				.append(traceElement.getLineNumber()).append(" | ")
				.append(traceElement.getMethodName()).append("()").append("]");
		return toStringBuffer.toString();
	}

	public static String _FILE_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getFileName();
	}

	public static String _FUNC_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getMethodName();
	}

	public static int _LINE_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getLineNumber();
	}
}
