package de.lh.tool.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CodeGenUtil {
	private static final String JS_BASE_PATH = "src/main/js/";

	private CodeGenUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static <T> void generateJavascriptWithConstants(Class<T> javaClass, String fileName) {
		try {
			Files.writeString(Paths.get(JS_BASE_PATH, fileName), getJavascriptConstantsDeclarations(javaClass));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| SecurityException | IOException e) {
			log.error("fail :(", e);
		}
	}

	/**
	 * public for test reasons, consider using
	 * {@link generateJavascriptWithConstants}
	 * 
	 * @param <T>
	 * @param javaClass
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static <T> String getJavascriptConstantsDeclarations(Class<T> javaClass)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder//
				.append("/////////////////////////////////////////////////////////////////////////////////////////////////\n")
				.append("//                                                                                             //\n")
				.append("//   ██████╗__██████╗_███╗___██╗_█╗_████████╗____████████╗_██████╗_██╗___██╗_██████╗██╗__██╗   //\n")
				.append("//   ██╔══██╗██╔═══██╗████╗__██║_╚╝_╚══██╔══╝____╚══██╔══╝██╔═══██╗██║___██║██╔════╝██║__██║   //\n")
				.append("//   ██║__██║██║___██║██╔██╗_██║_______██║__________██║___██║___██║██║___██║██║_____███████║   //\n")
				.append("//   ██║__██║██║___██║██║╚██╗██║_______██║__________██║___██║___██║██║___██║██║_____██╔══██║   //\n")
				.append("//   ██████╔╝╚██████╔╝██║_╚████║_______██║__________██║___╚██████╔╝╚██████╔╝╚██████╗██║__██║   //\n")
				.append("//   ╚═════╝__╚═════╝_╚═╝__╚═══╝_______╚═╝__________╚═╝____╚═════╝__╚═════╝__╚═════╝╚═╝__╚═╝   //\n")
				.append("//                                                                                             //\n")
				.append("/////////////////////////////////////////////////////////////////////////////////////////////////\n")
				.append("\n// This file got generated from ").append(javaClass.getSimpleName()).append(".java\n\n");
		if (javaClass.isEnum()) {
			Arrays.stream((Object[]) javaClass.getDeclaredMethod("values").invoke(null)).map(Object::toString)
					.forEach(enumConstant -> appendConstant(stringBuilder, enumConstant, enumConstant));
		} else {
			for (Field field : javaClass.getFields()) {
				appendConstant(stringBuilder, field.getName(), (String) field.get(null));
			}
		}

		stringBuilder.append("\n\n");
		return stringBuilder.toString();
	}

	private static void appendConstant(StringBuilder stringBuilder, String name, String value) {
		stringBuilder.append("export const ").append(name).append(" = '").append(value).append("';\n");
	}

}
