package jgine.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import jgine.render.light.Attenuation;
import jgine.render.material.Material;
import jgine.render.material.Texture;
import jgine.render.text.Text;
import jgine.utils.loader.ResourceManager;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector3f;
import jgine.utils.math.vector.Vector4f;

/**
 * Helper class for reading yaml data.
 */
public class ObjectUtils {

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
		return toVector2f(data, () -> defaultValue);
	}

	public static Vector2f toVector2f(Object data, Supplier<Vector2f> defaultValue) {
		if (data instanceof Number) {
			float f = ((Number) data).floatValue();
			return new Vector2f(f, f);
		} else if (data instanceof List list) {
			if (list.size() >= 2)
				return new Vector2f(ObjectUtils.toFloat(list.get(0)), ObjectUtils.toFloat(list.get(1)));
		} else if (data instanceof Map map) {
			return new Vector2f(ObjectUtils.toFloat(map.get("x")), ObjectUtils.toFloat(map.get("y")));
		}
		return defaultValue.get();
	}

	public static Vector3f toVector3f(Object data) {
		return toVector3f(data, Vector3f.NULL);
	}

	public static Vector3f toVector3f(Object data, Vector3f defaultValue) {
		return toVector3f(data, () -> defaultValue);
	}

	public static Vector3f toVector3f(Object data, Supplier<Vector3f> defaultValue) {
		if (data instanceof Number) {
			float f = ((Number) data).floatValue();
			return new Vector3f(f, f, f);
		} else if (data instanceof List list) {
			if (list.size() >= 3)
				return new Vector3f(ObjectUtils.toFloat(list.get(0)), ObjectUtils.toFloat(list.get(1)),
						ObjectUtils.toFloat(list.get(2)));
		} else if (data instanceof Map map) {
			return new Vector3f(ObjectUtils.toFloat(map.get("x")), ObjectUtils.toFloat(map.get("y")),
					ObjectUtils.toFloat(map.get("z")));
		}
		return defaultValue.get();
	}

	public static Vector4f toVector4f(Object data) {
		return toVector4f(data, Vector4f.NULL);
	}

	public static Vector4f toVector4f(Object data, Vector4f defaultValue) {
		return toVector4f(data, () -> defaultValue);
	}

	public static Vector4f toVector4f(Object data, Supplier<Vector4f> defaultValue) {
		if (data instanceof Number) {
			float f = ((Number) data).floatValue();
			return new Vector4f(f, f, f, f);
		} else if (data instanceof List list) {
			if (list.size() >= 4)
				return new Vector4f(ObjectUtils.toFloat(list.get(0)), ObjectUtils.toFloat(list.get(1)),
						ObjectUtils.toFloat(list.get(2)), ObjectUtils.toFloat(list.get(3), 1.0f));
		} else if (data instanceof Map map) {
			return new Vector4f(ObjectUtils.toFloat(map.get("x")), ObjectUtils.toFloat(map.get("y")),
					ObjectUtils.toFloat(map.get("z")), ObjectUtils.toFloat(map.get("w"), 1.0f));
		}
		return defaultValue.get();
	}

	public static int toColor(Object data) {
		return toColor(data, Color.WHITE);
	}

	public static int toColor(Object data, int defaultValue) {
		if (data instanceof Number)
			return ((Number) data).intValue();
		else if (data instanceof List list) {
			if (list.size() >= 3) {
				float r = toColor_(list.get(0), 0.0f);
				float g = toColor_(list.get(1), 0.0f);
				float b = toColor_(list.get(2), 0.0f);
				float a = 1.0f;
				if (list.size() >= 4)
					a = toColor_(list.get(3), 1.0f);
				return Color.rgba(r, g, b, a);
			}
		} else if (data instanceof Map map)
			return Color.rgba(toColor_(map.get("r"), 0.0f), toColor_(map.get("g"), 0.0f), toColor_(map.get("b"), 0.0f),
					toColor_(map.get("a"), 1.0f));
		return defaultValue;
	}

	private static float toColor_(Object o, float defaultValue) {
		if (o instanceof Double)
			return ((Double) o).floatValue();
		else if (o instanceof Integer)
			return ((Integer) o).floatValue() * Color.FLOAT_CONVERSION;
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

	public static Attenuation toAttenuation(Object data) {
		return toAttenuation(data, Attenuation.DEFAULT);
	}

	public static Attenuation toAttenuation(Object data, Attenuation defaultValue) {
		if (data instanceof Map map)
			return new Attenuation(ObjectUtils.toFloat(map.get("constant"), 0),
					ObjectUtils.toFloat(map.get("linear"), 0), ObjectUtils.toFloat(map.get("exponent"), 1));
		else if (data instanceof List list)
			if (list.size() >= 3)
				return new Attenuation(ObjectUtils.toFloat(list.get(0)), ObjectUtils.toFloat(list.get(1)),
						ObjectUtils.toFloat(list.get(2)));
		return defaultValue;
	}

	public static Material toMaterial(Object data) {
		return toMaterial(new Material(), data);
	}

	@SuppressWarnings("unchecked")
	public static Material toMaterial(Material out, Object data) {
		if (data instanceof String) {
			Texture texture = ResourceManager.getTexture((String) data);
			if (texture != null)
				out.setTexture(texture);
		} else if (data instanceof Map)
			out.load((Map<String, Object>) data);
		return out;
	}
}
