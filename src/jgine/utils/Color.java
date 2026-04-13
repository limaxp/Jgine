package jgine.utils;

import java.nio.FloatBuffer;

/**
 * Helper class for color handling. Colors are represented as integers between 0
 * .. 255 and floats between 0.0 .. 1.0.
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
	public static final int DARKEST_GRAY = rgb(0.2f, 0.2f, 0.2f);
	public static final int LIGHT_GRAY = rgb(0.7f, 0.7f, 0.7f);
	public static final int PINK = rgb(255, 175, 175);
	public static final int ORANGE = rgb(255, 125, 0);
	public static final int MAGENTA = rgb(255, 0, 255);
	public static final int TRANSLUCENT_WEAK = rgba(1.0f, 1.0f, 1.0f, 0.2f);
	public static final int TRANSLUCENT_MID = rgba(1.0f, 1.0f, 1.0f, 0.4f);
	public static final int TRANSLUCENT_STRONG = rgba(1.0f, 1.0f, 1.0f, 0.6f);

	public static final float FLOAT_CONVERSION = 1.0f / 255.0f;

	public static int rgb(byte r, byte g, byte b) {
		return 0xff000000 | r << 16 | g << 8 | b;
	}

	public static int rgba(byte r, byte g, byte b, byte a) {
		return 0x00000000 | a << 24 | r << 16 | g << 8 | b;
	}

	public static int rgb(short r, short g, short b) {
		return 0xff000000 | r << 16 | g << 8 | b;
	}

	public static int rgba(short r, short g, short b, short a) {
		return 0x00000000 | a << 24 | r << 16 | g << 8 | b;
	}

	public static int rgb(int r, int g, int b) {
		return 0xff000000 | r << 16 | g << 8 | b;
	}

	public static int rgba(int r, int g, int b, int a) {
		return 0x00000000 | a << 24 | r << 16 | g << 8 | b;
	}

	public static int rgb(float r, float g, float b) {
		return 0xff000000 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
	}

	public static int rgba(float r, float g, float b, float a) {
		return 0x00000000 | (int) (a * 255) << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
	}

	public static int decode(String s) {
		return Integer.decode(s).intValue();
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

	public static int alpha(int color) {
		int a = (color & 0xff000000) >> 24;
		if (a < 0)
			a += 256;
		return a;
	}

	public static float red_(int color) {
		return (float) red(color) * FLOAT_CONVERSION;
	}

	public static float green_(int color) {
		return (float) green(color) * FLOAT_CONVERSION;
	}

	public static float blue_(int color) {
		return (float) blue(color) * FLOAT_CONVERSION;
	}

	public static float alpha_(int color) {
		return (float) alpha(color) * FLOAT_CONVERSION;
	}

	public static String toString(int color) {
		return color + "(" + red(color) + "," + green(color) + "," + blue(color) + "," + alpha(color) + ")";
	}

	public static FloatBuffer toRGBBuffer(FloatBuffer buffer, int color) {
		buffer.put(red_(color));
		buffer.put(green_(color));
		buffer.put(blue_(color));
		return buffer;
	}

	public static FloatBuffer toRGBABuffer(FloatBuffer buffer, int color) {
		buffer.put(red_(color));
		buffer.put(green_(color));
		buffer.put(blue_(color));
		buffer.put(alpha_(color));
		return buffer;
	}
}
