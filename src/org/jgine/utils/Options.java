package org.jgine.utils;

import org.jgine.core.window.DisplayManager;
import org.jgine.core.window.Window;

import maxLibs.utils.options.Option;

/**
 * Helper class for {@link Option} registration. A registered {@link Option}
 * will check config.ini file and use its value if it exists otherwise it will
 * uses the default value and save it to file. {@link Option}<code>s</code> can
 * be overridden with VM arguments.
 */
public class Options extends maxLibs.utils.options.Options {

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
	public static final Option MAX_SOUNDS = a("jgine.sound.maxSounds", 512);

	/**
	 * The x resolution used.
	 */
	public static final Option RESOLUTION_X = a("jgine.graphic.resolution.x",
			DisplayManager.getPrimaryDisplay().getWidth());

	/**
	 * The y resolution used.
	 */
	public static final Option RESOLUTION_Y = a("jgine.graphic.resolution.y",
			DisplayManager.getPrimaryDisplay().getHeight());

	/**
	 * The monitor used.
	 */
	public static final Option MONITOR = a("jgine.graphic.monitor", 0);

	/**
	 * The window mode used.
	 */
	public static final Option WINDOW_MODE = a("jgine.graphic.mode", Window.Mode.WINDOWED);

	/**
	 * The anti aliasing factor used.
	 */
	public static final Option ANTI_ALIASING = a("jgine.graphic.antialiasing", 4);
}