package com.yokogawa.radiquest.ris.servlet;

/**
 * CSSの色変換クラス
 *
 * @author kuroyama
 */
public class CSSColorValueConverter {
	/**
	 * @param value 10進数色値文字列
	 * @return CSS色値(#xxxxxx)
	 */
	public static String convertFromDecimal(String value) {
		int numValue = 0;
		try {
			numValue = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			numValue = 0;
		}

		return convertFromDecimal(numValue);
	}

	/**
	 * @param value 10進数色値
	 * @return CSS色値(#xxxxxx)
	 */
	public static String convertFromDecimal(int value) {
		String hexValue = String.format("#%06x", value);
		String blue = hexValue.substring(1, 3);
		String red = hexValue.substring(5, hexValue.length());
		String green = hexValue.substring(3, 5);
		hexValue = "#"+red + green + blue;

		return hexValue;
	}

	public static void main(String[] args) {
		System.out.println("12615808 -> " + convertFromDecimal("12615808"));
		System.out.println("ABCDE -> " + convertFromDecimal("ABCDE"));
		System.out.println("65418 -> " + convertFromDecimal("65418"));
		System.out.println("6541811 -> " + convertFromDecimal("6541811"));
	}
}
