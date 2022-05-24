package org.jgine.misc.utils;

import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector3i;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.math.vector.Vector4i;

/**
 * Helper class for color handling. Colors are represented as an integer
 * 
 * @author Maximilian Paar
 */
public class Color {

	public static final int TRANSPARENT = 0x00000000;
	public static final int RED = 0xffff0000;
	public static final int GREEN = 0xff00ff00;
	public static final int BLUE = 0xff0000ff;
	public static final int WHITE = 0xffffffff;
	public static final int BLACK = 0xff000000;
	public static final int YELLOW = 0xffffff00;
	public static final int CYAN = 0xff00ffff;
	public static final int GRAY = rgb(0.5f, 0.5f, 0.5f);
	public static final int DARK_GRAY = rgb(0.3f, 0.3f, 0.3f);
	public static final int LIGHT_GRAY = rgb(0.7f, 0.7f, 0.7f);
	public static final int PINK = rgb(255, 175, 175);
	public static final int ORANGE = rgb(255, 200, 0);
	public static final int MAGENTA = rgb(255, 0, 255);

	public static int rgb(byte r, byte g, byte b) {
		return 0xff000000 | r << 16 | g << 8 | b << 0;
	}

	public static int rgba(byte r, byte g, byte b, byte a) {
		return 0x00000000 | a << 24 | r << 16 | g << 8 | b << 0;
	}

	public static int rgb(int r, int g, int b) {
		return 0xff000000 | r << 16 | g << 8 | b << 0;
	}

	public static int rgb(Vector3i vec) {
		return rgb(vec.x, vec.y, vec.z);
	}

	public static int rgba(int r, int g, int b, int a) {
		return 0x00000000 | a << 24 | r << 16 | g << 8 | b << 0;
	}

	public static int rgba(Vector4i vec) {
		return rgba(vec.x, vec.y, vec.z, vec.w);
	}

	public static int rgb(float r, float g, float b) {
		return 0xff000000 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255) << 0;
	}

	public static int rgb(Vector3f vec) {
		return rgb(vec.x, vec.y, vec.z);
	}

	public static int rgba(float r, float g, float b, float a) {
		return 0x00000000 | (int) (a * 255) << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255) << 0;
	}

	public static int rgba(Vector4f vec) {
		return rgba(vec.x, vec.y, vec.z, vec.w);
	}

	public static int decode(String s) {
		return Integer.decode(s).intValue();
	}

	public static int alpha(int color) {
		return (color & 0xff000000) >> 24;
	}

	public static int red(int color) {
		return (color & 0x00ff0000) >> 16;
	}

	public static int green(int color) {
		return (color & 0x0000ff00) >> 8;
	}

	public static int blue(int color) {
		return (color & 0x000000ff);
	}

	public static String toString(int color) {
		return color + " [" + red(color) + "," + green(color) + "," + blue(color) + "," + alpha(color) + "]";
	}

	public static Vector4f toVector(int color) {
		return new Vector4f(red(color) / 255, green(color) / 255, blue(color) / 255, alpha(color) / 255);
	}
}
