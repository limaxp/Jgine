package org.jgine.misc.utils.options;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jgine.misc.utils.loader.YamlLoader;
import org.jgine.misc.utils.logger.Logger;

/**
 * Helper class for config.ini access.
 */
public class OptionFile {

	public static final File FILE;
	private static final Map<String, Object> DATA;

	static {
		FILE = new File("." + File.separator + "config.ini");
		if (!FILE.exists()) {
			try {
				FILE.createNewFile();
			} catch (IOException e) {
				Logger.err("Options: Error creating config file!", e);
			}
		}
		Map<String, Object> data = YamlLoader.load(FILE);
		if (data == null)
			data = new HashMap<String, Object>();
		DATA = data;
	}

	public static void save() {
		YamlLoader.save(FILE, DATA);
	}

	public static Object getData(String property, Object defaultvalue) {
		if (property.contains(".")) {
			String[] split = property.split("\\.");
			Map<String, Object> data = findSubData(split);
			Object result = data.get(split[split.length - 1]);
			if (result == null) {
				data.put(split[split.length - 1], defaultvalue);
				result = defaultvalue;
			}
			return result;
		} else {
			Object result = DATA.get(property);
			if (result == null) {
				DATA.put(property, defaultvalue);
				result = defaultvalue;
			}
			return result;
		}
	}

	public static void setData(String property, Object value) {
		if (property.contains(".")) {
			String[] split = property.split("\\.");
			findSubData(split).put(split[split.length - 1], value);
		} else
			DATA.put(property, value);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> findSubData(String[] split) {
		Map<String, Object> data = DATA;
		for (int i = 0; i < split.length - 1; i++) {
			Object subData = data.get(split[i]);
			if (subData == null || !(subData instanceof Map)) {
				subData = new HashMap<String, Object>();
				data.put(split[i], subData);
			}
			data = (Map<String, Object>) subData;
		}
		return data;
	}
}
