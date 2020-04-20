package de.lh.tool.service.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class UrlMappingsTest {
	private final static String URL_MAPPINGS_JS_PATH = "src/main/js/urlmappings.js";

	@Test
	public void testIfInSync() throws FileNotFoundException, IllegalArgumentException, IllegalAccessException {
		Map<String, String> jsConsts = new HashMap<>();
		try (Scanner scanner = new Scanner(new File(URL_MAPPINGS_JS_PATH));) {
			Pattern pattern = Pattern.compile("^export const (\\S+) = (.*?);*$");
			String line = scanner.nextLine().trim();
			while (scanner.hasNextLine()) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					String key = matcher.group(1);
					jsConsts.put(key, matcher.group(2));
					line = scanner.nextLine().trim();
					while (line.startsWith("+")) {
						jsConsts.put(key, jsConsts.get(key) + line.replace(";", ""));
						line = scanner.nextLine().trim();
					}
				} else {
					line = scanner.nextLine().trim();
				}
			}
		}

		Map<String, String> javaConsts = new HashMap<>();
		for (Field field : UrlMappings.class.getFields()) {
			javaConsts.put(field.getName(), (String) field.get(null));
		}

		jsConsts.entrySet().stream().forEach(e -> assertEquals(javaConsts.remove(e.getKey()),
				getConstValue(jsConsts, e.getKey()), e.getKey() + " is different"));

		assertEquals(0, javaConsts.size(), StringUtils.join(javaConsts.keySet(), ", ") + " are missing in js file");
	}

	private String getConstValue(Map<String, String> jsConsts, String variableName) {
		StringBuilder sb = new StringBuilder();
		for (String tmp : jsConsts.get(variableName).replaceAll("\\s", "").split("\\+")) {
			if (tmp.startsWith("'")) {
				sb.append(tmp.replace("'", ""));
			} else {
				sb.append(getConstValue(jsConsts, tmp));
			}
		}
		return sb.toString();
	}
}
