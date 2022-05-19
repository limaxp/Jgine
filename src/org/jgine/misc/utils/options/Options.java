package org.jgine.misc.utils.options;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.jgine.core.window.DisplayManager;

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
	 * Whether to use a {@link NumberFormat} producing scientific notation output
	 * when formatting matrix, vector and quaternion components to strings.
	 */
	public static final Option USE_NUMER_FORMAT = a("jgine.format.active", true);

	/**
	 * Determines the number of decimal digits produced in the formatted numbers.
	 */
	public static final Option NUMBER_FORMAT_DECIMALS = a("jgine.format.decimals", 3);

	/**
	 * The {@link NumberFormat} used to format all numbers throughout all JOML
	 * classes.
	 */
	public static final NumberFormat NUMBER_FORMAT = decimalFormat();

	public static final Option MAX_SOUNDS = a("jgine.sound.maxSounds", 512);

	public static final Option RESOLUTION_X = a("jgine.graphic.resolution.x",
			DisplayManager.getPrimaryDisplay().getWidth());

	public static final Option RESOLUTION_Y = a("jgine.graphic.resolution.y",
			DisplayManager.getPrimaryDisplay().getHeight());

	public static final Option MONITOR = a("jgine.graphic.monitor", 0);

	public static final boolean USE_COMPRESSED_OOPS = a("UseCompressedOops", false).getBoolean();

	public static Option a(String property, boolean defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, hasOption(System.getProperty(property, value.toString())));
	}

	public static Option a(String property, short defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, Short.parseShort(System.getProperty(property, value.toString())));
	}

	public static Option a(String property, int defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, Integer.parseInt(System.getProperty(property, value.toString())));
	}

	public static Option a(String property, long defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, Long.parseLong(System.getProperty(property, value.toString())));
	}

	public static Option a(String property, float defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, Float.parseFloat(System.getProperty(property, value.toString())));
	}

	public static Option a(String property, double defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, Double.parseDouble(System.getProperty(property, value.toString())));
	}

	public static Option a(String property, char defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, System.getProperty(property, value.toString()).charAt(0));
	}

	public static Option a(String property, String defaultValue) {
		Object value = OptionFile.getData(property, defaultValue);
		return new Option(property, System.getProperty(property, value.toString()));
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