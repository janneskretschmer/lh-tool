package de.lh.tool;

import java.io.IOException;
import java.net.URL;

public class TestUtil {
	private static final int TIMEOUT = 30000;
	public static final String REST_URL = "http://localhost:8080/lh-tool/rest";

	private TestUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static void waitForLocalTomcat() {
		long timeout = System.currentTimeMillis() + TIMEOUT;
		while (System.currentTimeMillis() < timeout) {
			try {
				new URL(REST_URL).openConnection();
			} catch (IOException e) {
				System.out.println("Connect failed, waiting and trying again");
				try {
					Thread.sleep(2000);// 2 seconds
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}
}
