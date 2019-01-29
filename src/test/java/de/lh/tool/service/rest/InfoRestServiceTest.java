package de.lh.tool.service.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.lh.tool.service.rest.InfoRestService;

public class InfoRestServiceTest {
	private InfoRestService infoRestService;

	@BeforeEach
	public void before() {
		infoRestService = new InfoRestService();
	}

	@Test
	public void testHeartbeat() {
		assertEquals(true, infoRestService.heartbeat());
	}

}
