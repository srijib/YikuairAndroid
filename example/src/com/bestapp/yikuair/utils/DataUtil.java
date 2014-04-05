package com.bestapp.yikuair.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class DataUtil {

	private final static String xform = "DES/ECB/NoPadding";
	private final static byte[] hex = "0123456789ABCDEF".getBytes();

	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		try{
			for (int i = begin; i < begin + count; i++){
				bs[i - begin] = src[i];	
			}
			return bs;
		}catch(Exception e){
			e.printStackTrace();
			return "".getBytes();
		}
	}

	public static byte[] byteArray(byte[] b, byte[] nb) {
		ByteArrayOutputStream os = null;
		DataOutputStream ds;
		try {
			os = new ByteArrayOutputStream();
			ds = new DataOutputStream(os);
			if (b != null)
				ds.write(b);
			if (nb != null)
				ds.write(nb);
			byte[] joinByte = os.toByteArray();
			ds.flush();
			ds.close();
			os.close();
			return joinByte;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean isBytesEquals(byte[] a, byte[] b) {
		int len = a.length;
		if (len != b.length)
			return false;
		for (int i = 0; i < len; i++) {
			if (a[i] != b[i])
				return false;
		}
		return true;
	}

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	public static String Bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}

	public static byte[] HexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	static byte[] padbyte(byte[] b) {
		int pad = b.length % 8;
		if (pad > 0)
			pad = 8 - pad;
		byte[] newbytes = new byte[b.length + pad];
		for (int i = 0; i < b.length; i++)
			newbytes[i] = b[i];
		for (int i = 0; i < pad; i++)
			newbytes[b.length + i] = 0;
		return newbytes;
	}

	static byte[] unpadbyte(byte[] b) {
		int pad = 0;
		for (int i = b.length - 1; i >= 0 && b[i] == 0; i--)
			pad++;
		byte[] newbytes = new byte[b.length - pad];
		for (int i = 0; i < newbytes.length; i++)
			newbytes[i] = b[i];
		return newbytes;
	}

	static byte[] encrypt(byte[] inpBytes, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		return cipher.doFinal(padbyte(inpBytes));
	}

	static byte[] decrypt(byte[] inpBytes, SecretKey key) throws Exception {
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] tmp = cipher.doFinal(padbyte(inpBytes));
		return unpadbyte(tmp);
	}

	public static byte[] encodeECBBytes(String key, String plainText)
			throws Exception {
		SecretKey secretkey = new SecretKeySpec(key.getBytes(), "DES");
		return encrypt(plainText.getBytes(), secretkey);
	}

	public static String encodeECBAsHexString(String key, String plainText)
			throws Exception {
		return Bytes2HexString(encodeECBBytes(key, plainText));
		// return ecodeBase64(encodeECBBytes(key, plainText));
	}

	public static String encodeECBAsBase64String(String key, String plainText)
			throws Exception {
		return ecodeBase64(encodeECBBytes(key, plainText));
	}

	public static String decodeECBString(String key, byte[] encryptBytes)
			throws Exception {
		SecretKey secretkey = new SecretKeySpec(key.getBytes(), "DES");
		byte[] b = decrypt(encryptBytes, secretkey);
		return new String(b);
	}

	public static String padding8(String m) {
		int mod = m.length() % 8;
		if (mod > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < (8 - mod); i++)
				sb.append(' ');
			m = sb.toString() + m;
		}
		return m;
	}

	public static String ecodeBase64(byte[] buf) {
		return (new BASE64Encoder()).encode(buf);
	}

	public static byte[] decodeBase64(String buf) {
		try {
			return (new BASE64Decoder()).decodeBuffer(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
