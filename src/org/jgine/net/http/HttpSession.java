package org.jgine.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import maxLibs.net.http.controller.HttpController;
import maxLibs.utils.logger.Logger;

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
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String input = in.readLine();
			int postDataIndex = 0;
			String headerLine = null;
			while ((headerLine = in.readLine()).length() != 0) {
				if (headerLine.startsWith("Content-Length:"))
					postDataIndex = Integer.parseInt(headerLine, headerLine.indexOf("Content-Length:") + 16,
							headerLine.length(), 10);
			}

			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase();
			String url = parse.nextToken();
			int urlLength = url.length();
			if (urlLength > 1 && url.charAt(urlLength - 1) == '/')
				urlLength--;
			url = url.substring(1, urlLength).toLowerCase();

			if (postDataIndex > 0) {
				char[] charArray = new char[postDataIndex];
				in.read(charArray, 0, postDataIndex);
				getController(url).invoke(method, url, new String(charArray));
			}

			else {
				int argSeperatorIndex = url.lastIndexOf('?');
				if (argSeperatorIndex != -1)
					getController(url).invoke(method, url.substring(0, argSeperatorIndex),
							url.substring(argSeperatorIndex + 1));
				else
					getController(url).invoke(method, url);
			}
		} catch (IOException e) {
			Logger.err("HttpSession: Error!", e);
		} finally {
			try {
				in.close();
				socket.close();
			} catch (Exception e) {
				Logger.err("HttpSession: Error closing socket!", e);
			}
		}
	}

	public HttpController getController(String url) {
		int seperatorIndex = url.indexOf('/');
		HttpController controller;
		if (seperatorIndex != -1) {
			controller = server.getController(url.substring(0, seperatorIndex));
			if (controller == null)
				controller = server.getController("home");
		} else
			controller = server.getController("home");
		return controller;
	}
}
