package jgine.utils.loader;

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
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.Prefab;
import jgine.core.sound.SoundBuffer;
import jgine.core.sound.SoundStream;
import jgine.render.material.Texture;
import jgine.render.mesh.Model;
import jgine.render.shader.Shader;
import jgine.system.script.ScriptManager;
import jgine.utils.FileUtils;
import jgine.utils.Logger;
import jgine.utils.collection.list.UnorderedIdentityArrayList;

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

	private final List<Callable<Void>> models = new UnorderedIdentityArrayList<>();
	private final List<Callable<Void>> textures = new UnorderedIdentityArrayList<>();
	private final List<Callable<Void>> shaders = new UnorderedIdentityArrayList<>();
	private final List<Callable<Void>> prefabs = new UnorderedIdentityArrayList<>();
	private final List<Callable<Void>> sounds = new UnorderedIdentityArrayList<>();
	private final List<Callable<Void>> scripts = new UnorderedIdentityArrayList<>();

	public void load(String path) {
		load(new File(path));
	}

	public void load(File file) {
		if (file.isDirectory())
			scanFiles(file);
		else if (file.isFile())
			switchExtension(file);
		loadData();
	}

	private void scanFiles(File file) {
		String[] fileNames = file.list();
		for (int i = 0; i < fileNames.length; i++) {
			File underFile = new File(file.getAbsolutePath() + File.separator + fileNames[i]);
			if (underFile.exists()) {
				if (underFile.isDirectory())
					scanFiles(underFile);
				else if (underFile.isFile())
					switchExtension(underFile);
			}
		}
	}

	public void loadResource(String name) {
		try {
			loadResource_(name);
		} catch (URISyntaxException | IOException e) {
			Logger.err("ResourceLoader: Could not scan resource '" + name + "'", e);
		}
	}

	private void loadResource_(String name) throws URISyntaxException, IOException {
		Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(name);
		while (urls.hasMoreElements()) {
			URI uri = urls.nextElement().toURI();
			if (uri.getScheme().equals("jar")) {
				try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
					scanRecources(fileSystem.getPath(name));
				}
			} else
				scanRecources(Paths.get(uri));
		}
		loadData();
	}

	private void scanRecources(Path path) throws IOException {
		String pathName = path.getFileName().toString();
		try (Stream<Path> walk = Files.walk(path, Integer.MAX_VALUE)) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path subPath = it.next();
				String fileName = subPath.getFileName().toString();
				if (fileName.contains(".")) {
					String subPathString = subPath.toString();
					String resourcePath = subPathString.substring(subPathString.indexOf(pathName));
					switchExtension(fileName, resourcePath);
				}
			}
		}
	}

	private void loadData() {
		try {
			for (Callable<Void> shader : shaders)
				shader.call();
			for (Callable<Void> texture : textures)
				texture.call();
			for (Callable<Void> model : models)
				model.call();
			for (Callable<Void> sound : sounds)
				sound.call();
			for (Callable<Void> script : scripts)
				script.call();
			for (Callable<Void> prefab : prefabs)
				prefab.call();
		} catch (Exception e) {
			Logger.err("ResourceLoader: Could not load data", e);
		}
		shaders.clear();
		textures.clear();
		models.clear();
		sounds.clear();
		scripts.clear();
		prefabs.clear();
	}

	private void switchExtension(String fileName, String resourcePath) {
		int dotPosition = fileName.lastIndexOf('.');
		String name = fileName.substring(0, dotPosition);
		String extension = fileName.substring(dotPosition + 1, fileName.length());
		switch (extension) {

		case "png":
		case "jpg":
			textures.add(() -> {
				try (InputStream is = getResourceStream(resourcePath)) {
					textureCallback(name, loadTexture(name, resourcePath, is));
					return null;
				}
			});
			break;

		case "fs":
		case "vs":
		case "gs":
		case "cs":
			shaders.add(() -> {
				try (InputStream is = getResourceStream(resourcePath)) {
					shaderCallback(name, loadString(is));
					return null;
				}
			});
			break;

		case "prefab":
			prefabs.add(() -> {
				try (InputStream is = getResourceStream(resourcePath)) {
					prefabCallback(name, loadPrefab(name, is));
					return null;
				}
			});
			break;

		case "ogg":
			sounds.add(() -> {
				try (InputStream is = getResourceStream(resourcePath)) {
					soundCallback(name, loadSoundOgg(is));
					return null;
				}
			});
			break;

		case "wav":
			sounds.add(() -> {
				try (InputStream is = getResourceStream(resourcePath)) {
					soundCallback(name, loadSoundWav(is));
					return null;
				}
			});
			break;

		default:
			if (ModelLoader.supportsImportExtension(extension)) {
				models.add(() -> {
					try (InputStream is = getResourceStream(resourcePath)) {
						modelCallback(name, loadModel(name, is));
						return null;
					}
				});
				break;
			}

			ScriptEngine engine = ScriptManager.getEngineByExtension(extension);
			if (engine != null) {
				scripts.add(() -> {
					try (InputStream is = getResourceStream(resourcePath)) {
						ScriptManager.eval(engine, loadString(is));
						scriptCallback(name, engine);
						return null;
					}
				});
			}
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
			textures.add(() -> {
				textureCallback(name, loadTexture(name, file));
				return null;
			});
			break;

		case "fs":
		case "vs":
		case "gs":
		case "cs":
			shaders.add(() -> {
				shaderCallback(name, loadString(file));
				return null;
			});
			break;

		case "prefab":
			prefabs.add(() -> {
				prefabCallback(name, loadPrefab(name, file));
				return null;
			});
			break;

		case "ogg":
			sounds.add(() -> {
				soundCallback(name, loadSoundOgg(file));
				return null;
			});
			break;

		case "wav":
			sounds.add(() -> {
				soundCallback(name, loadSoundWav(file));
				return null;
			});
			break;

		default:
			if (ModelLoader.supportsImportExtension(extension)) {
				models.add(() -> {
					modelCallback(name, loadModel(name, file.getPath()));
					return null;
				});
				break;
			}
			ScriptEngine engine = ScriptManager.getEngineByExtension(extension);
			if (engine != null) {
				scripts.add(() -> {
					ScriptManager.eval(engine, loadString(file));
					scriptCallback(name, engine);
					return null;
				});
			}
			break;
		}
	}

	@Nullable
	public static String loadString(File file) {
		try {
			return FileUtils.readString(file);
		} catch (IOException e) {
			Logger.err("ResourceLoader: String '" + file.getPath() + "' could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static String loadString(InputStream is) {
		return FileUtils.readString(is);
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
			Logger.err("ResourceLoader: Ogg Sound file '" + file.getPath() + "' could not be loaded!", e);
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

	private static InputStream getResourceStream(String path) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}
}
