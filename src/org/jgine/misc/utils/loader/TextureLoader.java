package org.jgine.misc.utils.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.FileUtils;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.render.graphic.material.Image;
import org.jgine.render.graphic.material.Texture;
import org.jgine.render.graphic.material.TextureAnimationHandler.AnimationFrame;

public class TextureLoader {

	@Nullable
	public static Texture loadTexture(File file) {
		Image image = loadImage(file);
		if (image == null)
			return null;
		Texture texture = new Texture();
		texture.load(image);
		loadTextureAnimation(texture, file);
		return texture;
	}

	private static void loadTextureAnimation(Texture texture, File file) {
		String filePath = file.getPath();
		int dotPosition = filePath.lastIndexOf('.');
		String animationFilePath = filePath.substring(0, dotPosition) + ".meta";
		File animationFile = new File(animationFilePath);
		if (!animationFile.exists())
			return;
		loadAnimation(texture, animationFile);
	}

	@Nullable
	public static Texture loadTexture(String resourceName, InputStream is) {
		Image image = loadImage(is);
		if (image == null)
			return null;
		Texture texture = new Texture();
		texture.load(image);
		loadTextureAnimation(texture, resourceName);
		return texture;
	}

	private static void loadTextureAnimation(Texture texture, String resourceName) {
		int dotPosition = resourceName.lastIndexOf('.');
		String animationResourceName = resourceName.substring(0, dotPosition) + ".meta";
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				animationResourceName)) {
			if (is == null)
				return;
			loadAnimation(texture, is);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Animation '" + resourceName + "' could not be loaded!", e);
		}
	}

	@Nullable
	public static Image loadImage(File file) {
		try {
			return FileUtils.readImage(file);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Image '" + file.getPath() + "' could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static Image loadImage(InputStream is) {
		try {
			return FileUtils.readImage(is);
		} catch (IOException e) {
			Logger.err("ResourceLoader: Image input stream could not be loaded!", e);
			return null;
		}
	}

	@Nullable
	public static AnimationFrame[] loadAnimation(Texture texture, File file) {
		Map<String, Object> data = YamlLoader.load(file);
		if (data == null)
			return null;
		return loadAnimation(texture, data);
	}

	@Nullable
	public static AnimationFrame[] loadAnimation(Texture texture, InputStream is) {
		Map<String, Object> data = YamlLoader.load(is);
		if (data == null)
			return null;
		return loadAnimation(texture, data);
	}

	@SuppressWarnings("unchecked")
	public static AnimationFrame[] loadAnimation(Texture texture, Map<String, Object> data) {
		Object colums = data.get("colums");
		if (colums != null && colums instanceof Number)
			texture.setColums((int) colums);
		Object rows = data.get("rows");
		if (rows != null && rows instanceof Number)
			texture.setRows((int) rows);

		Object frames = data.get("frames");
		if (frames == null || !(frames instanceof List))
			return null;

		int baseFrameTime = 100;
		Object frameTime = data.get("frameTime");
		if (frameTime != null && frameTime instanceof Number)
			baseFrameTime = (int) frameTime;

		List<Integer> frameList = (List<Integer>) frames;
		AnimationFrame[] animationFrames = new AnimationFrame[frameList.size()];
		for (int i = 0; i < animationFrames.length; i++)
			animationFrames[i] = new AnimationFrame(baseFrameTime, frameList.get(i));

		Object frameTimes = data.get("frameTimes");
		if (frameTimes != null && frameTimes instanceof Map) {
			for (Entry<Integer, Object> entry : ((Map<Integer, Object>) frameTimes).entrySet()) {
				Object value = entry.getValue();
				if (value instanceof Number)
					animationFrames[entry.getKey()].frameTime += (int) value;
			}
		}
		texture.setAnimation(animationFrames);
		return animationFrames;
	}
}
