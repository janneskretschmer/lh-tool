package de.lh.tool.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.lh.tool.TestUtil;
import io.restassured.RestAssured;

public class InfoIT {

	@BeforeAll
	public static void waitForServer() {
		TestUtil.waitForLocalTomcat();
	}

	@Test
	public void testHeartbeat() throws IOException {
		String url = TestUtil.REST_URL + "/info/heartbeat";
		assertTrue(Boolean.parseBoolean(RestAssured.get(url).asString()));
	}
}
