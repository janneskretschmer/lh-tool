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
import org.junit.jupiter.api.Test;

public class CodeGenUtilTest {

	@Test
	public void testClass() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		assertEquals(""
				+ "/////////////////////////////////////////////////////////////////////////////////////////////////\n"
				+ "//                                                                                             //\n"
				+ "//   ██████╗__██████╗_███╗___██╗_█╗_████████╗____████████╗_██████╗_██╗___██╗_██████╗██╗__██╗   //\n"
				+ "//   ██╔══██╗██╔═══██╗████╗__██║_╚╝_╚══██╔══╝____╚══██╔══╝██╔═══██╗██║___██║██╔════╝██║__██║   //\n"
				+ "//   ██║__██║██║___██║██╔██╗_██║_______██║__________██║___██║___██║██║___██║██║_____███████║   //\n"
				+ "//   ██║__██║██║___██║██║╚██╗██║_______██║__________██║___██║___██║██║___██║██║_____██╔══██║   //\n"
				+ "//   ██████╔╝╚██████╔╝██║_╚████║_______██║__________██║___╚██████╔╝╚██████╔╝╚██████╗██║__██║   //\n"
				+ "//   ╚═════╝__╚═════╝_╚═╝__╚═══╝_______╚═╝__________╚═╝____╚═════╝__╚═════╝__╚═════╝╚═╝__╚═╝   //\n"
				+ "//                                                                                             //\n"
				+ "/////////////////////////////////////////////////////////////////////////////////////////////////\n"
				+ "\n" + "// This file got generated from TestClass.java\n" + "\n"
				+ "export const TEST_CONSTANT1 = 'TEST_CONSTANT1';\n"
				+ "export const TEST_CONSTANT2 = 'TEST_CONSTANT2';\n"
				+ "export const TEST_CONSTANT1_AND_TEST_CONSTANT2_COMBINED = 'TEST_CONSTANT1_AND_TEST_CONSTANT2_COMBINED';\n"
				+ "\n" + "\n", CodeGenUtil.getJavascriptConstantsDeclarations(TestClass.class));
	}

	private class TestClass {
		public static final String TEST_CONSTANT1 = "TEST_CONSTANT1";
		public static final String TEST_CONSTANT2 = "TEST_CONSTANT2";
		@SuppressWarnings("unused")
		public static final String TEST_CONSTANT1_AND_TEST_CONSTANT2_COMBINED = TEST_CONSTANT1 + "_AND_"
				+ TEST_CONSTANT2 + "_COMBINED";
	}

	@Test
	public void testEnum() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		assertEquals(""
				+ "/////////////////////////////////////////////////////////////////////////////////////////////////\n"
				+ "//                                                                                             //\n"
				+ "//   ██████╗__██████╗_███╗___██╗_█╗_████████╗____████████╗_██████╗_██╗___██╗_██████╗██╗__██╗   //\n"
				+ "//   ██╔══██╗██╔═══██╗████╗__██║_╚╝_╚══██╔══╝____╚══██╔══╝██╔═══██╗██║___██║██╔════╝██║__██║   //\n"
				+ "//   ██║__██║██║___██║██╔██╗_██║_______██║__________██║___██║___██║██║___██║██║_____███████║   //\n"
				+ "//   ██║__██║██║___██║██║╚██╗██║_______██║__________██║___██║___██║██║___██║██║_____██╔══██║   //\n"
				+ "//   ██████╔╝╚██████╔╝██║_╚████║_______██║__________██║___╚██████╔╝╚██████╔╝╚██████╗██║__██║   //\n"
				+ "//   ╚═════╝__╚═════╝_╚═╝__╚═══╝_______╚═╝__________╚═╝____╚═════╝__╚═════╝__╚═════╝╚═╝__╚═╝   //\n"
				+ "//                                                                                             //\n"
				+ "/////////////////////////////////////////////////////////////////////////////////////////////////\n"
				+ "\n" + "// This file got generated from TestEnum.java\n" + "\n"
				+ "export const TEST_KEY1 = 'TEST_KEY1';\n" + "export const TEST_KEY2 = 'TEST_KEY2';\n"
				+ "export const TEST_KEY3 = 'TEST_KEY3';\n"
				+ "export const THIS_IS_A_SUPER_DUPER_LONG_KEY_IN_ORDER_TO_TEST_IF_LONG_KEYS_WORK = 'THIS_IS_A_SUPER_DUPER_LONG_KEY_IN_ORDER_TO_TEST_IF_LONG_KEYS_WORK';\n"
				+ "\n" + "\n", CodeGenUtil.getJavascriptConstantsDeclarations(TestEnum.class));
	}

	private enum TestEnum {
		TEST_KEY1("value"), TEST_KEY2("value"), TEST_KEY3,
		THIS_IS_A_SUPER_DUPER_LONG_KEY_IN_ORDER_TO_TEST_IF_LONG_KEYS_WORK(
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

		private TestEnum() {
		}

		private TestEnum(String value) {
		}
	}

	public static class BasicJsSyncTest<T> {

		protected boolean testIfInSync(String jsFilePath, Class<T> javaClass)
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
					e1.printStackTrace();
				}
			} else {
				for (Field field : javaClass.getFields()) {
					javaConsts.put(field.getName(), (String) field.get(null));
				}
			}

			jsConsts.entrySet().stream().forEach(
					e -> assertEquals(javaConsts.remove(e.getKey()), getConstValue(jsConsts, e.getKey()), e.getKey()
							+ " is different. Please regenerate the js file with the main method of the according class."));

			assertEquals(0, javaConsts.size(), StringUtils.join(javaConsts.keySet(), ", ")
					+ " are missing in js file. Please regenerate the js file with the main method of the according class.");
			return true;
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
}
