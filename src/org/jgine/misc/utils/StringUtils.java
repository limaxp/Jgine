package org.jgine.misc.utils;

import java.text.NumberFormat;

public class StringUtils {

	public static boolean isInteger(String s, int radix) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}

	public static boolean isInteger(String str) {
		// if(str.matches("-?\\d+(\\.\\d+)?"))
		if (str.matches("^-?\\d{1,8}$"))
			return true;
		return false;
	}

	public static boolean isDouble(String str) {
		// if(str.matches("^-?\\d{1,23}\\.\\d{1,8}$"))
		if (str.matches("(?=^.{1,30}$)^-?\\d{1,23}\\.\\d{1,8}$"))
			return true;
		return false;
	}

	// probably wrong!
	public static boolean isFloat(String str) {
		if (str.matches("^-?\\d{1,9}\\.\\d{1,9}$"))
			return true;
		return false;
	}

	public static boolean isShort(String str) {
		if (str.matches("^-?\\d{1,4}$"))
			return true;
		return false;
	}

	public static boolean isByte(String str) {
		if (str.matches("^-?\\d{1,2}$"))
			return true;
		return false;
	}

	public static String formatNumbers(String str) {
		StringBuffer res = new StringBuffer();
		int eIndex = Integer.MIN_VALUE;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == 'E') {
				eIndex = i;
			}
			else if (c == ' ' && eIndex == i - 1) {
				// workaround Java 1.4 DecimalFormat bug
				res.append('+');
				continue;
			}
			else if (Character.isDigit(c) && eIndex == i - 1) {
				res.append('+');
			}
			res.append(c);
		}
		return res.toString();
	}

	public static String format(double number, NumberFormat format) {
		if (Double.isNaN(number)) {
			return padLeft(format, " NaN");
		}
		else if (Double.isInfinite(number)) {
			return padLeft(format, number > 0.0 ? " +Inf" : " -Inf");
		}
		return format.format(number);
	}

	private static String padLeft(NumberFormat format, String str) {
		int len = format.format(0.0).length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len - str.length() + 1; i++) {
			sb.append(" ");
		}
		return sb.append(str).toString();
	}
}
