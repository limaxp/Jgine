package org.jgine.misc.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.jgine.render.graphic.material.Image;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

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
		java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				java.nio.file.Files.delete(file);
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
				java.nio.file.Files.delete(dir);
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
		return java.nio.file.Files.readAllBytes(path);
	}

	public static byte[] readBytes(final URL url) throws IOException {
		return readBytes(new File(url.getFile()));
	}

	public static byte[] readBytes(File file) throws IOException {
		return readBytes(file.getPath());
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
		try (FileInputStream fis = new FileInputStream(file);
				FileChannel fc = fis.getChannel();) {
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
		return java.nio.file.Files.readAllLines(path, encoding);
	}

	public static List<String> readStringList(URL url, Charset encoding) throws IOException {
		return readStringList(new File(url.getFile()), encoding);
	}

	public static List<String> readStringList(File file, Charset encoding) throws IOException {
		return readStringList(file.getPath(), encoding);
	}

	public static List<String> readStringList(String path) throws IOException {
		return readStringList(path, Charset.defaultCharset());
	}

	public static List<String> readStringList(Path path) throws IOException {
		return readStringList(path, Charset.defaultCharset());
	}

	public static List<String> readStringList(URL url) throws IOException {
		return readStringList(new File(url.getFile()), Charset.defaultCharset());
	}

	public static List<String> readStringList(File file) throws IOException {
		return readStringList(file, Charset.defaultCharset());
	}

	public static List<String> readStringList(InputStream is) throws IOException {
		List<String> stringList = new ArrayList<String>();
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			while ((line = reader.readLine()) != null)
				stringList.add(line);
		}
		return stringList;
	}

	public static String readString(String path) throws IOException {
		return readString(new File(path));
	}

	public static String readString(Path path) throws IOException {
		return readString(path.toFile());
	}

	public static String readString(URL url) throws IOException {
		return readString(new File(url.getFile()));
	}

	public static String readString(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
				builder.append(line).append("\n");
			return builder.toString();
		}
	}

	public static String readString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is);
		java.util.Scanner s2 = s.useDelimiter("\\A");
		String string = s2.hasNext() ? s2.next() : "";
		s.close();
		s2.close();
		return string;
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
		java.nio.file.Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
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
		}
		else {
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
						buffer = resizeBuffer(buffer, Math.max(buffer.capacity() * 2, buffer.capacity() - buffer
								.remaining() + bytes));
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

	private static InputStream getResourceStream(Class<?> clazz, String name) {
		return clazz.getResourceAsStream(name);
	}

	private static InputStream getResourceStream(ClassLoader classLoader, String name) {
		return classLoader.getResourceAsStream(name);
	}

	private static InputStream getResourceStream(String name) {
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
}
