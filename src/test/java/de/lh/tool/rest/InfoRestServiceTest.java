package de.lh.tool.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InfoRestServiceTest {
	InfoRestService infoRestService;

	@BeforeEach
	public void before() {
		infoRestService = new InfoRestService();
	}

	@Test
	public void testHeartbeat() {
		assertEquals(true, infoRestService.heartbeat());
	}

}
