package org.jgine.core.manager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Prefab;
import org.jgine.core.entity.PrefabManager;
import org.jgine.misc.collection.list.arrayList.IdentityArrayList;
import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.misc.utils.loader.ModelLoader;
import org.jgine.misc.utils.loader.ResourceLoader;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
import org.jgine.render.graphic.mesh.Model;
import org.jgine.sound.SoundBuffer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ResourceManager {

	// TODO should be able to dynamic load resources if used and free resources if
	// not!

	private static final Map<String, Model> MODEL_MAP = new HashMap<String, Model>();
	private static final Map<String, Texture> TEXTURE_MAP = new HashMap<String, Texture>();
	private static final Map<String, String> SHADER_MAP = new HashMap<String, String>();
	private static final Map<String, SoundBuffer> SOUND_MAP = new HashMap<String, SoundBuffer>();
	private static final BiMap<String, ScriptEngine> SCRIPT_MAP = HashBiMap.create();
	private static final Map<String, List<Material>> WAITING_MATERIAL_MAP = new HashMap<String, List<Material>>();

	private static class ResourceData extends ResourceLoader {

		private final Map<String, Model> models = new HashMap<String, Model>();
		private final Map<String, Texture> textures = new HashMap<String, Texture>();
		private final Map<String, String> shaders = new HashMap<String, String>();
		private final Map<String, SoundBuffer> sounds = new HashMap<String, SoundBuffer>();
		private final Map<String, ScriptEngine> scripts = new HashMap<String, ScriptEngine>();
		private final List<Prefab> prefabs = new IdentityArrayList<Prefab>();

		@Override
		public void modelCallback(String name, @Nullable Model model) {
			if (model != null) {
				Model old = MODEL_MAP.put(name, model);
				if (old != null) {
					Logger.warn("ResourceManager: Model name conflict '" + name + "'");
					MODEL_MAP.put(name, old);
					model.close();
				} else
					models.put(name, model);
			}
		}

		@Override
		public void textureCallback(String name, @Nullable Texture texture) {
			if (texture != null) {
				Texture old = TEXTURE_MAP.put(name, texture);
				loadWaitingMaterial(name, texture);
				if (old != null) {
					Logger.warn("ResourceManager: Texture name conflict '" + name + "'");
					TEXTURE_MAP.put(name, old);
					texture.close();
				} else
					textures.put(name, texture);
			}
		}

		@Override
		public void shaderCallback(String name, @Nullable String shader) {
			if (shader != null && !shader.isEmpty()) {
				String old = SHADER_MAP.put(name, shader);
				if (old != null) {
					Logger.warn("ResourceManager: Shader name conflict '" + name + "'");
					SHADER_MAP.put(name, old);
				} else
					shaders.put(name, shader);
			}
		}

		@Override
		public void prefabCallback(String name, @Nullable Prefab prefab) {
			if (prefab != null)
				prefabs.add(prefab);
		}

		@Override
		public void soundCallback(String name, @Nullable SoundBuffer sound) {
			if (sound != null) {
				SoundBuffer old = SOUND_MAP.put(name, sound);
				if (old != null) {
					Logger.warn("ResourceManager: Sound name conflict '" + name + "'");
					SOUND_MAP.put(name, old);
					sound.close();
				} else
					sounds.put(name, sound);
			}
		}

		@Override
		public void scriptCallback(String name, @Nullable ScriptEngine script) {
			if (script != null) {
				ScriptEngine old = SCRIPT_MAP.put(name, script);
				if (old != null) {
					Logger.warn("ResourceManager: Script name conflict '" + name + "'");
					SCRIPT_MAP.put(name, old);
				} else
					scripts.put(name, script);
			}
		}
	}

	private static final Map<Object, ResourceData> DATA_MAP = new HashMap<Object, ResourceData>();

	static {
		loadResource("engine_assets");
	}

	public static void terminate() {
		free();
		ModelLoader.free();
	}

	public static void load(String path) {
		load(null, path);
	}

	public static void load(Object identifier, String path) {
		getData(identifier).load(path);
	}

	public static void load(File file) {
		load(null, file);
	}

	public static void load(Object identifier, File file) {
		getData(identifier).load(file);
	}

	public static void loadResource(String name) {
		loadResource(null, name);
	}

	public static void loadResource(Object identifier, String name) {
		getData(identifier).loadResource(name);
	}

	private static ResourceData getData(Object identifier) {
		ResourceData data = DATA_MAP.get(identifier);
		if (data == null)
			DATA_MAP.put(identifier, data = new ResourceData());
		return data;
	}

	public static void free() {
		for (Model model : MODEL_MAP.values())
			model.close();
		MODEL_MAP.clear();
		for (Texture texture : TEXTURE_MAP.values())
			texture.close();
		TEXTURE_MAP.clear();
		SHADER_MAP.clear();
		for (SoundBuffer sound : SOUND_MAP.values())
			sound.close();
		SOUND_MAP.clear();
		SCRIPT_MAP.clear();
		DATA_MAP.clear();
		PrefabManager.clear();
	}

	public static void free(Object identifier) {
		ResourceData data = DATA_MAP.remove(identifier);
		if (data == null)
			return;
		for (Entry<String, Model> model : data.models.entrySet()) {
			MODEL_MAP.remove(model.getKey());
			model.getValue().close();
		}
		for (Entry<String, Texture> texture : data.textures.entrySet()) {
			TEXTURE_MAP.remove(texture.getKey());
			texture.getValue().close();
		}
		for (Entry<String, String> shader : data.shaders.entrySet()) {
			SHADER_MAP.remove(shader.getKey());
		}
		for (Entry<String, SoundBuffer> sound : data.sounds.entrySet()) {
			SOUND_MAP.remove(sound.getKey());
			sound.getValue().close();
		}
		for (Entry<String, ScriptEngine> script : data.scripts.entrySet()) {
			SCRIPT_MAP.remove(script.getKey());
		}
		for (Prefab prefab : data.prefabs)
			PrefabManager.free(prefab);
	}

	@Nullable
	public static Model getModel(String name) {
		return MODEL_MAP.get(name);
	}

	public static Collection<Model> getModels() {
		return MODEL_MAP.values();
	}

	@Nullable
	public static Texture getTexture(String name) {
		return TEXTURE_MAP.get(name);
	}

	@Nullable
	public static Texture getTexture(String name, Material material) {
		int index = name.lastIndexOf(".");
		if (index != -1)
			name = name.substring(0, index);
		Texture texture = TEXTURE_MAP.get(name);
		if (texture == null) {
			addWaitingMaterial(name, material);
			return Texture.NONE;
		}
		return texture;
	}

	public static Collection<Texture> getTextures() {
		return TEXTURE_MAP.values();
	}

	@Nullable
	public static String getShader(String name) {
		return SHADER_MAP.get(name);
	}

	public static Collection<String> getShaders() {
		return SHADER_MAP.values();
	}

	@Nullable
	public static SoundBuffer getSound(String name) {
		return SOUND_MAP.get(name);
	}

	public static Collection<SoundBuffer> getSounds() {
		return SOUND_MAP.values();
	}

	@Nullable
	public static ScriptEngine getScript(String name) {
		return SCRIPT_MAP.get(name);
	}

	@Nullable
	public static String getScriptName(ScriptEngine scipt) {
		return SCRIPT_MAP.inverse().get(scipt);
	}

	public static Collection<ScriptEngine> getScripts() {
		return SCRIPT_MAP.values();
	}

	private static void addWaitingMaterial(String name, Material material) {
		List<Material> waitingList = WAITING_MATERIAL_MAP.get(name);
		if (waitingList == null)
			WAITING_MATERIAL_MAP.put(name, waitingList = new UnorderedIdentityArrayList<Material>());
		waitingList.add(material);
	}

	private static void loadWaitingMaterial(String name, Texture texture) {
		List<Material> waiting = WAITING_MATERIAL_MAP.get(name);
		if (waiting != null) {
			WAITING_MATERIAL_MAP.remove(name);
			for (Material material : waiting)
				material.setTexture(texture);
		}
	}

	public static Path getResourcePath(String name) {
		try {
			return getResourcePath_(name);
		} catch (URISyntaxException | IOException e) {
			Logger.err("ResourceManager: Error on getting resource path!", e);
			return null;
		}
	}

	public static Path getResourcePath_(String name) throws URISyntaxException, IOException {
		URI uri = Thread.currentThread().getContextClassLoader().getResource(name).toURI();
		if (uri.getScheme().equals("jar"))
			try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
				return fileSystem.getPath(name);
			}
		else
			return Paths.get(uri);
	}
}
