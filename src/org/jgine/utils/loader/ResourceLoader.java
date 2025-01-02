package org.jgine.utils.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Prefab;
import org.jgine.core.sound.SoundBuffer;
import org.jgine.core.sound.SoundStream;
import org.jgine.render.material.Texture;
import org.jgine.render.mesh.Model;
import org.jgine.render.shader.Shader;
import org.jgine.utils.FileUtils;
import org.jgine.utils.collection.list.UnorderedIdentityArrayList;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.script.ScriptManager;

/**
 * Helper class for loading {@link Model}<code>s</code>,
 * {@link Texture}<code>s</code>, {@link SoundBuffer}<code>s</code>,
 * {@link Shader}<code>s</code>, {@link ScriptEngine}<code>s</code> and
 * {@link Prefab}<code>s</code>.
 * <p>
 * Use load() method to import a folder. Use loadResource() method to import a
 * resource folder (e.g. a jar file).
 */
public abstract class ResourceLoader {

	public abstract void modelCallback(String name, @Nullable Model model);

	public abstract void textureCallback(String name, @Nullable Texture texture);

	public abstract void shaderCallback(String name, @Nullable String shader);

	public abstract void prefabCallback(String name, @Nullable Prefab prefab);

	public abstract void soundCallback(String name, @Nullable SoundBuffer sound);

	public abstract void scriptCallback(String name, @Nullable ScriptEngine script);

	private final List<PrefabPrototype> loadingPrefabs = new UnorderedIdentityArrayList<PrefabPrototype>();

	public void load(String path) {
		load(new File(path));
	}

	public void load(File file) {
		if (file.isDirectory())
			loadFiles(file);
		else if (file.isFile())
			switchExtension(file);
		loadPrefabs();
	}

	private void loadFiles(File file) {
		String[] fileNames = file.list();
		for (int i = 0; i < fileNames.length; i++) {
			File underFile = new File(file.getAbsolutePath() + File.separator + fileNames[i]);
			if (underFile.exists()) {
				if (underFile.isDirectory())
					loadFiles(underFile);
				else if (underFile.isFile())
					switchExtension(underFile);
			}
		}
	}

	public void loadResource(String name) {
		try {
			loadResource_(name);
		} catch (URISyntaxException | IOException e) {
			Logger.err("ResourceLoader: Could not load resource '" + name + "'", e);
		}
	}

	private void loadResource_(String name) throws URISyntaxException, IOException {
		Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(name);
		while (urls.hasMoreElements()) {
			URI uri = urls.nextElement().toURI();
			if (uri.getScheme().equals("jar")) {
				try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
					loadRecources(fileSystem.getPath(name));
				}
			} else
				loadRecources(Paths.get(uri));
		}
		loadPrefabs();
	}

	private void loadRecources(Path path) throws IOException {
		String pathName = path.getFileName().toString();
		try (Stream<Path> walk = Files.walk(path, Integer.MAX_VALUE)) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path subPath = it.next();
				String fileName = subPath.getFileName().toString();
				if (fileName.contains(".")) {
					String subPathString = subPath.toString();
					String resourcePath = subPathString.substring(subPathString.indexOf(pathName));
					InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
					if (is != null)
						switchExtension(fileName, resourcePath, is);
				}
			}
		}
	}

	private void loadPrefabs() {
		for (PrefabPrototype prototype : loadingPrefabs)
			prefabCallback(prototype.name, prototype.load());
		loadingPrefabs.clear();
	}

	private void switchExtension(String fileName, String resourcePath, InputStream is) throws IOException {
		int dotPosition = fileName.lastIndexOf('.');
		String name = fileName.substring(0, dotPosition);
		String extension = fileName.substring(dotPosition + 1, fileName.length());
		switch (extension) {

		case "png":
		case "jpg":
			textureCallback(name, loadTexture(name, resourcePath, is));
			is.close();
			break;

		case "fs":
		case "vs":
		case "gs":
		case "cs":
			shaderCallback(name, loadShader(is));
			is.close();
			break;

		case "prefab":
			loadingPrefabs.add(new PrefabInputStreamPrototype(name, is));
			break;

		case "ogg":
			soundCallback(name, loadSoundOgg(is));
			is.close();
			break;

		case "wav":
			soundCallback(name, loadSoundWav(is));
			is.close();
			break;

		default:
			if (ModelLoader.supportsImportExtension(extension))
				modelCallback(name, loadModel(name, is));
			ScriptEngine engine = ScriptManager.getEngineByExtension(extension);
			if (engine != null) {
				ScriptManager.eval(engine, loadScript(is));
				scriptCallback(name, engine);
			}
			is.close();
			break;
		}
	}

	private void switchExtension(File file) {
		String fileName = file.getName();
		int dotPosition = fileName.lastIndexOf('.');
		String name = fileName.substring(0, dotPosition);
		String extension = fileName.substring(dotPosition + 1, fileName.length());
		switch (extension) {

		case "png":
		case "jpg":
			textureCallback(name, loadTexture(name, file));
			break;

		case "fs":
		case "vs":
		case "gs":
		case "cs":
			shaderCallback(name, loadShader(file));
			break;

		case "prefab":
			loadingPrefabs.add(new PrefabFilePrototype(name, file));
			break;

		case "ogg":
			soundCallback(name, loadSoundOgg(file));
			break;

		case "wav":
			soundCallback(name, loadSoundWav(file));
			break;

		default:
			if (ModelLoader.supportsImportExtension(extension))
				modelCallback(name, loadModel(name, file.getPath()));
			ScriptEngine engine = ScriptManager.getEngineByExtension(extension);
			if (engine != null) {
				ScriptManager.eval(engine, loadScript(file));
				scriptCallback(name, engine);
			}
			break;
		}
	}

	@Nullable
	public static Model loadModel(String name, String path) {
		return ModelLoader.load(name, path);
	}

	/**
	 * Only works with file formats which don't spread their content onto multiple
	 * files, such as .obj or .md3.
	 * 
	 * @param is
	 * @return
	 */
	public static Model loadModel(String name, InputStream is) {
		ByteBuffer buffer;
		try {
			buffer = FileUtils.readByteBuffer(is);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Model input stream could not be loaded!", e);
			return null;
		}
		return ModelLoader.load(name, buffer);
	}

	@Nullable
	public static Texture loadTexture(String name, File file) {
		return TextureLoader.loadTexture(name, file);
	}

	@Nullable
	public static Texture loadTexture(String name, String resourcePath, InputStream is) {
		return TextureLoader.loadTexture(name, resourcePath, is);
	}

	@Nullable
	public static String loadShader(File file) {
		try {
			return FileUtils.readString(file);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Shader '" + file.getPath() + "' could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static String loadShader(InputStream is) {
		return FileUtils.readString(is);
	}

	@Nullable
	public static Prefab loadPrefab(String name, File file) {
		return PrefabLoader.load(name, file);
	}

	@Nullable
	public static Prefab loadPrefab(String name, InputStream is) {
		return PrefabLoader.load(name, is);
	}

	@Nullable
	public static SoundBuffer loadSoundOgg(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			if (fis.available() > SoundStream.MIN_SIZE)
				return SoundStream.from(fis);
			else
				return new SoundBuffer(fis);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Sound '" + file.getPath() + "' could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static SoundBuffer loadSoundOgg(InputStream is) {
		try {
			if (is.available() > SoundStream.MIN_SIZE)
				return SoundStream.from(is);
			else
				return new SoundBuffer(is);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Ogg input stream could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static SoundBuffer loadSoundWav(File file) {
		WaveData waveData = WaveData.create(file);
		SoundBuffer soundBuffer;
		if (waveData.data.remaining() > SoundStream.MIN_SIZE)
			soundBuffer = new SoundStream(waveData.data, waveData.format, waveData.samplerate,
					waveData.data.remaining());
		else
			soundBuffer = new SoundBuffer(waveData.data, waveData.format, waveData.samplerate);
		waveData.dispose();
		return soundBuffer;
	}

	@Nullable
	public static SoundBuffer loadSoundWav(InputStream is) {
		try (BufferedInputStream bis = new BufferedInputStream(is)) {
			WaveData waveData = WaveData.create(bis);
			SoundBuffer soundBuffer;
			if (waveData.data.remaining() > SoundStream.MIN_SIZE)
				soundBuffer = new SoundStream(waveData.data, waveData.format, waveData.samplerate,
						waveData.data.remaining());
			else
				soundBuffer = new SoundBuffer(waveData.data, waveData.format, waveData.samplerate);
			waveData.dispose();
			return soundBuffer;
		} catch (IOException e) {
			Logger.err("ResourceLoader: Wavefront input stream could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static String loadScript(File file) {
		try {
			return FileUtils.readString(file);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Script '" + file.getPath() + "' could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static String loadScript(InputStream is) {
		return FileUtils.readString(is);
	}

	private static abstract class PrefabPrototype {

		protected final String name;

		protected PrefabPrototype(String name) {
			this.name = name;
		}

		public abstract Prefab load();
	}

	private static class PrefabFilePrototype extends PrefabPrototype {

		protected final File file;

		protected PrefabFilePrototype(String name, File file) {
			super(name);
			this.file = file;
		}

		@Override
		public Prefab load() {
			return loadPrefab(name, file);
		}
	}

	private static class PrefabInputStreamPrototype extends PrefabPrototype {

		protected final InputStream is;

		protected PrefabInputStreamPrototype(String name, InputStream is) {
			super(name);
			this.is = is;
		}

		@Override
		public Prefab load() {
			Prefab prefab = loadPrefab(name, is);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return prefab;
		}
	}
}
