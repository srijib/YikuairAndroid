package com.bestapp.yikuair.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtils {

	public static InputStream getStreamFromURL(String imageURL) {

		InputStream in = null;
		try {

			URL url = new URL(imageURL);

			URLConnection openConnection = url.openConnection();

			if (openConnection != null && openConnection.getDate() > 0) {
				HttpURLConnection connection = (HttpURLConnection) openConnection;
				in = connection.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;

	}

}
