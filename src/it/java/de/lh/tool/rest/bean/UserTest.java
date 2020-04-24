package de.lh.tool.rest.bean;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTest {
	private List<String> emails;
	private HttpStatus expectedHttpCode;
	private String expectedResponse;
	private List<String> validationQueries;
	private List<EmailTest> expectedEmails;
}
