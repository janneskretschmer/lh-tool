package de.lh.tool.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.lh.tool.TestUtil;

public class InfoIT {

	@BeforeAll
	public static void waitForServer() {
		TestUtil.waitForLocalTomcat();
	}

	@Test
	public void testHeartbeat() throws IOException {
		String url = TestUtil.REST_URL + "/info/heartbeat";
		URLConnection connection = new URL(url).openConnection();
		try (InputStream response = connection.getInputStream(); Scanner scanner = new Scanner(response)) {
			String responseBody = scanner.nextLine();
			assertEquals("true", responseBody);
		}
	}
}
