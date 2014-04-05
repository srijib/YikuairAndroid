package com.bestapp.yikuair.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DesECBUtil {

	public static String key = "_yikuair";

	public static String encryptDES(String encryptString, String encryptKey)
			throws Exception {
		byte[] newbyte;
		SecretKeySpec key = new SecretKeySpec(getKey(encryptKey), "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] bytes = encryptString.getBytes();
		int num = bytes.length % 8;
		if ((num) != 0) {
			newbyte = new byte[bytes.length - num + 8];
			for (int i = 0; i < newbyte.length; i++) {
				if (i > bytes.length - 1) {
					newbyte[i] = 0;
				} else {
					newbyte[i] = bytes[i];
				}
			}
		} else {
			newbyte = bytes;
		}
		byte[] encryptedData = cipher.doFinal(newbyte);
		return ConvertUtil.bytesToHexString(encryptedData);
	}

	public static byte[] getKey(String keyRule) {
		Key key = null;
		byte[] keyByte = keyRule.getBytes();
		// ����һ���յİ�λ����,Ĭ�������Ϊ0
		byte[] byteTemp = new byte[8];
		// ���û�ָ���Ĺ���ת���ɰ�λ����
		for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
			byteTemp[i] = keyByte[i];
		}
		key = new SecretKeySpec(byteTemp, "DES");
		return key.getEncoded();
	}

	public static String decryptDES(String decryptString, String decryptKey)
			throws Exception {
		SecretKeySpec key = new SecretKeySpec(getKey(decryptKey), "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte decryptedData[] = cipher.doFinal(ConvertUtil
				.hexStringToByte(decryptString));
		return new String(decryptedData);
	}

}