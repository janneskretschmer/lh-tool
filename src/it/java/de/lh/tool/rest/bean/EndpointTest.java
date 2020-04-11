package de.lh.tool.rest.bean;

import java.util.List;

import org.springframework.http.HttpStatus;

import io.restassured.http.Method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointTest {
	private String url;
	private List<String> initializationQueries;
	private Method method;
	private Object body;
	private List<UserTest> userTests;
	private HttpStatus httpCodeForOthers;
	private String responseForOthers;
	private List<String> validationQueriesForOthers;
}
