package org.jgine.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import org.jgine.misc.utils.StringUtils;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.http.controller.HttpController;

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
		return SESSION.get().getServer();
	}

	public static Socket socket() {
		return SESSION.get().socket;
	}

	private final HttpServer server;
	private final Socket socket;

	public HttpSession(HttpServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void run() {
		SESSION.set(this);
		BufferedReader in = null;
		String url = null;
		String[] urlSplit = null;
		Object[] args = null;

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String input = in.readLine();
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase();
			url = parse.nextToken().toLowerCase();
			url = url.replaceFirst("/", "");
			int urlLength = url.length() - 1;
			if (urlLength > -1 && url.charAt(urlLength) == '/')
				url = url.substring(0, urlLength);
			urlSplit = url.split("/");

			if (url.contains("?")) {
				String[] urlSplit2 = url.split("\\?");
				url = urlSplit2[0];
				String argumentString = urlSplit2[1];
				if (argumentString.contains("&")) {
					String[] argumentSplit = argumentString.split("&");
					args = new Object[argumentSplit.length];
					int i = 0;
					for (String argument : argumentSplit) {
						args[i++] = castObjectType(argument);
					}
				} else {
					args = new Object[1];
					args[0] = argumentString;
				}
			}

			HttpController controller;
			if (urlSplit.length > 1)
				controller = server.getController(urlSplit[0]);
			else
				controller = server.getController("home");
			if (controller == null)
				controller = server.getController("home");
			controller.invoke(method, url, args);

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

	public HttpServer getServer() {
		return server;
	}

	public Socket getSocket() {
		return socket;
	}

	private static Object castObjectType(String str) {
		if (StringUtils.isDouble(str))
			return Double.parseDouble(str);
		else if (StringUtils.isFloat(str))
			return Float.parseFloat(str);
		else if (StringUtils.isInteger(str))
			return Integer.parseInt(str);
		else if (StringUtils.isShort(str))
			return Short.parseShort(str);
		else if (StringUtils.isByte(str))
			return Byte.parseByte(str);
		return str;
	}

}
