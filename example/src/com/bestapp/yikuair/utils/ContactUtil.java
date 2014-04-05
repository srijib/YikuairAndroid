package com.bestapp.yikuair.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class ContactUtil {

	private Context mContext;
	private static String requestURL;
	private static Thread getThread;
	private static String base64;
	public List<FriendEntity> contactList = new ArrayList<FriendEntity>();
	public static HashMap<String, List<FriendEntity>> departmentContactMap = new HashMap<String, List<FriendEntity>>();
	private DBOpenHelper dbOpenHelper;

	public ContactUtil(Context context) {
		mContext = context;
		dbOpenHelper = new DBOpenHelper(mContext);
	}

	public void requestContact() {

		String departmentName;
		if (UserInfo.departmentName.equals(UserInfo.team))
			departmentName = UserInfo.departmentName;
		else
			departmentName = UserInfo.departmentName + "-" + UserInfo.team;

		String userInfo = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password + "&com_id=" + UserInfo.companyId
				+ "&de_name=" + departmentName + "&id=" + UserInfo.db_id;

		Log.i("test", "comid : " + UserInfo.companyId);
		String key = UserInfo.key;
		try {
			base64 = URLEncoder.encode(
					DataUtil.encodeECBAsBase64String(key, userInfo), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestURL = UserInfo.getContactUrl + base64;

		getThread = new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet getMethod = new HttpGet(requestURL);
				Log.i("test", "url :" + requestURL);
				HttpResponse response = null;
				try {
					response = client.execute(getMethod);
					if (response != null) {
						StatusLine statusLine = response.getStatusLine();
						if (statusLine != null) {
							if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
								HttpEntity entity = response.getEntity();
								if (entity != null) {
									InputStream content = entity.getContent();
									handleEntity(content);
								}
							}
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					client.getConnectionManager().shutdown();
				}
			}

			@SuppressLint("NewApi")
			private void handleEntity(InputStream content) throws IOException {
				String result = "";
				BufferedReader in = null;
				in = new BufferedReader(new InputStreamReader(content));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				result = sb.toString();
				result = JsonFilter(result);
				getContactFromJson(result);
				content.close();
			}
		};
		getThread.start();
	}

	public static String JsonFilter(String jsonstr) {
		return jsonstr.substring(jsonstr.indexOf("{")).replace("\r\n", "\n");
	}

	@SuppressLint("NewApi")
	private void getContactFromJson(String jsonStr) {

		try {
			JSONObject jsonOBJ = new JSONObject(jsonStr).getJSONObject("data");
			JSONArray jsonContacts = jsonOBJ.getJSONArray("contacts");
			JSONObject resultCode = new JSONObject(jsonStr);
			int code = resultCode.getInt("code");

			Log.e("test", "length : " + jsonContacts.length());
			for (int i = 0; i < jsonContacts.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonContacts.opt(i));
				String phone = jsonObj.getString("phone");
				String id = jsonObj.getString("id");
				String duty = jsonObj.getString("duty");
				String realName = jsonObj.getString("realname");
				String mobile = jsonObj.getString("mobile");
				String headUrl = jsonObj.getString("headurl");
				String email = jsonObj.getString("email");
				String companyId = jsonObj.getString("com_id");
				String userName = jsonObj.getString("username");
				String alphaName = jsonObj.getString("pinyin");
				String sig = jsonObj.getString("signature");
				String sex = jsonObj.getString("sex");
				String dep = jsonObj.getString("de_name");
				String pinyin = CN2Spell(realName.replace("", " "));
				String searchIndex = realName + " " + pinyin;

				if (realName.equals(mContext.getResources().getString(
						R.string.user_feedback))) {
					UserInfo.feedback_dbId = id;
				} else if (realName.equals(mContext.getResources().getString(
						R.string.company_news))) {
					UserInfo.companyNews_dbId = id;
				}

				String department = "";
				String team = "";

				String[] str = dep.split("-");
				if (str != null && str.length > 0) {
					department = str[0];
					if (str.length > 1)
						team = str[1];
					else
						team = str[0];
				}

				contactList.add(new FriendEntity(userName, realName, phone,
						mobile, email, duty, department, headUrl, sig,
						companyId, alphaName, team, sex, id, pinyin,
						searchIndex));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateDB(contactList);
	}

	public void updateDB(List<FriendEntity> list) {
		if (list == null)
			return;
		Collections.sort(contactList, new PinyinComparator());
		for (int i = 0; i < list.size(); i++) {
			String[] strArray = new String[] { list.get(i).getId(),
					list.get(i).getRealName(), list.get(i).getPhone(),
					list.get(i).getMobile(), list.get(i).getEmail(),
					list.get(i).getDuty(), list.get(i).getDepartmentName(),
					list.get(i).getHeadUrl(), list.get(i).getSignature(),
					list.get(i).getCompanyId(), list.get(i).getAlpha(),
					list.get(i).getTeam(), list.get(i).getSex(),
					list.get(i).getDbId(), list.get(i).getPinyin(),
					list.get(i).getSearchIndex() };
			dbOpenHelper.insert(strArray);
		}
		dbOpenHelper.close();
		UserInfo.isFinishProcess = true;
	}

	public static String CN2Spell(String chinese) {
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i],
							defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString();
	}
}
