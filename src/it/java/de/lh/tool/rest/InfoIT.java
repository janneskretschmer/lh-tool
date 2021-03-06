package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

public class InfoIT extends BasicRestIntegrationTest {

	@Test
	public void testHeartbeat() throws IOException {
		String url = REST_URL + "/info/heartbeat";
		assertTrue(Boolean.parseBoolean(RestAssured.get(url).asString()));
	}

	@Test
	public void testTimezone() throws IOException {
		String url = REST_URL + "/info/timezone";
		assertEquals(0, Integer.parseInt(RestAssured.get(url).asString()));
	}
}
