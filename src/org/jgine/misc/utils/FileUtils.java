package org.jgine.misc.utils;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.jgine.render.material.Image;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

/**
 * Helper class for file operations.
 */
public class FileUtils {

	public static void copy(String source, String target) throws IOException {
		copy(new File(source), new File(target));
	}

	public static void copy(Path source, Path target) throws IOException {
		copy(source.toFile(), target.toFile());
	}

	public static void copy(URL source, URL target) throws IOException {
		copy(new File(source.getFile()), new File(target.getFile()));
	}

	public static void copy(File source, File target) throws IOException {
		if (source.isDirectory())
			copyDirectory(source, target);
		else
			Files.copy(source.toPath(), target.toPath());
	}

	public static void copyDirectory(String source, String target) throws IOException {
		copyDirectory(new File(source), new File(target));
	}

	public static void copyDirectory(Path source, Path target) throws IOException {
		copyDirectory(source.toFile(), target.toFile());
	}

	public static void copyDirectory(URL source, URL target) throws IOException {
		copyDirectory(new File(source.getFile()), new File(target.getFile()));
	}

	public static void copyDirectory(File source, File target) throws IOException {
		if (!target.exists())
			target.mkdir();

		for (String f : source.list())
			copy(new File(source, f), new File(target, f));
	}

	public static void delete(final String path) throws IOException {
		delete(new File(path));
	}

	public static void delete(final Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException e) {
				return handleException(e);
			}

			private FileVisitResult handleException(final IOException e) {
				e.printStackTrace(); // replace with more robust error handling
				return FileVisitResult.TERMINATE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
				if (e != null)
					return handleException(e);
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	};

	public static void delete(final URL url) throws IOException {
		delete(new File(url.getFile()));
	}

	public static void delete(final File file) throws IOException {
		delete(file.toPath());
	}

	public static byte[] readBytes(String path) throws IOException {
		return readBytes(Paths.get(path));
	}

	public static byte[] readBytes(Path path) throws IOException {
		return Files.readAllBytes(path);
	}

	public static byte[] readBytes(final URL url) throws IOException {
		return readBytes(new File(url.getFile()));
	}

	public static byte[] readBytes(File file) throws IOException {
		return readBytes(file.toPath());
	}

	public static byte[] readBytes(InputStream is) throws IOException {
		byte[] targetArray = new byte[is.available()];
		is.read(targetArray);
		return targetArray;
	}

	public static ByteBuffer readByteBuffer(String path) throws IOException {
		return readByteBuffer(new File(path));
	}

	public static ByteBuffer readByteBuffer(Path path) throws IOException {
		return readByteBuffer(path.toFile());
	}

	public static ByteBuffer readByteBuffer(URL url) throws IOException {
		return readByteBuffer(new File(url.getFile()));
	}

	public static ByteBuffer readByteBuffer(File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file); FileChannel fc = fis.getChannel();) {
			return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		}
	}

	public static ByteBuffer readByteBuffer(InputStream is) throws IOException {
		ByteBuffer buffer;
		try (ReadableByteChannel rbc = Channels.newChannel(is)) {
			buffer = BufferUtils.createByteBuffer(is.available());
			while (true) {
				int bytes = rbc.read(buffer);
				if (bytes == -1) {
					break;
				}
				if (buffer.remaining() == 0) {
					buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
				}
			}
		}
		buffer.flip();
		return MemoryUtil.memSlice(buffer);
	}

	public static List<String> readStringList(String path, Charset encoding) throws IOException {
		return readStringList(Paths.get(path), encoding);
	}

	public static List<String> readStringList(Path path, Charset encoding) throws IOException {
		return Files.readAllLines(path, encoding);
	}

	public static List<String> readStringList(URL url, Charset encoding) throws IOException {
		return readStringList(new File(url.getFile()), encoding);
	}

	public static List<String> readStringList(File file, Charset encoding) throws IOException {
		return readStringList(file.toPath(), encoding);
	}

	public static List<String> readStringList(InputStream is, Charset encoding) throws IOException {
		List<String> stringList = new ArrayList<String>();
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding))) {
			while ((line = reader.readLine()) != null)
				stringList.add(line);
		}
		return stringList;
	}

	public static List<String> readStringList(String path) throws IOException {
		return readStringList(path, Charset.defaultCharset());
	}

	public static List<String> readStringList(Path path) throws IOException {
		return readStringList(path, Charset.defaultCharset());
	}

	public static List<String> readStringList(URL url) throws IOException {
		return readStringList(url, Charset.defaultCharset());
	}

	public static List<String> readStringList(File file) throws IOException {
		return readStringList(file, Charset.defaultCharset());
	}

	public static List<String> readStringList(InputStream is) throws IOException {
		return readStringList(is, Charset.defaultCharset());
	}

	public static String readString(String path, Charset encoding) throws IOException {
		return readString(Paths.get(path), encoding);
	}

	public static String readString(Path path, Charset encoding) throws IOException {
		return Files.readString(path, encoding);
	}

	public static String readString(URL url, Charset encoding) throws IOException {
		return readString(new File(url.getFile()), encoding);
	}

	public static String readString(File file, Charset encoding) throws IOException {
		return readString(file.toPath(), encoding);
	}

	public static String readString(InputStream is, Charset encoding) {
		try (Scanner s = new java.util.Scanner(is, encoding); Scanner s2 = s.useDelimiter("\\A")) {
			String string = s2.hasNext() ? s2.next() : "";
			s.close();
			s2.close();
			return string;
		}
	}

	public static String readString(String path) throws IOException {
		return readString(path, Charset.defaultCharset());
	}

	public static String readString(Path path) throws IOException {
		return readString(path, Charset.defaultCharset());
	}

	public static String readString(URL url) throws IOException {
		return readString(url, Charset.defaultCharset());
	}

	public static String readString(File file) throws IOException {
		return readString(file, Charset.defaultCharset());
	}

	public static String readString(InputStream is) {
		return readString(is, Charset.defaultCharset());
	}

	public static Stream<String> readStream(String path, Charset encoding) throws IOException {
		return readStream(Paths.get(path), encoding);
	}

	public static Stream<String> readStream(Path path, Charset encoding) throws IOException {
		return Files.lines(path, encoding);
	}

	public static Stream<String> readStream(URL url, Charset encoding) throws IOException {
		return readStream(new File(url.getFile()), encoding);
	}

	public static Stream<String> readStream(File file, Charset encoding) throws IOException {
		return readStream(file.toPath(), encoding);
	}

	public static Stream<String> readStream(InputStream is, Charset encoding) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding))) {
			return reader.lines();
		}
	}

	public static Stream<String> readStream(String path) throws IOException {
		return readStream(path, Charset.defaultCharset());
	}

	public static Stream<String> readStream(Path path) throws IOException {
		return readStream(path, Charset.defaultCharset());
	}

	public static Stream<String> readStream(URL url) throws IOException {
		return readStream(url, Charset.defaultCharset());
	}

	public static Stream<String> readStream(File file) throws IOException {
		return readStream(file, Charset.defaultCharset());
	}

	public static Stream<String> readStream(InputStream is) throws IOException {
		return readStream(is, Charset.defaultCharset());
	}

	public static BufferedImage readBufferedImage(String path) throws IOException {
		return readBufferedImage(new File(path));
	}

	public static BufferedImage readBufferedImage(Path path) throws IOException {
		return readBufferedImage(path.toFile());
	}

	public static BufferedImage readBufferedImage(URL url) throws IOException {
		return readBufferedImage(new File(url.getFile()));
	}

	public static BufferedImage readBufferedImage(File file) throws IOException {
		return ImageIO.read(file);
	}

	public static BufferedImage readBufferedImage(InputStream is) throws IOException {
		return ImageIO.read(is);
	}

	public static Image readImage(String path) throws IOException {
		return readImage(new File(path));
	}

	public static Image readImage(Path path) throws IOException {
		return readImage(path.toFile());
	}

	public static Image readImage(URL url) throws IOException {
		return readImage(new File(url.getFile()));
	}

	public static Image readImage(File file) throws IOException {
		return Image.read(file);
	}

	public static Image readImage(InputStream is) throws IOException {
		return Image.read(is);
	}

	public static void writeFile(String path, String data) throws FileNotFoundException {
		writeFile(new File(path), data);
	}

	public static void writeFile(Path path, String data) throws FileNotFoundException {
		writeFile(path.toFile(), data);
	}

	public static void writeFile(URL url, String data) throws FileNotFoundException {
		writeFile(new File(url.getFile()), data);
	}

	public static void writeFile(File file, String data) throws FileNotFoundException {
		try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
			out.print(data);
		}
	}

	public static void writeFile(String path, byte[] data) throws IOException {
		writeFile(new File(path), data);
	}

	public static void writeFile(Path path, byte[] data) throws IOException {
		writeFile(path.toFile(), data);
	}

	public static void writeFile(URL url, byte[] data) throws IOException {
		writeFile(new File(url.getFile()), data);
	}

	public static void writeFile(File file, byte[] data) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(data);
		}
	}

	public static void writeFile(String path, ByteBuffer data) throws IOException {
		writeFile(new File(path), data);
	}

	public static void writeFile(Path path, ByteBuffer data) throws IOException {
		writeFile(path.toFile(), data);
	}

	public static void writeFile(URL url, ByteBuffer data) throws IOException {
		writeFile(new File(url.getFile()), data);
	}

	public static void writeFile(File file, ByteBuffer data) throws IOException {
		try (FileOutputStream os = new FileOutputStream(file); FileChannel fc = os.getChannel()) {
			fc.write(data);
		}
	}

	public static void writeFile(String path, List<String> data) throws IOException {
		writeFile(new File(path), data);
	}

	public static void writeFile(Path path, List<String> data) throws IOException {
		writeFile(path.toFile(), data);
	}

	public static void writeFile(URL url, List<String> data) throws IOException {
		writeFile(new File(url.getFile()), data);
	}

	public static void writeFile(File file, List<String> data) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			for (String str : data)
				writer.write(str);
		}
	}

	public static void writeFile(String path, InputStream stream) throws IOException {
		writeFile(Paths.get(path), stream);
	}

	public static void writeFile(Path path, InputStream stream) throws IOException {
		Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void writeFile(URL url, InputStream stream) throws IOException {
		writeFile(new File(url.getFile()), stream);
	}

	public static void writeFile(File file, InputStream stream) throws IOException {
		writeFile(file.getPath(), stream);
	}

	public static void zip(String file, String target) throws IOException {
		zip(new File(file), new File(target));
	}

	public static void zip(Path file, Path target) throws IOException {
		zip(file.toFile(), target.toFile());
	}

	public static void zip(URL file, URL target) throws IOException {
		zip(new File(file.getFile()), new File(target.getFile()));
	}

	public static void zip(File file, File target) throws IOException {
		if (file.isDirectory())
			zipDir(file, target, File.separator, true);
		else
			zipFile(file, target);
	}

	public static void zipFile(String file, String target) throws IOException {
		zipFile(new File(file), new File(target));
	}

	public static void zipFile(Path file, Path target) throws IOException {
		zipFile(file.toFile(), target.toFile());
	}

	public static void zipFile(URL file, URL target) throws IOException {
		zipFile(new File(file.getFile()), new File(target.getFile()));
	}

	public static void zipFile(File file, File target) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(target); ZipOutputStream zos = new ZipOutputStream(fos)) {
			zos.putNextEntry(new ZipEntry(file.getName()));
			byte[] bytes = readBytes(file.getAbsolutePath());
			zos.write(bytes, 0, bytes.length);
			zos.closeEntry();
			zos.finish();
		}
	}

	public static void zipDir(String file, String target, String fileSeperator, boolean withSourceFolder)
			throws IOException {
		zipDir(new File(file), new File(target), fileSeperator, withSourceFolder);
	}

	public static void zipDir(Path file, Path target, String fileSeperator, boolean withSourceFolder)
			throws IOException {
		zipDir(file.toFile(), target.toFile(), fileSeperator, withSourceFolder);
	}

	public static void zipDir(URL file, URL target, String fileSeperator, boolean withSourceFolder) throws IOException {
		zipDir(new File(file.getFile()), new File(target.getFile()), fileSeperator, withSourceFolder);
	}

	public static void zipDir(File dir, File target, String fileSeperator, boolean withSourceFolder)
			throws IOException {
		String sourceName = "";
		if (withSourceFolder)
			sourceName = dir.getName() + fileSeperator;
		String sourcepath = dir.getAbsolutePath();
		int sourceLength = dir.getAbsolutePath().length();
		try (FileOutputStream fos = new FileOutputStream(target); ZipOutputStream zos = new ZipOutputStream(fos)) {
			for (String file : generateFileList(dir)) {
				file = file.substring(sourceLength + 1, file.length()).replace("\\", fileSeperator);
				zos.putNextEntry(new ZipEntry(sourceName + file));
				byte[] bytes = readBytes(sourcepath + File.separator + file);
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();
			}
			zos.finish();
		}
	}

	public static List<String> generateFileList(String file) throws IOException {
		return generateFileList(new File(file));
	}

	public static List<String> generateFileList(Path file) throws IOException {
		return generateFileList(file.toFile());
	}

	public static List<String> generateFileList(URL file) throws IOException {
		return generateFileList(new File(file.getFile()));
	}

	public static List<String> generateFileList(File file) {
		List<String> fileList = new ArrayList<String>();
		generateFileList(fileList, file);
		return fileList;
	}

	private static void generateFileList(List<String> fileList, File file) {
		if (file.isFile())
			fileList.add(file.getAbsolutePath());
		else if (file.isDirectory()) {
			for (String filename : file.list())
				generateFileList(fileList, new File(file, filename));
		}
	}

	public static ByteBuffer readResourceByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;
		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
		if (url == null)
			throw new IOException("Classpath resource not found: " + resource);
		File file = new File(url.getFile());
		if (file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			fc.close();
			fis.close();
		} else {
			buffer = BufferUtils.createByteBuffer(bufferSize);
			InputStream source = url.openStream();
			if (source == null)
				throw new FileNotFoundException(resource);
			try {
				byte[] buf = new byte[8192];
				while (true) {
					int bytes = source.read(buf, 0, buf.length);
					if (bytes == -1)
						break;
					if (buffer.remaining() < bytes)
						buffer = resizeBuffer(buffer,
								Math.max(buffer.capacity() * 2, buffer.capacity() - buffer.remaining() + bytes));
					buffer.put(buf, 0, bytes);
				}
				buffer.flip();
			} finally {
				source.close();
			}
		}
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	public static void copyResourceFile(Class<?> clazz, String name, String path) throws IOException {
		try (InputStream in = getResourceStream(clazz, name)) {
			writeFile(path, in);
		}
	}

	public static void copyResourceFile(Class<?> clazz, String name, Path path) throws IOException {
		try (InputStream in = getResourceStream(clazz, name)) {
			writeFile(path, in);
		}
	}

	public static void copyResourceFile(Class<?> clazz, String name, URL url) throws IOException {
		try (InputStream in = getResourceStream(clazz, name)) {
			writeFile(url, in);
		}
	}

	public static void copyResourceFile(Class<?> clazz, String name, File file) throws IOException {
		try (InputStream in = getResourceStream(clazz, name)) {
			writeFile(file, in);
		}
	}

	public static void copyResourceFile(ClassLoader classLoader, String name, String path) throws IOException {
		try (InputStream in = getResourceStream(classLoader, name)) {
			writeFile(path, in);
		}
	}

	public static void copyResourceFile(ClassLoader classLoader, String name, Path path) throws IOException {
		try (InputStream in = getResourceStream(classLoader, name)) {
			writeFile(path, in);
		}
	}

	public static void copyResourceFile(ClassLoader classLoader, String name, URL url) throws IOException {
		try (InputStream in = getResourceStream(classLoader, name)) {
			writeFile(url, in);
		}
	}

	public static void copyResourceFile(ClassLoader classLoader, String name, File file) throws IOException {
		try (InputStream in = getResourceStream(classLoader, name)) {
			writeFile(file, in);
		}
	}

	public static void copyResourceFile(String name, String path) throws IOException {
		try (InputStream in = getResourceStream(name)) {
			writeFile(path, in);
		}
	}

	public static void copyResourceFile(String name, Path path) throws IOException {
		try (InputStream in = getResourceStream(name)) {
			writeFile(path, in);
		}
	}

	public static void copyResourceFile(String name, URL url) throws IOException {
		try (InputStream in = getResourceStream(name)) {
			writeFile(url, in);
		}
	}

	public static void copyResourceFile(String name, File file) throws IOException {
		try (InputStream in = getResourceStream(name)) {
			writeFile(file, in);
		}
	}

	public static InputStream getResourceStream(Class<?> clazz, String name) {
		return clazz.getResourceAsStream(name);
	}

	public static InputStream getResourceStream(ClassLoader classLoader, String name) {
		return classLoader.getResourceAsStream(name);
	}

	public static InputStream getResourceStream(String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}

	public static File getLastModified(String path) {
		return getLastModified(new File(path));
	}

	public static File getLastModified(Path path) {
		return getLastModified(path.toFile());
	}

	public static File getLastModified(URL url) {
		return getLastModified(new File(url.getFile()));
	}

	public static File getLastModified(File directory) {
		File[] files = directory.listFiles(File::isFile);
		long lastModifiedTime = Long.MIN_VALUE;
		File chosenFile = null;

		if (files != null) {
			for (File file : files) {
				if (file.lastModified() > lastModifiedTime) {
					chosenFile = file;
					lastModifiedTime = file.lastModified();
				}
			}
		}
		return chosenFile;
	}

	public static void openExplorer(File file) {
		if (!openSystem(file.getPath()) && !openDESKTOP(file))
			System.err.println("unable to open file " + System.getProperty("os.name"));
	}

	private static boolean openSystem(String what) {
		String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (os.contains("win")) {
			return (run("explorer", "%s", what));
		}
		if (os.contains("mac")) {
			return (run("open", "%s", what));
		}
		return run("kde-open", "%s", what) || run("gnome-open", "%s", what) || run("xdg-open", "%s", what);
	}

	private static boolean openDESKTOP(File file) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
			try {
				Desktop.getDesktop().open(file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static boolean run(String command, String arg, String file) {
		String[] args = arg.split(" ");
		String[] parts = new String[args.length + 1];
		parts[0] = command;
		for (int i = 0; i < args.length; i++) {
			parts[i + 1] = String.format(args[0], file).trim();
		}
		try {
			Process p = Runtime.getRuntime().exec(parts);
			if (p == null)
				return false;
			try {
				if (p.exitValue() == 0)
					return true;
				return false;
			} catch (IllegalThreadStateException itse) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}

}
