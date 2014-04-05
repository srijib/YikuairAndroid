package com.bestapp.yikuair.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Client {
	private static final String BASE_URL = "http://api.yikuair.com/yikuairAPI/a/star/list?";
	private static final String Menu_URL = "http://api.yikuair.com/yikuairAPI/a/star/button/list?";
	private static final String Menu_USE_URL = "http://api.yikuair.com/yikuairAPI/a/star/button/message?";
	private static final String SUB_USE_URL = "http://api.yikuair.com/yikuairAPI/a/star/attention/list?";
	private static final String OFF_LINE_MESSAGE = "http://api.yikuair.com/yikuairAPI/a/message/list?";
	private static final String PHOTO_URL = "http://api.yikuair.com/yikuairAPI/a/tinder/user?";
	private static final String UPLOADING_PHOTO_URL = "http://api.yikuair.com/yikuairAPI/a/tinder/upload/header";
	private static final String UPLOADING_Loaction = "http://api.yikuair.com/yikuairAPI/a/tinder/upload/location?";
	private static final String LOADING_PEOPLE_URL = "http://api.yikuair.com/yikuairAPI/a/tinder/friends?";
	private static final String LIKE_OR_NOT_URL = "http://api.yikuair.com/yikuairAPI/a/tinder/like?";
	private static final String FRIEND_URL = "http://api.yikuair.com/yikuairAPI/a/tinder/match?";
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(AsyncHttpResponseHandler responseHandler) {

		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password.trim() + "&com_id="
				+ UserInfo.companyId;
		String afterurl = null;
		try {
			String data = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
					request.trim());

			afterurl = URLEncoder.encode(data, "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestParams params = new RequestParams();
		params.put("__", afterurl);
		String urlString = BASE_URL + params.toString();
		DBlog.e("url", urlString);
		client.get(urlString, responseHandler);

	}

	public static void getSub(AsyncHttpResponseHandler responseHandler) {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password.trim() + "&com_id="
				+ UserInfo.companyId + "&id=" + UserInfo.db_id;
		String afterurl_sub = null;
		try {
			String data_sub = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
					request.trim());
			afterurl_sub = URLEncoder.encode(data_sub, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestParams params_sub = new RequestParams();
		params_sub.put("__", afterurl_sub);
		String urlString = SUB_USE_URL + params_sub.toString();
		DBlog.e("url", urlString);
		client.get(urlString, responseHandler);

	}

	public static void getMenu(RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		String urlString = Menu_URL + params.toString();
		DBlog.e("url", urlString);
		client.get(urlString, responseHandler);
	}

	public static void UseMenu(RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		String urlString = Menu_USE_URL + params.toString();
		DBlog.e("url", urlString);
		client.get(urlString, responseHandler);
	}

	public static void getOffTimeMessage(RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		String urlString = OFF_LINE_MESSAGE + params.toString();
		DBlog.e("sdasdasdasd", urlString);
		client.get(urlString, responseHandler);
	}

	public static void getPhoto(AsyncHttpResponseHandler responseHandler) {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password.trim() + "&id=" + UserInfo.db_id;
		String afterurl_sub = null;
		try {
			String data_sub = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
					request.trim());
			afterurl_sub = URLEncoder.encode(data_sub, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestParams params_sub = new RequestParams();
		params_sub.put("__", afterurl_sub);
		String urlString = PHOTO_URL + params_sub.toString();
		DBlog.e("url", urlString);
		client.get(urlString, responseHandler);
	}

	public static void upLoadingLocation(String lon, String lan,
			AsyncHttpResponseHandler responseHandler) {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password.trim() + "&id=" + UserInfo.db_id
				+ "&lon=" + lon + "&lan=" + lan;
		String afterurl_sub = null;
		try {
			String data_sub = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
					request.trim());
			afterurl_sub = URLEncoder.encode(data_sub, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestParams params_sub = new RequestParams();
		params_sub.put("__", afterurl_sub);
		String urlString = UPLOADING_Loaction + params_sub.toString();
		DBlog.e("url", urlString);
		client.get(urlString, responseHandler);
	}

	public static void loadingPeople(String lon, String lan,
			AsyncHttpResponseHandler responseHandler) {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password.trim() + "&id=" + UserInfo.db_id
				+ "&lon=" + lon + "&lan=" + lan;
		String afterurl_sub = null;
		try {
			String data_sub = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
					request.trim());
			afterurl_sub = URLEncoder.encode(data_sub, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestParams params_sub = new RequestParams();
		params_sub.put("__", afterurl_sub);
		String urlString = LOADING_PEOPLE_URL + params_sub.toString();
		DBlog.e("url_loadingPeople", urlString);
		client.get(urlString, responseHandler);
	}

	public static void test(byte[] data) throws ClientProtocolException,
			IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(UPLOADING_PHOTO_URL);
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("id", new StringBody(UserInfo.db_id));
		reqEntity.addPart("username", new StringBody(UserInfo.id));
		reqEntity.addPart("sex", new StringBody(UserInfo.sex));
		reqEntity.addPart("password",
				new StringBody(UserInfo.cipher_password.trim()));
		Log.e("==========", UserInfo.nick_name);

		ByteArrayBody byteBoady = new ByteArrayBody(data, "photo.jpg");
		reqEntity.addPart("upload", byteBoady);
		postRequest.setEntity(reqEntity);
		reqEntity.addPart("nickname", new StringBody(UserInfo.nick_name,
				Charset.forName("utf-8")));
		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		String sResponse;
		StringBuilder s = new StringBuilder();
		while ((sResponse = reader.readLine()) != null) {
			s = s.append(sResponse);
		}
		try {
			JSONObject object = new JSONObject(s.toString());
			if (object.has("message")) {
				if (object.getString("message").trim().equals("success")) {
					JSONObject object1 = object.getJSONObject("data");
					if (object1.has("headurl")) {
						UserInfo.nick_url = object1.getString("headurl");
					}

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.e("tag", s.toString());
	}

	public static void likeOrNot(String toId, String isLike,
			AsyncHttpResponseHandler responseHandler) {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password.trim() + "&id=" + UserInfo.db_id
				+ "&to=" + toId + "&like=" + isLike;
		String afterurl_sub = null;
		try {
			String data_sub = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
					request.trim());
			afterurl_sub = URLEncoder.encode(data_sub, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestParams params_sub = new RequestParams();
		params_sub.put("__", afterurl_sub);
		String urlString = LIKE_OR_NOT_URL + params_sub.toString();
		DBlog.e("url_loadingPeople", urlString);
		client.get(urlString, responseHandler);
	}

	public static void loadingMatchFriend(
			AsyncHttpResponseHandler responseHandler) {
		String request = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password.trim() + "&id=" + UserInfo.db_id;
		String afterurl_sub = null;
		try {
			String data_sub = DataUtil.encodeECBAsBase64String(DesECBUtil.key,
					request.trim());
			afterurl_sub = URLEncoder.encode(data_sub, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestParams params_sub = new RequestParams();
		params_sub.put("__", afterurl_sub);
		String urlString = FRIEND_URL + params_sub.toString();
		DBlog.e("url_loadingPeople", urlString);
		client.get(urlString, responseHandler);
	}
}
