package org.jgine.net.http.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jgine.net.http.annotation.Parameter;
import org.jgine.net.http.annotation.Route;
import org.jgine.net.http.annotation.Routes;

public abstract class HttpController {

	private final Map<String, RouteEntry> routeMap;

	public HttpController() {
		routeMap = new HashMap<String, RouteEntry>();
		loadMethods();
	}

	private void loadMethods() {
		for (Method method : this.getClass().getMethods()) {
			if (!method.isAnnotationPresent(Route.class) && !method.isAnnotationPresent(Routes.class))
				continue;
			RouteEntry entry = new RouteEntry(method);
			for (Route route : method.getAnnotationsByType(Route.class))
				routeMap.put(route.value().toLowerCase(), entry);
		}
	}

	public Object invoke(String method, String url) {
		RouteEntry entry = routeMap.get(url);
		if (entry == null)
			return null;
		if (!method.equals(entry.htmlMethod))
			return null;
		return entry.invoke(this);
	}

	public Object invoke(String method, String url, String args) {
		RouteEntry entry = routeMap.get(url);
		if (entry == null)
			return null;
		if (!method.equals(entry.htmlMethod))
			return null;

		int argLength = entry.getArgumentSize();
		Object[] arguments = new Object[argLength];
		int sepLast = 0;
		for (int i = 0; i < argLength - 1; i++) {
			int sep1 = args.indexOf('&', sepLast);
			int sep2 = args.indexOf('=', sepLast);
			int index = entry.getArgIndex(args, sepLast, sep2 - sepLast);
			arguments[index] = entry.cast(index, args.substring(sep2 + 1, sep1));
			sepLast = sep1 + 1;
		}
		int sep2 = args.indexOf('=', sepLast);
		int index = entry.getArgIndex(args, sepLast, sep2 - sepLast);
		arguments[index] = entry.cast(index, args.substring(sep2 + 1, args.length()));
		return entry.invoke(this, arguments);
	}

	public static class RouteEntry {

		private static final String[] EMPTY_NAMES = new String[] {};

		@SuppressWarnings("unchecked")
		private static final Function<String, ?>[] EMPTY_FUNCTIONS = new Function[] {};

		public final Method method;
		public final String htmlMethod;
		private final String[] argNames;
		private final Function<String, ?>[] castFunctions;

		@SuppressWarnings("unchecked")
		public RouteEntry(Method method) {
			this.method = method;
			if (method.isAnnotationPresent(org.jgine.net.http.annotation.Method.class))
				this.htmlMethod = method.getAnnotation(org.jgine.net.http.annotation.Method.class).value();
			else
				this.htmlMethod = "GET";

			if (!method.isAnnotationPresent(Parameter.class)) {
				argNames = EMPTY_NAMES;
				castFunctions = EMPTY_FUNCTIONS;
				return;
			}
			argNames = method.getAnnotation(Parameter.class).value();
			int parameterSize = argNames.length;
			this.castFunctions = new Function[parameterSize];
			Class<?>[] types = method.getParameterTypes();
			for (int i = 0; i < parameterSize; i++)
				castFunctions[i] = getCastFunction(types[i]);
		}

		public Object invoke(HttpController controller) {
			try {
				return method.invoke(controller);
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}

		public Object invoke(HttpController controller, Object[] args) {
			try {
				return method.invoke(controller, args);
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}

		public Object cast(int index, String args) {
			return castFunctions[index].apply(args);
		}

		public String getArgName(int index) {
			return argNames[index];
		}

		public int getArgIndex(String name) {
			for (int i = 0; i < argNames.length; i++) {
				if (argNames[i].equals(name))
					return i;
			}
			return -1;
		}

		public int getArgIndex(String args, int offset, int length) {
			for (int i = 0; i < argNames.length; i++) {
				if (argNames[i].regionMatches(0, args, offset, length))
					return i;
			}
			return -1;
		}

		public int getArgumentSize() {
			return argNames.length;
		}

		public static Function<String, ?> getCastFunction(Class<?> type) {
			if (type == String.class)
				return (s) -> s;
			else if (type == Integer.class)
				return Integer::parseInt;
			else if (type == Float.class)
				return Float::parseFloat;
			else if (type == Double.class)
				return Double::parseDouble;
			else if (type == Long.class)
				return Long::parseLong;
			else if (type == Byte.class)
				return Byte::parseByte;
			else if (type == Short.class)
				return Short::parseShort;
			return (s) -> s;
		}
	}
}
