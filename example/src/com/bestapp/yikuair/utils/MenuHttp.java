package com.bestapp.yikuair.utils;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.bestapp.yikuair.utils.Client;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.DataUtil;
import com.bestapp.yikuair.utils.DesECBUtil;
import com.bestapp.yikuair.utils.UserInfo;

public class MenuHttp {

	public void getMenuList(String toId,
			AsyncHttpResponseHandler ResponseHandler) throws Exception {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password + "&to=" + toId;
		String afterurl = null;
		String data = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
				request.trim());
		afterurl = URLEncoder.encode(data, "utf-8");
		RequestParams params = new RequestParams();
		params.put("__", afterurl);
		DBlog.e("params", params.toString());
		Client.getMenu(params, ResponseHandler);
	}

	public ArrayList<MenuData> getMenuList(String josn) throws JSONException {
		JSONObject jsonObject = new JSONObject(josn.toString());
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		ArrayList<MenuData> list = new ArrayList<MenuHttp.MenuData>();
		for (int i = 0; i < jsonArray.length(); i++) {
			MenuData menu = new MenuData();
			JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
			menu.callbackurl = jsonObject2.getString("callbackurl");
			menu.msguuid = jsonObject2.getString("msguuid");
			menu.buttonText = new String(DataUtil.decodeBase64(jsonObject2
					.getString("buttonText")));
			list.add(menu);
		}
		return list;

	}

	public void getMenuBackInfo(String josn) throws JSONException {
		JSONObject jsonObject = new JSONObject(josn.toString());
		String token = jsonObject.getString("token");
		if (token.equals("1")) {
			String content = new String(DataUtil.decodeBase64(jsonObject
					.getString("content")));
		} else if (token.equals("2")) {
			String smallImgPath = jsonObject.getString("smallImgPath");
			String filePath = jsonObject.getString("filePath");
		} else if (token.equals("3")) {
			String filePath = jsonObject.getString("filePath");
		} else if (token.equals("4")) {
			String imgpath = jsonObject.getString("imgpath");
			String title = new String(DataUtil.decodeBase64(jsonObject
					.getString("title")));
			String content = new String(DataUtil.decodeBase64(jsonObject
					.getString("content")));
		}
	}

	public void useMenu(String toId, MenuData menuData,
			AsyncHttpResponseHandler ResponseHandler) throws Exception {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password + "&callbackurl="
				+ menuData.callbackurl + "&msguuid=" + menuData.msguuid
				+ "&id=" + menuData.msguuid + "&content=&to=" + toId;
		String afterurl = null;
		String data = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
				request.trim());
		afterurl = URLEncoder.encode(data, "utf-8");
		RequestParams params = new RequestParams();
		params.put("__", afterurl);
		DBlog.e("params", params.toString());
		Client.UseMenu(params, ResponseHandler);
	}

	public void getOffTimeMessage(AsyncHttpResponseHandler ResponseHandler)
			throws Exception {
		if (UserInfo.id == null) {
			return;
		}
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password + "&from=" + "&to=" + UserInfo.db_id;
		String afterurl = null;
		DBlog.e("ss", request);
		String data = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
				request.trim());
		afterurl = URLEncoder.encode(data, "utf-8");
		RequestParams params = new RequestParams();
		params.put("__", afterurl);
		DBlog.e("params", params.toString());
		Client.getOffTimeMessage(params, ResponseHandler);
	}

	public void sendMessageBefore(String toId, String content,
			AsyncHttpResponseHandler ResponseHandler) throws Exception {
		String request = "username="
				+ UserInfo.id
				+ "&password="
				+ UserInfo.cipher_password
				+ "&callbackurl=http%3A%2F%2Fapi.yikuair.com%2FyikuairAPI%2Fa%2Ftest%2Fcallback&msz"
				+ "&id=-1&content=" + URLEncoder.encode(DataUtil.ecodeBase64(content.getBytes()))
				+ "&to=" + toId;
		String afterurl = null;
		String data = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
				request.trim());
		afterurl = URLEncoder.encode(data, "utf-8");
		RequestParams params = new RequestParams();
		params.put("__", afterurl);
		Client.UseMenu(params, ResponseHandler);
	}

	public class MenuData {
		public String callbackurl;
		public String msguuid;
		public String buttonText;
	}
}
