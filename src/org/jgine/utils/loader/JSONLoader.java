package org.jgine.utils.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import maxLibs.utils.logger.Logger;

/**
 * Helper class for reading and writing json files with
 * <a href="https://github.com/FasterXML/jackson">FasterXML/jackson</a>.
 */
public class JSONLoader {

	public static final JsonEncoding DEFAULT_ENCODING = JsonEncoding.UTF8;
	private static final JsonFactory FACTORY;
	private static final ObjectMapper OBJECT_MAPPER;

	static {
		FACTORY = new JsonFactory();
		FACTORY.enable(JsonParser.Feature.ALLOW_COMMENTS);
		OBJECT_MAPPER = new ObjectMapper(FACTORY);
	}

	@Nullable
	public static JsonParser parser(String s) {
		try {
			return FACTORY.createParser(s);
		} catch (IOException e) {
			Logger.err("JSONLoader: Could not load inputstream!", e);
			return null;
		}
	}

	@Nullable
	public static JsonParser parser(File file) {
		try {
			return FACTORY.createParser(file);
		} catch (IOException e) {
			Logger.err("JSONLoader: Could not load file '" + file.getPath() + "'", e);
			return null;
		}
	}

	@Nullable
	public static JsonParser parser(InputStream is) {
		try {
			return FACTORY.createParser(is);
		} catch (IOException e) {
			Logger.err("JSONLoader: Could not load inputstream!", e);
			return null;
		}
	}

	public static JsonGenerator writer(File file) {
		return writer(file, DEFAULT_ENCODING);
	}

	public static JsonGenerator writer(File file, JsonEncoding encoding) {
		try {
			JsonGenerator jg = FACTORY.createGenerator(file, encoding);
			jg.useDefaultPrettyPrinter();
			return jg;
		} catch (IOException e) {
			Logger.err("JSONLoader: Could not write outputstream!", e);
			return null;
		}
	}

	public static JsonGenerator writer(OutputStream os) {
		return writer(os, DEFAULT_ENCODING);
	}

	public static JsonGenerator writer(OutputStream os, JsonEncoding encoding) {
		try {
			JsonGenerator jg = FACTORY.createGenerator(os, encoding);
			jg.useDefaultPrettyPrinter();
			return jg;
		} catch (IOException e) {
			Logger.err("JSONLoader: Could not write outputstream!", e);
			return null;
		}
	}

	public static JsonNode load(String json) {
		try {
			return OBJECT_MAPPER.readTree(json);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonNode load(File file) {
		try {
			return OBJECT_MAPPER.readTree(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonNode load(InputStream is) {
		try {
			return OBJECT_MAPPER.readTree(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void write(File file, JsonNode node) {
		try (JsonGenerator gen = writer(file)) {
			OBJECT_MAPPER.writeTree(gen, node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void write(OutputStream os, JsonNode node) {
		try (JsonGenerator gen = writer(os)) {
			OBJECT_MAPPER.writeTree(gen, node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> T load(String json, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(json, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T load(File file, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(file, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T load(InputStream is, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(is, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void write(File file, Object obj) {
		try {
			OBJECT_MAPPER.writeValue(file, obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void write(OutputStream os, Object obj) {
		try {
			OBJECT_MAPPER.writeValue(os, obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String toJsonString(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static byte[] toJsonBytes(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsBytes(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
}
