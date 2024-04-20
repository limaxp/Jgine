package org.jgine.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import org.jgine.net.http.controller.HttpController;
import org.jgine.utils.StringUtils;
import org.jgine.utils.logger.Logger;

public class HttpSession implements Runnable {

	private static final ThreadLocal<HttpSession> SESSION = new ThreadLocal<HttpSession>() {
		@Override
		protected HttpSession initialValue() {
			return null;
		}
	};

	public static HttpSession session() {
		return SESSION.get();
	}

	public static HttpServer server() {
		return SESSION.get().server;
	}

	public static Socket socket() {
		return SESSION.get().socket;
	}

	public final HttpServer server;
	public final Socket socket;

	public HttpSession(HttpServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void run() {
		SESSION.set(this);
		parseMessage();
	}

	private void parseMessage() {
		BufferedReader in = null;
		String url = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String input = in.readLine();
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase();
			url = parse.nextToken().toLowerCase().substring(1);
			int urlLength = url.length() - 1;
			if (urlLength > -1 && url.charAt(urlLength) == '/')
				url = url.substring(0, urlLength);

			Object[] args = null;
			if (url.contains("?")) {
				int argSeperatorIndex = url.lastIndexOf('?');
				if (argSeperatorIndex != urlLength)
					args = parseArguments(url.substring(argSeperatorIndex + 1));
				url = url.substring(0, argSeperatorIndex);
			}
			invokeController(method, url, args);
		} catch (IOException e) {
			Logger.err("Session: Error!", e);
		} finally {
			try {
				in.close();
				socket.close();
			} catch (Exception e) {
				Logger.err("Session: Error closing socket!", e);
			}
		}
	}

	public void invokeController(String method, String url, Object[] args) {
		int seperatorIndex = url.indexOf('/');
		HttpController controller;
		if (seperatorIndex != -1)
			controller = server.getController(url.substring(0, seperatorIndex));
		else
			controller = server.getController("home");
		if (controller == null)
			controller = server.getController("home");
		controller.invoke(method, url, args);
	}

	public static Object[] parseArguments(String s) {
		Object[] args = null;
		if (s.contains("&")) {
			String[] argumentSplit = s.split("&");
			args = new Object[argumentSplit.length];
			int i = 0;
			for (String argument : argumentSplit) {
				args[i++] = castArguments(argument);
			}
		} else {
			args = new Object[1];
			args[0] = s;
		}
		return args;
	}

	public static Object castArguments(String s) {
		if (StringUtils.isByte(s))
			return Byte.parseByte(s);
		else if (StringUtils.isShort(s))
			return Short.parseShort(s);
		else if (StringUtils.isInteger(s))
			return Integer.parseInt(s);
		else if (StringUtils.isLong(s))
			return Long.parseLong(s);
		else if (StringUtils.isFloat(s))
			return Float.parseFloat(s);
		else if (StringUtils.isDouble(s))
			return Double.parseDouble(s);
		return s;
	}
}
