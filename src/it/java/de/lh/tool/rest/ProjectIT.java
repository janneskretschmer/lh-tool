package de.lh.tool.rest;

import org.junit.jupiter.api.Test;

public class ProjectIT extends BasicRestIntegrationTest {

	@Test
	public void testProjectCreation() throws Exception {
		createTestUsers();
		deleteTestUsers();
	}
}
