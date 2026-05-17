package jgine.utils;

import java.util.Arrays;

public final class ArrayUtils {

	public static byte[] append(byte[] a, byte value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static char[] append(char[] a, char value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static short[] append(short[] a, short value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static int[] append(int[] a, int value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static long[] append(long[] a, long value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static float[] append(float[] a, float value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static double[] append(double[] a, double value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static <T> T[] append(T[] a, T value) {
		int size = a.length;
		a = Arrays.copyOf(a, size + 1);
		a[size] = value;
		return a;
	}

	public static boolean contains(byte[] a, byte value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}

	public static boolean contains(char[] a, char value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}

	public static boolean contains(short[] a, short value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}

	public static boolean contains(int[] a, int value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}

	public static boolean contains(long[] a, long value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}

	public static boolean contains(float[] a, float value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}

	public static boolean contains(double[] a, double value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}

	public static <T> boolean contains(T[] a, T value) {
		int size = a.length;
		for (int i = 0; i < size; i++)
			if (a[i] == value)
				return true;
		return false;
	}
}
