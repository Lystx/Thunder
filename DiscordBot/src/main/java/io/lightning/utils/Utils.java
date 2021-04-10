package io.lightning.utils;

import com.google.common.base.Splitter;
import lombok.SneakyThrows;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class Utils {
	
	public static String[] splitStringToArgs(String str) {
		List<String> tokens = new LinkedList<>();
		StringBuilder sb = new StringBuilder();

		boolean insideQuote = false;

		for (char c : str.toCharArray()) {
		    if (c == '"') {
		        insideQuote = !insideQuote;
		    } else if (c == ' ' && !insideQuote) {
		    	if (sb.length() > 0) {
					tokens.add(sb.toString());
				}
		        sb.delete(0, sb.length());
		    } else {
		        sb.append(c);
		    }
		}
		tokens.add(sb.toString());
		
		return tokens.toArray(new String[0]);
	}


	@SneakyThrows
	public static String colorToString(Color color) {
		for (Field declaredField : color.getClass().getDeclaredFields()) {
			if (declaredField.get(color).equals(color)) {
				return declaredField.getName().toUpperCase();
			}
		}
		return null;
	}

	@SneakyThrows
	public static List<String> toStringList(List<?> list, String method) {
		List<String> list1 = new LinkedList<>();
		for (Object o : list) {
			final Method declaredMethod = o.getClass().getDeclaredMethod(method); declaredMethod.setAccessible(true);
			final Object invoke = declaredMethod.invoke(o);
			list1.add(String.valueOf(invoke));
		}
		return list1;
	}

	@SneakyThrows
	public static Color stringToColor(String color) {
		return (Color) Color.class.getDeclaredField(color).get(Color.class);
	}

	public static List<String> getMessageToSend(String input, int i) {
		List<String> list = new LinkedList<>();
		if (input.length() >= i) {
			Iterable<String> pieces = Splitter.fixedLength(i).split(input);
			for (String piece : pieces) {
				list.add(piece);
			}
		} else {
			list.add(input);
		}
		return list;
	}

}
