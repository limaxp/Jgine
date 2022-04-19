package org.jgine.misc.utils.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.entity.Prefab;
import org.jgine.core.manager.SystemManager;
import org.jgine.system.EngineSystem;

public class SceneLoader {

	protected interface LineParser {

		public void parseLine(String line) throws IOException,
				NumberFormatException, NoSuchElementException;
	}

	protected final HashMap<String, LineParser> parsers;
	protected File path;
	protected Scene scene;
	protected Entity currentEntity;

	public SceneLoader() {
		parsers = new HashMap<String, LineParser>();
		parsers.put("scene", new SceneLineParser());
	}

	public Scene load(File file) throws IOException {
		path = file.getParentFile();
		String filename = file.getName();
		scene = Engine.getInstance().createScene(filename);
		parseFile(filename);
		return scene;
	}

	protected void parseFile(String filename) throws IOException {
		// get the file relative to the source path
		File file = new File(path, filename);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		LineParser parser = getParser(filename);

		// parse every line in the file
		while (true) {
			String line = reader.readLine();
			// no more lines to read
			if (line == null) {
				reader.close();
				return;
			}
			line = line.trim();
			// ignore blank lines and comments
			if (line.length() > 0 && !line.startsWith("#")) {
				// interpret the line
				try {
					parser.parseLine(line);
				} catch (NumberFormatException ex) {
					reader.close();
					throw new IOException(ex.getMessage());
				} catch (NoSuchElementException ex) {
					reader.close();
					throw new IOException(ex.getMessage());
				}
			}
		}
	}

	private LineParser getParser(String filename) {
		// get the parser based on the file extension
		LineParser parser = null;
		int extIndex = filename.lastIndexOf('.');
		if (extIndex != -1) {
			String ext = filename.substring(extIndex + 1);
			parser = (LineParser) parsers.get(ext.toLowerCase());
		}
		if (parser == null) {
			parser = (LineParser) parsers.get("scene");
		}
		return parser;
	}

	protected class SceneLineParser implements LineParser {

		public void parseLine(String line) throws IOException,
				NumberFormatException, NoSuchElementException {
			StringTokenizer tokenizer = new StringTokenizer(line);
			String command = tokenizer.nextToken();
			switch (command) {
			case "system":
				scene.addSystem(SystemManager.get(tokenizer.nextToken()));
				break;

			case "entity":
				String name = tokenizer.nextToken();
				Prefab prefab = Prefab.get(name);
				currentEntity = new Entity(scene);
				if (prefab != null)
					prefab.create(scene, currentEntity);
				break;

			case "addSystem":
				EngineSystem system = SystemManager.get(tokenizer.nextToken());
				if (system != null) {
					// TODO create some System to load data
					// probably save as Yaml
					currentEntity.addSystem(system, system.load()); 
				}
				break;

			case "removeSystem":
				EngineSystem system2 = SystemManager.get(tokenizer.nextToken());
				if (system2 != null)
					currentEntity.removeSystem(system2);
				break;

			default:
				break;
			}
		}
	}
}
