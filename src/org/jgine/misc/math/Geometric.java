package org.jgine.misc.math;

public class Geometric {

	public static double[] sphere(double radius, int upSize, int sideSize) {
		return sphere(radius, radius, upSize, sideSize);
	}

	public static double[] sphere(double radius, double height, int upSize, int sideSize) {
		double[] result = new double[(upSize * (sideSize * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI; phi += FastMath.PI / upSize) {
			double y = height * FastMath.cos(phi);
			for (double theta = 0; theta < FastMath.PI * 2; theta += FastMath.PI / sideSize) {
				result[i] = radius * FastMath.cos(theta) * FastMath.sin(phi);
				result[i + 1] = y;
				result[i + 2] = radius * FastMath.sin(theta) * FastMath.sin(phi);
				i += 3;
			}
		}
		return result;
	}

	public static double[] sphereStep(double radius, int sideSize, double phi) {
		return sphereStep(radius, radius, sideSize, phi);
	}

	public static double[] sphereStep(double radius, double height, int sideSize, double phi) {
		double[] result = new double[(1 + (sideSize * 2)) * 3];
		int i = 0;
		double y = height * FastMath.cos(phi);
		for (double theta = 0; theta <= FastMath.PI * 2; theta += FastMath.PI / sideSize) {
			result[i] = radius * FastMath.cos(theta) * FastMath.sin(phi);
			result[i + 1] = y;
			result[i + 2] = radius * FastMath.sin(theta) * FastMath.sin(phi);
			i += 3;
		}
		return result;
	}

	public static double[] halfSphere(double radius, int upSize, int sideSize) {
		return halfSphere(radius, radius, upSize, sideSize);
	}

	public static double[] halfSphere(double radius, double height, int upSize, int sideSize) {
		double[] result = new double[(upSize * sideSize) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI; phi += FastMath.PI / upSize) {
			double y = height * FastMath.cos(phi);
			for (double theta = 0; theta < FastMath.PI; theta += FastMath.PI / sideSize) {
				result[i] = radius * FastMath.cos(theta) * FastMath.sin(phi);
				result[i + 1] = y;
				result[i + 2] = radius * FastMath.sin(theta) * FastMath.sin(phi);
				i += 3;
			}
		}
		return result;
	}

	public static double[] horizontalRing(double radius, int size) {
		double[] result = new double[(1 + (size * 2)) * 2];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			result[i] = radius * FastMath.cos(phi); // x
			result[i + 1] = radius * FastMath.sin(phi); // z
			i += 2;
		}
		return result;
	}

	public static double[] horizontalRingStep(double radius, int size, double phi) {
		double[] result = new double[2];
		result[0] = radius * FastMath.cos(phi); // x
		result[1] = radius * FastMath.sin(phi); // z
		return result;
	}

	public static double[] verticalRing(double radius, int size) {
		double[] result = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			result[i] = radius * FastMath.sin(phi);
			result[i + 1] = radius * FastMath.cos(phi);
			result[i + 2] = radius * FastMath.sin(phi);
			i += 3;
		}
		return result;
	}

	public static double[] verticalRing(double radius, int size, double yaw) {
		double[] result = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			result[i] = radius * FastMath.cos(yaw) * FastMath.sin(phi);
			result[i + 1] = radius * FastMath.cos(phi);
			result[i + 2] = radius * FastMath.sin(yaw) * FastMath.sin(phi);
			i += 3;
		}
		return result;
	}

	public static double[] verticalRingStep(double radius, int size, double phi) {
		double[] result = new double[3];
		result[0] = radius * FastMath.sin(phi);
		result[1] = radius * FastMath.cos(phi);
		result[2] = radius * FastMath.sin(phi);
		return result;
	}

	public static double[] doubleRing(double radius, int size) {
		double[] result = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			result[i] = radius * FastMath.cos(phi);
			result[i + 1] = FastMath.cos(phi) / 4;
			result[i + 2] = radius * FastMath.sin(phi);
			i += 3;
		}
		return result;
	}

	public static double[] doubleRingStep(double radius, double phi) {
		double[] result = new double[3];
		result[0] = radius * FastMath.cos(phi);
		result[1] = FastMath.sin(phi) / 4;
		result[2] = radius * FastMath.sin(phi);
		return result;
	}

	public static double[] beam(double range, double yaw, double pitch) {
		double[] result = new double[(int) (3 + 10 * range * 3)];
		double xzLen = FastMath.cos(pitch);
		double x = xzLen * FastMath.cos(yaw);
		double y = FastMath.sin(pitch);
		double z = xzLen * FastMath.sin(-yaw);
		int i = 0;
		for (double d = 0; d < range; d += 0.1) {
			result[i] = x + d;
			result[i + 1] = y + d;
			result[i + 2] = z + d;
			i += 3;
		}
		return result;
	}

	public static double[] wings(double radius, int size) {
		double[] result = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi <= FastMath.PI * 2; phi += FastMath.PI / size) {
			result[i] = radius * FastMath.cos(phi);
			result[i + 1] = radius * FastMath.sin(phi * 2) * FastMath.cos(phi);
			result[i + 2] = radius * FastMath.cos(phi);
			i += 3;
		}
		return result;
	}
}
