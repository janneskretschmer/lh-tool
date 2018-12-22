package de.lh.tool.util;

public class StringUtil {

	public static boolean constantTimeEquals(String strA, String strB) {
		if (strA == null && strB == null) {
			return true;
		}
		if (strA == null || strB == null || strA.length() != strB.length()) {
			return false;
		}

		byte[] a = strA.getBytes();
		byte[] b = strB.getBytes();

		int result = 0;
		for (int i = 0; i < a.length; i++) {
			result |= a[i] ^ b[i];
		}
		return result == 0;
	}
}
