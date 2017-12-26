package com.geotdb.compile.utils;

import java.security.MessageDigest;

public class MD5Utils {

	public static String MD5(String source) {
		String md5String = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes());
			byte[] digest = md.digest();
			md5String = hasString(digest);
		} catch (Exception e) {
		}
		return md5String;
	}

	public static String MD5(String source, int number) {
		String md5String = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes());
			byte[] digest = md.digest();
			md5String = hasString(digest);
		} catch (Exception e) {
		}
		return md5String.substring(0, number);
	}

	private static String hasString(byte[] source) {
		String has = "";
		String tmp = "";
		for (int i = 0; i < source.length; i++) {
			tmp = Integer.toHexString(source[i] & 0XFF);
			if (tmp.length() == 1) {
				has = has + "0" + tmp;
			} else {
				has = has + tmp;
			}
			/*
			 * if (i < b.length - 1) { has = has + ":"; }
			 */
		}
		return has.toUpperCase();
	}

//	public static void main(String[] args) {
//		System.out.println(MD5Utils.MD5("1111", 16));
//		System.out.println(MD5Utils.MD5("1111", 24));
//		System.out.println(MD5Utils.MD5("1111", 32));
//		System.out.println(MD5Utils.MD5("1111"));
//	}
}
