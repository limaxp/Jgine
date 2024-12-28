package org.jgine.utils.loader;

import java.util.List;
import java.util.Map;

import org.jgine.render.mesh.text.Text;
import org.jgine.utils.Color;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.math.vector.Vector4f;

/**
 * Helper class for reading yaml data.
 */
public class YamlHelper {

	public static byte toByte(Object data) {
		return toByte(data, (byte) 0);
	}

	public static byte toByte(Object data, byte defaultValue) {
		if (data instanceof Number)
			return ((Number) data).byteValue();
		return defaultValue;
	}

	public static short toShort(Object data) {
		return toShort(data, (short) 0);
	}

	public static short toShort(Object data, short defaultValue) {
		if (data instanceof Number)
			return ((Number) data).shortValue();
		return defaultValue;
	}

	public static int toInt(Object data) {
		return toInt(data, 0);
	}

	public static int toInt(Object data, int defaultValue) {
		if (data instanceof Number)
			return ((Number) data).intValue();
		return defaultValue;
	}

	public static float toFloat(Object data) {
		return toFloat(data, 0.0f);
	}

	public static float toFloat(Object data, float defaultValue) {
		if (data instanceof Number)
			return ((Number) data).floatValue();
		return defaultValue;
	}

	public static double toDouble(Object data) {
		return toDouble(data, 0.0);
	}

	public static double toDouble(Object data, double defaultValue) {
		if (data instanceof Number)
			return ((Number) data).doubleValue();
		return defaultValue;
	}

	public static String toString(Object data) {
		return toString(data, "");
	}

	public static String toString(Object data, String defaultValue) {
		if (data instanceof String)
			return (String) data;
		return defaultValue;
	}

	public static boolean toBoolean(Object data) {
		return toBoolean(data, false);
	}

	public static boolean toBoolean(Object data, boolean defaultValue) {
		if (data instanceof Boolean)
			return (Boolean) data;
		else if (data instanceof Number)
			return ((Number) data).intValue() != 0;
		else if (data instanceof String) {
			if (((String) data).equalsIgnoreCase("true"))
				return true;
			else if (((String) data).equalsIgnoreCase("false"))
				return false;
		}
		return defaultValue;
	}

	public static Vector2f toVector2f(Object data) {
		return toVector2f(data, Vector2f.NULL);
	}

	public static Vector2f toVector2f(Object data, Vector2f defaultValue) {
		if (data instanceof Number) {
			float f = ((Number) data).floatValue();
			return new Vector2f(f, f);
		} else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> rotationList = (List<Object>) data;
			if (rotationList.size() >= 2)
				return new Vector2f(YamlHelper.toFloat(rotationList.get(0)), YamlHelper.toFloat(rotationList.get(1)));
		} else if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> rotationMap = (Map<String, Object>) data;
			return new Vector2f(YamlHelper.toFloat(rotationMap.get("x")), YamlHelper.toFloat(rotationMap.get("y")));
		}
		return defaultValue;
	}

	public static Vector3f toVector3f(Object data) {
		return toVector3f(data, Vector3f.NULL);
	}

	public static Vector3f toVector3f(Object data, Vector3f defaultValue) {
		if (data instanceof Number) {
			float f = ((Number) data).floatValue();
			return new Vector3f(f, f, f);
		} else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> rotationList = (List<Object>) data;
			if (rotationList.size() >= 3)
				return new Vector3f(YamlHelper.toFloat(rotationList.get(0)), YamlHelper.toFloat(rotationList.get(1)),
						YamlHelper.toFloat(rotationList.get(2)));
		} else if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> rotationMap = (Map<String, Object>) data;
			return new Vector3f(YamlHelper.toFloat(rotationMap.get("x")), YamlHelper.toFloat(rotationMap.get("y")),
					YamlHelper.toFloat(rotationMap.get("z")));
		}
		return defaultValue;
	}

	public static Vector4f toVector4f(Object data) {
		return toVector4f(data, Vector4f.NULL);
	}

	public static Vector4f toVector4f(Object data, Vector4f defaultValue) {
		if (data instanceof Number) {
			float f = ((Number) data).floatValue();
			return new Vector4f(f, f, f, f);
		} else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> rotationList = (List<Object>) data;
			if (rotationList.size() >= 4)
				return new Vector4f(YamlHelper.toFloat(rotationList.get(0)), YamlHelper.toFloat(rotationList.get(1)),
						YamlHelper.toFloat(rotationList.get(2)), YamlHelper.toFloat(rotationList.get(3), 1.0f));
		} else if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> rotationMap = (Map<String, Object>) data;
			return new Vector4f(YamlHelper.toFloat(rotationMap.get("x")), YamlHelper.toFloat(rotationMap.get("y")),
					YamlHelper.toFloat(rotationMap.get("z")), YamlHelper.toFloat(rotationMap.get("w"), 1.0f));
		}
		return defaultValue;
	}

	public static int toColor(Object data) {
		return toColor(data, Color.WHITE);
	}

	public static int toColor(Object data, int defaultValue) {
		if (data instanceof Number)
			return ((Number) data).intValue();
		else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> dataList = (List<Object>) data;
			if (dataList.size() >= 3) {
				float r = toColor_(dataList.get(0), 0.0f);
				float g = toColor_(dataList.get(1), 0.0f);
				float b = toColor_(dataList.get(2), 0.0f);
				float a = 1;
				if (dataList.size() >= 4)
					a = toColor_(dataList.get(3), 1);
				return Color.rgba(r, g, b, a);
			}
		} else if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) data;
			return Color.rgba(toColor_(dataMap.get("r"), 0.0f), toColor_(dataMap.get("g"), 0.0f),
					toColor_(dataMap.get("b"), 0.0f), toColor_(dataMap.get("a"), 1.0f));
		}
		return defaultValue;
	}

	private static float toColor_(Object o, float defaultValue) {
		if (o instanceof Double)
			return ((Double) o).floatValue();
		else if (o instanceof Integer)
			return ((Integer) o).floatValue() / 255;
		return defaultValue;
	}

	public static int toTextType(Object data) {
		return toTextType(data, Text.TYPE_TRUETYPE);
	}

	public static int toTextType(Object data, int defaultValue) {
		if (data instanceof Number)
			return ((Number) data).intValue();
		else if (data instanceof String) {
			if (((String) data).equalsIgnoreCase("truetype"))
				return Text.TYPE_TRUETYPE;
			else if (((String) data).equalsIgnoreCase("bitmap"))
				return Text.TYPE_BITMAP;
		}
		return defaultValue;
	}
}
