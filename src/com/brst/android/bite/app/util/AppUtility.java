package com.brst.android.bite.app.util;

public class AppUtility {

	public static String parseWeekData(String noOffer) {

		String[] parsed = noOffer.split("-");
		String str = checkStringWeek(parsed[0]);

		return str;
	}

	private static String checkStringWeek(String string) {
		String output;
		String uppercase = string.toUpperCase();
		if (uppercase.contains("SUN") || uppercase.contains("MON")
				|| uppercase.contains("TUE") || uppercase.contains("WED")
				|| uppercase.contains("THU") || uppercase.contains("FRI")
				|| uppercase.contains("SAT")) {
			output = uppercase.substring(0, 3);

		} else {
			output = uppercase;
		}

		return output;
	}

	public static String parseMonthData(String noOffer) {
		String output;
		String uppercase = noOffer.toUpperCase();
		if (uppercase.contains("JAN") || uppercase.contains("FEB")
				|| uppercase.contains("MAR") || uppercase.contains("APR")
				|| uppercase.contains("JUN") || uppercase.contains("MAY")
				|| uppercase.contains("JUL") || uppercase.contains("AUG")
				|| uppercase.contains("OCT") || uppercase.contains("SEPT")
				|| uppercase.contains("NOV") || uppercase.contains("DEC")) {
			output = uppercase.substring(0, 3);

		} else {
			output = noOffer;
		}
		return output;
	}

}
