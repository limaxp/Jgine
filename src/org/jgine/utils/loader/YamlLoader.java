package org.jgine.utils.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.logger.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Helper class for reading and writing yaml files with
 * <a href="https://github.com/snakeyaml/snakeyaml">snakeyaml</a>.
 */
public class YamlLoader {

	private static final Yaml YAML;

	static {
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		YAML = new Yaml(options);
	}

	@Nullable
	public static Map<String, Object> load(File file) {
		try (InputStream is = new FileInputStream(file)) {
			return load(is);
		} catch (IOException e) {
			Logger.err("YamlLoader: Could not load file '" + file.getPath() + "'", e);
			return null;
		}
	}

	@Nullable
	public static Map<String, Object> load(InputStream is) {
		return YAML.load(is);
	}

	public static void save(File file, Map<String, Object> data) {
		try (FileWriter writer = new FileWriter(file)) {
			YAML.dump(data, writer);
		} catch (IOException e) {
			Logger.err("YamlLoader: Could not write to file '" + file.getPath() + "'", e);
		}
	}

	public static void save(OutputStream os, Map<String, Object> data) {
		try (OutputStreamWriter writer = new OutputStreamWriter(os)) {
			YAML.dump(data, writer);
		} catch (IOException e) {
			Logger.err("YamlLoader: Could not write to output stream!", e);
		}
	}

	public static void save(Writer writer, Map<String, Object> data) {
		YAML.dump(data, writer);
	}
}
