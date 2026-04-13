package jgine.utils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import jgine.core.window.DisplayManager;
import jgine.core.window.Window;
import jgine.utils.loader.YamlLoader;

public class Options {

	/**
	 * Whether certain debugging checks should be made.
	 */
	public static final boolean DEBUG = as("jgine.debug", false).asBoolean();

	/**
	 * Whether the engine systems update synchron or asynchron.
	 */
	public static final boolean SYNCHRONIZED = as("jgine.synchronized", false).asBoolean();

	/**
	 * Whether fast approximations of some java.lang.Math operations should be used.
	 */
	public static final boolean FASTMATH = as("jgine.fastmath", false).asBoolean();

	/**
	 * When {@link #FASTMATH} is <code>true</code>, whether to use a lookup table
	 * for sin/cos.
	 */
	public static final boolean SIN_LOOKUP = as("jgine.sinLookup.active", false).asBoolean();

	/**
	 * When {@link #SIN_LOOKUP} is <code>true</code>, this determines the table
	 * size.
	 */
	public static final int SIN_LOOKUP_BITS = as("jgine.sinLookup.bits", 14).asInt();

	/**
	 * Whether to try using java.lang.Math.fma() in most matrix/vector/quaternion
	 * operations if it is available. If the CPU does <i>not</i> support it, it will
	 * be a lot slower than `a*b+c` and potentially generate a lot of memory
	 * allocations for the emulation with `java.util.BigDecimal`, though.
	 */
	public static final boolean USE_MATH_FMA = as("jgine.useMathFma", false).asBoolean();

	/**
	 * The maximal amount of sounds that can be played simultaneously.
	 */
	public static final Option<Integer> MAX_SOUNDS = as("jgine.sound.maxSounds", 512);

	/**
	 * The x resolution used.
	 */
	public static final Option<Integer> RESOLUTION_X = as("jgine.graphic.resolution.x",
			(DisplayManager.getPrimaryDisplay() != null) ? DisplayManager.getPrimaryDisplay().getWidth() : 1280);

	/**
	 * The y resolution used.
	 */
	public static final Option<Integer> RESOLUTION_Y = as("jgine.graphic.resolution.y",
			(DisplayManager.getPrimaryDisplay() != null) ? DisplayManager.getPrimaryDisplay().getHeight() : 1024);

	/**
	 * The monitor used.
	 */
	public static final Option<Integer> MONITOR = as("jgine.graphic.monitor", 0);

	/**
	 * The window mode used.
	 */
	public static final Option<Integer> WINDOW_MODE = as("jgine.graphic.mode", Window.Mode.FULLSCREEN);

	/**
	 * If v-sync should be applied.
	 */
	public static final Option<Boolean> V_SYNC = as("jgine.graphic.v-sync", true, Window::v_sync);

	/**
	 * The anti aliasing factor used.
	 */
	public static final Option<Integer> ANTI_ALIASING = as("jgine.graphic.antialiasing", 4);

	/**
	 * Whether to use a {@link NumberFormat} producing scientific notation output
	 * when formatting matrix, vector and quaternion components to strings.
	 */
	public static final Option<Boolean> USE_NUMBER_FORMAT = as("jgine.format.active", true);

	/**
	 * Determines the number of decimal digits produced in the formatted numbers.
	 */
	public static final Option<Integer> NUMBER_FORMAT_DECIMALS = as("jgine.format.decimals", 3);

	/**
	 * The {@link NumberFormat} used to format all numbers throughout all JOML
	 * classes.
	 */
	public static final NumberFormat NUMBER_FORMAT = decimalFormat();

	public static Option<Boolean> as(String property, boolean defaultValue) {
		return as(property, defaultValue, (_) -> {
		});
	}

	public static Option<Boolean> as(String property, boolean defaultValue, Consumer<Boolean> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Boolean>(property, hasOption(System.getProperty(property, value.toString())), func);
	}

	public static Option<Short> as(String property, short defaultValue) {
		return as(property, defaultValue, (Consumer<Short>) (_) -> {
		});
	}

	public static Option<Short> as(String property, short defaultValue, Consumer<Short> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Short>(property, Short.parseShort(System.getProperty(property, value.toString())), func);
	}

	public static Option<Integer> as(String property, int defaultValue) {
		return as(property, defaultValue, (Consumer<Integer>) (_) -> {
		});
	}

	public static Option<Integer> as(String property, int defaultValue, Consumer<Integer> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Integer>(property, Integer.parseInt(System.getProperty(property, value.toString())), func);
	}

	public static Option<Long> as(String property, long defaultValue) {
		return as(property, defaultValue, (Consumer<Long>) (_) -> {
		});
	}

	public static Option<Long> as(String property, long defaultValue, Consumer<Long> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Long>(property, Long.parseLong(System.getProperty(property, value.toString())), func);
	}

	public static Option<Float> as(String property, float defaultValue) {
		return as(property, defaultValue, (Consumer<Float>) (_) -> {
		});
	}

	public static Option<Float> as(String property, float defaultValue, Consumer<Float> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Float>(property, Float.parseFloat(System.getProperty(property, value.toString())), func);
	}

	public static Option<Double> as(String property, double defaultValue) {
		return as(property, defaultValue, (Consumer<Double>) (_) -> {
		});
	}

	public static Option<Double> as(String property, double defaultValue, Consumer<Double> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Double>(property, Double.parseDouble(System.getProperty(property, value.toString())), func);
	}

	public static Option<Character> as(String property, char defaultValue) {
		return as(property, defaultValue, (Consumer<Character>) (_) -> {
		});
	}

	public static Option<Character> as(String property, char defaultValue, Consumer<Character> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Character>(property, System.getProperty(property, value.toString()).charAt(0), func);
	}

	public static Option<String> as(String property, String defaultValue) {
		return as(property, defaultValue, (_) -> {
		});
	}

	public static Option<String> as(String property, String defaultValue, Consumer<String> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<String>(property, System.getProperty(property, value.toString()), func);
	}

	public static boolean hasOption(String v) {
		if (v == null)
			return false;
		if (v.trim().length() == 0)
			return true;
		return Boolean.valueOf(v).booleanValue();
	}

	private static NumberFormat decimalFormat() {
		NumberFormat df;
		if (USE_NUMBER_FORMAT.asBoolean()) {
			char[] prec = new char[NUMBER_FORMAT_DECIMALS.asInt()];
			Arrays.fill(prec, '0');
			df = new DecimalFormat(" 0." + new String(prec) + "E0;-");
		} else {
			df = NumberFormat.getNumberInstance(Locale.ENGLISH);
			df.setGroupingUsed(false);
		}
		return df;
	}

	/**
	 * An option with a <code>string</code> key and any value. Changing an option
	 * will update config.ini file!
	 */
	public static class Option<T extends Object> {

		public final String key;
		private T value;
		private final Consumer<T> func;

		public Option(String key, T value) {
			this(key, value, (_) -> {
			});
		}

		public Option(String key, T value, Consumer<T> func) {
			this.key = key;
			this.value = value;
			this.func = func;
		}

		public void set(T value) {
			this.value = value;
			func.accept(value);
			OptionFile.setData(key, value);
		}

		public T value() {
			return value;
		}

		public boolean asBoolean() {
			return (boolean) value;
		}

		public short asShort() {
			return (short) value;
		}

		public int asInt() {
			return (int) value;
		}

		public long asLong() {
			return (long) value;
		}

		public float asFloat() {
			return (float) value;
		}

		public double asDouble() {
			return (double) value;
		}

		public char asChar() {
			return (char) value;
		}

		public String asString() {
			return (String) value;
		}

		@Override
		public String toString() {
			return key + ":" + value;
		}
	}

	/**
	 * Helper class for config.ini access.
	 */
	public static class OptionFile {

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
}
