package de.lh.tool;

import de.lh.tool.domain.dto.LoginDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class IntegrationTestUtil {
	private static final int TIMEOUT = 30000;
	public static final String REST_URL = "http://localhost:8080/lh-tool/rest";

	private IntegrationTestUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static void waitForLocalTomcat() {
		long timeout = System.currentTimeMillis() + TIMEOUT;
		while (System.currentTimeMillis() < timeout) {
			try {
				RestAssured.when().get(REST_URL + "/info/heartbeat").then().statusCode(200);
				return;
			} catch (Exception e) {
				System.out.println("Connect failed, waiting and trying again");
				try {
					Thread.sleep(2000);// 2 seconds
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}

	public static RequestSpecification getRequestSpecWithAdminLogin() {
		LoginDto dto = new LoginDto();
		dto.setEmail("test-admin@lh-tool.de");
		dto.setPassword("testing");
		return RestAssured.given().body(dto).contentType(ContentType.JSON);
	}

	public static RequestSpecification getRequestSpecWithJWT(String jwt) {
		return RestAssured.given().header("Authorization", "Bearer " + jwt);
	}
}
