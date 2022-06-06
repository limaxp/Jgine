package org.jgine.net.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jgine.misc.utils.logger.Logger;
import org.jgine.net.http.controller.HttpController;

public class HttpServer implements Runnable {

	private ServerSocket socket;
	private boolean isRunning;
	private boolean isLogging;
	private Map<String, HttpController> controllerMap;

	public HttpServer(int port) {
		try {
			this.socket = new ServerSocket(port);
			this.socket.setSoTimeout(1000);
		} catch (IOException e) {
			Logger.err("HttpServer: Error creating socket!", e);
		}
		controllerMap = new HashMap<String, HttpController>();
	}

	public void stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			Socket socket = null;
			try {
				socket = this.socket.accept();
			} catch (SocketTimeoutException e) {
				// ignore
			} catch (IOException e) {
				Logger.err("HttpServer: Error listening for packets!", e);
			}
			if (socket != null)
				handleConnection(socket);
		}

		try {
			socket.close();
		} catch (IOException e) {
			Logger.err("HttpServer: Error closing socket!", e);
		}
	}

	private void handleConnection(Socket socket) {
		HttpSession session = new HttpSession(this, socket);
		if (isLogging)
			Logger.log("HttpServer: Connection(" + new Date() + ")" + socket.getInetAddress() + ":"
					+ socket.getLocalPort());
		new Thread(session).start();
	}

	public void registerController(HttpController controller) {
		controllerMap.put(controller.getClass().getSimpleName().replace("Controller", "").toLowerCase(), controller);
	}

	public void unregisterController(HttpController controller) {
		controllerMap.remove(controller.getClass().getSimpleName().replace("Controller", "").toLowerCase());
	}

	public HttpController getController(String name) {
		return controllerMap.get(name);
	}

	public InetAddress getIp() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getLocalPort();
	}

	public void setLogging(boolean log) {
		isLogging = log;
	}

	public boolean isLogging() {
		return isLogging;
	}
}
