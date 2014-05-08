package com.bestapp.yikuair.utils;

import java.util.HashMap;
import java.util.Timer;

import com.bestapp.yikuair.fragments.ClientSocket;


public class UserInfo {
	public static boolean isFinishProcess = false;
	public static Timer timer = null;
	public static String LocalphotoPath;
	public static String password;
	public static String headUrl;
	public static String userName;
	public static String user_email;
	public static String push_userId;
	public static String push_channelId;
	public static String user_password;
	public static String cipher_password;
	public static String departmentName;
	public static String team;
	public static String signature;
	public static String mobile;
	public static String db_id;
	public static String sex;
	public static String realName;
	public static String duty;
	public static String id;
	public static String companyId;
	public static String feedback_dbId;
	public static String companyNews_dbId;
	public static boolean isHomePressed = false;
	public static boolean isSendBroadCast = true;
	public static boolean isRecreateConnection = false;
	public static int screenWidth;
	public static int screenHeight;
	public static String token = "00-21-cc-b5-63-2c";
	public static final String key = "_yikuair";
	public static final String modifyPasswordUrl = "http://192.168.1.2:8080/yikuairAPI/a/user/repwd?__=";
	public static final String getGroupMemberUrl = "http://192.168.1.2:8080/yikuairAPI/a/group/member?__=";

	public static final String requestUrl = "http://192.168.1.2:8080/yikuairAPI/a/user/login?__=";
	public static final String downloadImgUrl = "192.168.1.2:8080/yikuairAPI/static";
	public static final String downloadVoiceUrl = "192.168.1.2:8080/yikuairAPI/static";
	public static final String uploadHeaderUrl = "http://192.168.1.2:8080/yikuairAPI/a/upload/header";

	public static final String uploadRequestUrl = "http://192.168.1.2:8080/yikuairAPI/a/upload/file";
	public static final String addTaskUrl = "http://192.168.1.2:8080/yikuairAPI/a/task/add?__=";
	public static final String getTaskListUrl = "http://192.168.1.2:8080/yikuairAPI/a/task/list?__=";
	public static final String getContactUrl = "http://192.168.1.2:8080/yikuairAPI/a/user/list?__=";
	public static final String getSearchResultUrl = "http://192.168.1.2:8080/yikuairAPI/a/user?__=";
	public static final String updateSuccess = "UPDATESUCCESS";
	public static ClientSocket clientsocket;
	public static String nick_name;
	public static HashMap<String, Boolean> urlMap = new HashMap<String, Boolean>();

	public static String mac_address;
	public static boolean isFirstLogin = false;
	public static boolean isLogin = false;
	public static boolean isExit = false;
	public static boolean isDefaultUserFeedbackForMenu = true;
	public static boolean isDefaultCompanyNewsForMenu = true;
	public static String nick_url;
	public static double lon;
	public static double lan;
}
