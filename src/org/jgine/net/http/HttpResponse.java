package org.jgine.net.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;

import maxLibs.utils.logger.Logger;

public class HttpResponse {

	public static final String HTML = "text/html";
	public static final String CSS = "text/css";
	public static final String JS = "text/js";

	public static void send(String contentType, byte[] data) {
		send("200 OK", contentType, data);
	}

	public static void send(String httpResponse, String contentType, byte[] data) {
		Socket socket = HttpSession.socket();
		PrintWriter out = null;
		OutputStream os = null;
		int dataLength = data.length;
		try {
			out = new PrintWriter(socket.getOutputStream());
			writeHTMLHeader(out, httpResponse, contentType, dataLength);
			os = new BufferedOutputStream(socket.getOutputStream());
			os.write(data, 0, dataLength);
			os.flush();
		} catch (IOException e) {
			Logger.err("HttpResponse: Error", e);
		} finally {
			try {
				out.close();
				os.close();
			} catch (Exception e) {
				Logger.err("HttpResponse: Error closing stream!", e);
			}
		}
	}

	public static void sendFile(File file) {
		sendFile("200 OK", file);
	}

	public static void sendFile(String httpResponse, File file) {
		Socket socket = HttpSession.socket();
		PrintWriter out = null;
		OutputStream os = null;
		try {
			out = new PrintWriter(socket.getOutputStream());
			writeHTMLHeader(out, httpResponse, Files.probeContentType(file.toPath()), (int) file.length());
			os = new BufferedOutputStream(socket.getOutputStream());
			Files.copy(file.toPath(), os);
			os.flush();
		} catch (FileNotFoundException e) {
			Logger.err("HttpResponse: file " + file.getName() + " not found!", e);
		} catch (IOException e) {
			Logger.err("HttpResponse: Error", e);
		} finally {
			try {
				out.close();
				os.close();
			} catch (Exception e) {
				Logger.err("HttpResponse: Error closing stream!", e);
			}
		}
	}

	public static void sendResource(String contentType, String resource) {
		sendResource("200 OK", contentType, resource);
	}

	public static void sendResource(String httpResponse, String contentType, String resource) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
		Socket socket = HttpSession.socket();
		PrintWriter out = null;
		OutputStream os = null;
		try {
			out = new PrintWriter(socket.getOutputStream());
			writeHTMLHeader(out, httpResponse, contentType, is.available());
			os = new BufferedOutputStream(socket.getOutputStream());
			is.transferTo(os);
			os.flush();
		} catch (IOException e) {
			Logger.err("HttpResponse: Error", e);
		} finally {
			try {
				out.close();
				os.close();
			} catch (Exception e) {
				Logger.err("HttpResponse: Error closing stream!", e);
			}
		}
	}

	public static void sendByte(byte b) {
		send("200 OK", "text/html", new byte[] { b });
	}

	public static void sendShort(short s) {
		send("200 OK", "text/html", Short.toString(s).getBytes());
	}

	public static void sendInt(int i) {
		send("200 OK", "text/html", Integer.toString(i).getBytes());
	}

	public static void sendLong(int l) {
		send("200 OK", "text/html", Long.toString(l).getBytes());
	}

	public static void sendFloat(float f) {
		send("200 OK", "text/html", Float.toString(f).getBytes());
	}

	public static void sendDouble(double d) {
		send("200 OK", "text/html", Double.toString(d).getBytes());
	}

	public static void sendString(String s) {
		send("200 OK", "text/html", s.getBytes());
	}

	public static void writeHTMLHeader(PrintWriter out, String httpResponse, String contentType, int dataLength) {
		out.println("HTTP/1.1 " + httpResponse);
		out.println("Server: HTTPServer");
		out.println("Date: " + new Date());
		out.println("Content-type: " + contentType);
		out.println("Content-length" + dataLength);
		out.println();
		out.flush();
	}
}
