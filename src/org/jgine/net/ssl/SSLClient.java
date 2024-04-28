package org.jgine.net.ssl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import maxLibs.utils.logger.Logger;

public class SSLClient implements Runnable {

	protected Socket socket;
	protected boolean isRunning;
	private boolean isLogging;

	public SSLClient() {
	}

	public void createSocket(String ip, int port) {
		try {
			this.socket = new Socket(InetAddress.getByName(ip), port);
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
			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				out.append("Does it work?");
				out.newLine();
				out.flush();

				String msg;
				while ((msg = in.readLine()) != null) {
					System.out.println("Server: " + msg);
				}
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
