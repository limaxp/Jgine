package org.jgine.utils.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.material.Image;
import org.jgine.render.material.Texture;

import maxLibs.utils.logger.Logger;

/**
 * Helper class for loading {@link Texture} files.
 */
public class TextureLoader {

	@Nullable
	public static Texture loadTexture(File file) {
		return loadTexture("", file);
	}

	@Nullable
	public static Texture loadTexture(String name, File file) {
		Image image = loadImage(file);
		if (image == null)
			return null;
		Texture texture = new Texture(name);
		texture.load(image);
		loadTextureAnimation(texture, file);
		return texture;
	}

	@Nullable
	public static Texture loadTexture(String resourcePath, InputStream is) {
		return loadTexture("", resourcePath, is);
	}

	@Nullable
	public static Texture loadTexture(String name, String resourcePath, InputStream is) {
		Image image = loadImage(is);
		if (image == null)
			return null;
		Texture texture = new Texture(name);
		texture.load(image);
		loadTextureAnimation(texture, resourcePath);
		return texture;
	}

	private static void loadTextureAnimation(Texture texture, File file) {
		String filePath = file.getPath();
		File animationFile = new File(filePath.substring(0, filePath.lastIndexOf('.')) + ".meta");
		if (animationFile.exists())
			texture.setAnimation(TextureAnimationLoader.loadAnimation(animationFile));
	}

	private static void loadTextureAnimation(Texture texture, String resourcePath) {
		String animationResourceName = resourcePath.substring(0, resourcePath.lastIndexOf('.')) + ".meta";
		try (InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(animationResourceName)) {
			if (is != null)
				texture.setAnimation(TextureAnimationLoader.loadAnimation(is));
		} catch (IOException e) {
			Logger.err("ResourceLoader: Animation '" + resourcePath + "' could not be loaded!", e);
		}
	}

	@Nullable
	public static Image loadImage(File file) {
		return Image.read(file); // ImageIO.read(is);
	}

	@Nullable
	public static Image loadImage(InputStream is) {
		return Image.read(is); // ImageIO.read(is);
	}
}
