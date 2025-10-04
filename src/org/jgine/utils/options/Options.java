package org.jgine.utils.options;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

import org.jgine.core.window.DisplayManager;
import org.jgine.core.window.Window;

public class Options {

	/**
	 * Whether certain debugging checks should be made.
	 */
	public static final boolean DEBUG = a("jgine.debug", false).getBoolean();

	/**
	 * Whether the engine systems update synchron or asynchron.
	 */
	public static final boolean SYNCHRONIZED = a("jgine.synchronized", false).getBoolean();

	/**
	 * Whether fast approximations of some java.lang.Math operations should be used.
	 */
	public static final boolean FASTMATH = a("jgine.fastmath", false).getBoolean();

	/**
	 * When {@link #FASTMATH} is <code>true</code>, whether to use a lookup table
	 * for sin/cos.
	 */
	public static final boolean SIN_LOOKUP = a("jgine.sinLookup.active", false).getBoolean();

	/**
	 * When {@link #SIN_LOOKUP} is <code>true</code>, this determines the table
	 * size.
	 */
	public static final int SIN_LOOKUP_BITS = a("jgine.sinLookup.bits", 14).getInt();

	/**
	 * Whether to try using java.lang.Math.fma() in most matrix/vector/quaternion
	 * operations if it is available. If the CPU does <i>not</i> support it, it will
	 * be a lot slower than `a*b+c` and potentially generate a lot of memory
	 * allocations for the emulation with `java.util.BigDecimal`, though.
	 */
	public static final boolean USE_MATH_FMA = a("jgine.useMathFma", false).getBoolean();

	/**
	 * The maximal amount of sounds that can be played simultaneously.
	 */
	public static final Option<Integer> MAX_SOUNDS = a("jgine.sound.maxSounds", 512);

	/**
	 * The x resolution used.
	 */
	public static final Option<Integer> RESOLUTION_X = a("jgine.graphic.resolution.x",
			DisplayManager.getPrimaryDisplay().getWidth());

	/**
	 * The y resolution used.
	 */
	public static final Option<Integer> RESOLUTION_Y = a("jgine.graphic.resolution.y",
			DisplayManager.getPrimaryDisplay().getHeight());

	/**
	 * The monitor used.
	 */
	public static final Option<Integer> MONITOR = a("jgine.graphic.monitor", 0);

	/**
	 * The window mode used.
	 */
	public static final Option<Integer> WINDOW_MODE = a("jgine.graphic.mode", Window.Mode.FULLSCREEN);

	/**
	 * If v-sync should be applied.
	 */
	public static final Option<Boolean> V_SYNC = a("jgine.graphic.v-sync", true, Window::v_sync);

	/**
	 * The anti aliasing factor used.
	 */
	public static final Option<Integer> ANTI_ALIASING = a("jgine.graphic.antialiasing", 4);

	/**
	 * Whether to use a {@link NumberFormat} producing scientific notation output
	 * when formatting matrix, vector and quaternion components to strings.
	 */
	public static final Option<Boolean> USE_NUMER_FORMAT = a("jgine.format.active", true);

	/**
	 * Determines the number of decimal digits produced in the formatted numbers.
	 */
	public static final Option<Integer> NUMBER_FORMAT_DECIMALS = a("jgine.format.decimals", 3);

	/**
	 * The {@link NumberFormat} used to format all numbers throughout all JOML
	 * classes.
	 */
	public static final NumberFormat NUMBER_FORMAT = decimalFormat();

	public static Option<Boolean> a(String property, boolean defaultValue) {
		return a(property, defaultValue, (v) -> {
		});
	}

	public static Option<Boolean> a(String property, boolean defaultValue, Consumer<Boolean> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Boolean>(property, hasOption(System.getProperty(property, value.toString())), func);
	}

	public static Option<Short> a(String property, short defaultValue) {
		return a(property, defaultValue, (Consumer<Short>) (v) -> {
		});
	}

	public static Option<Short> a(String property, short defaultValue, Consumer<Short> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Short>(property, Short.parseShort(System.getProperty(property, value.toString())), func);
	}

	public static Option<Integer> a(String property, int defaultValue) {
		return a(property, defaultValue, (Consumer<Integer>) (v) -> {
		});
	}

	public static Option<Integer> a(String property, int defaultValue, Consumer<Integer> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Integer>(property, Integer.parseInt(System.getProperty(property, value.toString())), func);
	}

	public static Option<Long> a(String property, long defaultValue) {
		return a(property, defaultValue, (Consumer<Long>) (v) -> {
		});
	}

	public static Option<Long> a(String property, long defaultValue, Consumer<Long> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Long>(property, Long.parseLong(System.getProperty(property, value.toString())), func);
	}

	public static Option<Float> a(String property, float defaultValue) {
		return a(property, defaultValue, (Consumer<Float>) (v) -> {
		});
	}

	public static Option<Float> a(String property, float defaultValue, Consumer<Float> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Float>(property, Float.parseFloat(System.getProperty(property, value.toString())), func);
	}

	public static Option<Double> a(String property, double defaultValue) {
		return a(property, defaultValue, (Consumer<Double>) (v) -> {
		});
	}

	public static Option<Double> a(String property, double defaultValue, Consumer<Double> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Double>(property, Double.parseDouble(System.getProperty(property, value.toString())), func);
	}

	public static Option<Character> a(String property, char defaultValue) {
		return a(property, defaultValue, (Consumer<Character>) (v) -> {
		});
	}

	public static Option<Character> a(String property, char defaultValue, Consumer<Character> func) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option<Character>(property, System.getProperty(property, value.toString()).charAt(0), func);
	}

	public static Option<String> a(String property, String defaultValue) {
		return a(property, defaultValue, (v) -> {
		});
	}

	public static Option<String> a(String property, String defaultValue, Consumer<String> func) {
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
		if (USE_NUMER_FORMAT.getBoolean()) {
			char[] prec = new char[NUMBER_FORMAT_DECIMALS.getInt()];
			Arrays.fill(prec, '0');
			df = new DecimalFormat(" 0." + new String(prec) + "E0;-");
		} else {
			df = NumberFormat.getNumberInstance(Locale.ENGLISH);
			df.setGroupingUsed(false);
		}
		return df;
	}
}
