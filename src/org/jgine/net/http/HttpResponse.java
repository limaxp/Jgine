package org.jgine.net.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.jgine.utils.FileUtils;
import org.jgine.utils.logger.Logger;

public class HttpResponse {

	public static final String ROOT = System.getProperty("user.dir");
	public static final String VIEW_ROOT = "./view";

	public static void send(String httpResponse, String contentType, byte[] data) {
		Socket connect = HttpSession.socket();
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		int dataLength = data.length;
		try {
			out = new PrintWriter(connect.getOutputStream());
			writeHTMLHeader(out, httpResponse, contentType, dataLength);
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			dataOut.write(data, 0, dataLength);
			dataOut.flush();
		} catch (IOException e) {
			Logger.err("Response: Error", e);
		} finally {
			try {
				out.close();
				dataOut.close();
			} catch (Exception e) {
				Logger.err("Response: Error closing stream!", e);
			}
		}
	}

	public static void sendFile(File file) {
		sendFile("200 OK", file);
	}

	public static void sendFile(String httpResponse, File file) {
		Socket connect = HttpSession.socket();
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		int fileLength = (int) file.length();
		try {
			out = new PrintWriter(connect.getOutputStream());
			writeHTMLHeader(out, httpResponse, Files.probeContentType(file.toPath()), fileLength);
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			Files.copy(file.toPath(), dataOut);
			dataOut.flush();
		} catch (FileNotFoundException e) {
			Logger.err("Response: file " + file.getName() + " not found!", e);
			sendPage("404.html", "404 File Not Found");
		} catch (IOException e) {
			Logger.err("Response: Error", e);
		} finally {
			try {
				out.close();
				dataOut.close();
			} catch (Exception e) {
				Logger.err("Response: Error closing stream!", e);
			}
		}
	}

	private static void writeHTMLHeader(PrintWriter out, String httpResponse, String contentType, int dataLength) {
		out.println("HTTP/1.1 " + httpResponse);
		out.println("Server: Java HTTP Server");
		out.println("Date: " + new Date());
		out.println("Content-type: " + contentType);
		out.println("Content-length" + dataLength);
		out.println();
		out.flush();
	}

	public static void sendResource(String contentType, String resource) {
		sendResource("200 OK", contentType, resource);
	}

	public static void sendResource(String httpResponse, String contentType, String resource) {
		File resourceFile = new File(resource);
		if (resourceFile.exists())
			sendFile(httpResponse, resourceFile);
		else {
			File jar = new File(ROOT + "/AIOSTHttpServer.jar");
			if (jar.exists()) {
				JarEntry entry;
				System.out.println(resource);
				try (JarInputStream is = new JarInputStream(new FileInputStream(jar));
						JarFile jarFile = new JarFile(jar)) {
					while ((entry = is.getNextJarEntry()) != null) {
						System.out.println(entry.getName());
						if (entry.getName().equals(resource)) {
							System.out.println("found");
							try (InputStream in = jarFile.getInputStream(entry)) {
								send(httpResponse, contentType, FileUtils.readBytes(in));
							}
							return;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void sendPage(String page, String httpResponse) {
		sendResource(httpResponse, getContentType(page), VIEW_ROOT + "/" + page);
	}

	public static void sendPage(String page) {
		sendPage(page, "200 OK");
	}

	public static void sendInt(int i) {
		send("200 OK", "text/html", Integer.toString(i).getBytes());
	}

	public static void sendShort(short s) {
		send("200 OK", "text/html", Short.toString(s).getBytes());
	}

	public static void sendByte(byte b) {
		send("200 OK", "text/html", new byte[] { b });
	}

	public static void sendString(String s) {
		send("200 OK", "text/html", s.getBytes());
	}

	private static byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} catch (Exception e) {

		} finally {
			if (fileIn != null)
				fileIn.close();
		}
		return fileData;
	}

	private static String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}
}
