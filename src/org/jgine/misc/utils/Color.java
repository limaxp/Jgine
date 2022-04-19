package org.jgine.misc.utils;

/**
 * Helper class for color handling. Colors are represented as an integer
 * 
 * @author Maximilian Paar
 */
public class Color {

	public static final int RED = 0xffff0000;
	public static final int GREEN = 0xff00ff00;
	public static final int BLUE = 0xff0000ff;
	public static final int WHITE = 0xFFFFFFFF;
	public static final int BLACK = 0xff000000;

	public static int rgb(byte r, byte g, byte b) {
		return 0xff000000 | r << 16 | g << 8 | b << 0;
	}

	public static int rgb(int r, int g, int b) {
		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;
		return 0xff000000 | r << 16 | g << 8 | b << 0;
	}

	public static int rgba(byte r, byte g, byte b, byte a) {
		return 0x00000000 | a << 24 | r << 16 | g << 8 | b << 0;
	}

	public static int rgba(int r, int g, int b, int a) {
		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;
		if (a > 255)
			a = 255;
		return 0x00000000 | a << 24 | r << 16 | g << 8 | b << 0;
	}

	public static int getR(int color) {
		return ((color & 0x00ff0000) >> 16);
	}

	public static int getG(int color) {
		return ((color & 0x0000ff00) >> 8);
	}

	public static int getB(int color) {
		return ((color & 0x000000ff) >> 0);
	}

	public static int getA(int color) {
		return ((color & 0xff000000) >> 24);
	}

	public static String toString(int color) {
		return color + " [r = " + getR(color) + "| g = " + getG(color) + "| b = " + getB(color) + "| a = " + getA(color)
				+ "]";
	}
}
