package org.jgine.net.ssl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.jgine.utils.logger.Logger;

public class SSLServer implements Runnable {

	protected ServerSocket socket;
	protected boolean isRunning;
	private boolean isLogging;

	public SSLServer() {
	}

	public void createSocket(int port) {
		try {
			this.socket = new ServerSocket(port);
			this.socket.setSoTimeout(1000);
		} catch (IOException e) {
			Logger.err("HttpServer: Error creating ServerSocket!", e);
		}
	}

	public void stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			Socket socket = null;
			try {
				socket = this.socket.accept();
				if (socket != null)
					handleConnection(socket);
			} catch (SocketTimeoutException e) {
				// ignore
			} catch (IOException e) {
				Logger.err("HttpServer: Error listening for packets!", e);
			}
		}

		try {
			socket.close();
		} catch (IOException e) {
			Logger.err("HttpServer: Error closing socket!", e);
		}
	}

	protected void handleConnection(Socket socket) throws IOException {
		if (isLogging)
			Logger.log("SSLServer: Connection(" + new Date() + ")" + socket.getInetAddress() + ":"
					+ socket.getLocalPort());
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String msg;
		while ((msg = in.readLine()) != null) {
			System.out.println("Client: " + msg);
			out.append("yes");
			out.newLine();
			out.flush();
		}
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

	public boolean isRunning() {
		return isRunning;
	}
}
