package de.lh.tool.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class BasicJsSyncTest<T> {

	protected void testIfInSync(String jsFilePath, Class<T> javaClass)
			throws FileNotFoundException, IllegalAccessException {
		Map<String, String> jsConsts = new HashMap<>();
		try (Scanner scanner = new Scanner(new File(jsFilePath));) {
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
		if (javaClass.isEnum()) {
			try {
				Arrays.stream((Object[]) javaClass.getDeclaredMethod("values").invoke(null)).map(Object::toString)
						.forEach(enumConstant -> javaConsts.put(enumConstant, enumConstant));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			for (Field field : javaClass.getFields()) {
				javaConsts.put(field.getName(), (String) field.get(null));
			}
		}

		jsConsts.entrySet().stream().forEach(e -> assertEquals(javaConsts.remove(e.getKey()),
				getConstValue(jsConsts, e.getKey()), e.getKey() + " is different"));

		assertEquals(0, javaConsts.size(), StringUtils.join(javaConsts.keySet(), ", ") + " are missing in js file");
	}

	protected String getConstValue(Map<String, String> jsConsts, String variableName) {
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
