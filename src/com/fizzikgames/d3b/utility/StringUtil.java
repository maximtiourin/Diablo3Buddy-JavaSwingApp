package com.fizzikgames.d3b.utility;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Offers string utility functions for manipulating or encrypting strings
 * @author Maxim Tiourin
 */
public class StringUtil {
	public StringUtil() {
		
	}
	
	/**
	 * Returns the string between starting position and rest of string
	 * @param s string to take substring from
	 * @param start position of this string exclusive
	 * @return substring
	 */
	public static String substring(String s, String start, boolean optimized) {
		String newString;
		if (optimized) {
			newString = new String(s.substring(s.indexOf(start) + start.length()));
		}
		else {
			newString = s.substring(s.indexOf(start) + start.length());
		}
		return newString;
	}
	
	/**
	 * Returns the string between two strings
	 * @param s string to take substring from
	 * @param start position of this string exclusive
	 * @param end position of this string exclusive
	 * @return substring
	 */
	public static String substring(String s, String start, String end, boolean optimized) {
		String newString;
		if (optimized) {
			newString = new String(s.substring(s.indexOf(start) + start.length(), s.indexOf(end)));
		}
		else {
			newString = s.substring(s.indexOf(start) + start.length(), s.indexOf(end));
		}
		return newString;
	}
	
	/**
	 * Returns the string between two strings
	 * @param s string to take substring from
	 * @param start position of this string inclusive
	 * @param end position of this string exclusive
	 * @return substring
	 */
	public static String substring(String s, int start, int end, boolean optimized) {
		String newString;
		if (optimized) {
			newString = new String(s.substring(start, end));
		}
		else {
			newString = s.substring(start, end);
		}
		return newString;
	}
	
	/**
	 * Returns a String array with index 0 is the String before the parameter String,
	 * and index 1 is the rest of the String after the parameter String. So if a String was 
	 * "All work and no play", and the parameter String was " ", then this function would return
	 * String[0] = "All", and String[1] = "work and no play".
	 * @param s string to take substring from
	 * @param pivot the string to trim from the two sides of the string
	 * @return substring
	 */
	public static String[] trimSubstring(String s, String pivot, boolean optimized) {
		String stra[] = new String[2];
		int pos = s.indexOf(pivot);
		
		if (pos == -1) return null;
		
		stra[0] = substring(s, 0, pos, optimized);
		stra[1] = substring(s, pos + pivot.length(), s.length(), optimized);
		return stra;
	}
	
	/**
	 * Finds and returns the position of the first occurence of 
	 * the one character string before the given position inclusively.
	 * @param src the source string
	 * @param c the character to search for
	 * @param pos the position to backtrack from
	 * @return int
	 */
	public static int firstOccurenceBeforePos(String src, String c, int pos) {
		int i = pos;
		
		while ((i < src.length()) && (i > 0) && (src.charAt(i) != c.charAt(0))) {
			i--;
		}
		
		return i;
	}
	
	/**
	 * Returns the md5 hash of a source string's bytes using UTF-8 character set
	 * @param src the source string
	 * @return the md5 has of the source string
	 */
	public static String md5(String src) {
		try {
			MessageDigest crypto = MessageDigest.getInstance("MD5");
			byte[] bytes = src.getBytes("UTF-8");
			byte[] digest = crypto.digest(bytes);
			BigInteger bigint = new BigInteger(1, digest);
			String result = bigint.toString(16);
			System.out.println(src + " = " + result);
			System.out.println(result.length() + "");
			crypto.reset();
			
			return result;
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
