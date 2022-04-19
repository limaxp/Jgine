package org.jgine.misc.math;

public class Geometric {

	public static double[] sphere(double radius, int upSize, int sideSize) {
		return sphere(radius, radius, upSize, sideSize);
	}

	public static double[] sphere(double radius, double height, int upSize, int sideSize) {
		double[] coordinates = new double[(upSize * (sideSize * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI; phi += FastMath.PI / upSize) {
			double y = height * FastMath.cos(phi);
			for (double theta = 0; theta < FastMath.PI * 2; theta += FastMath.PI / sideSize) {
				coordinates[i] = radius * FastMath.cos(theta) * FastMath.sin(phi);
				coordinates[i + 1] = y;
				coordinates[i + 2] = radius * FastMath.sin(theta) * FastMath.sin(phi);
				i += 3;
			}
		}
		return coordinates;
	}

	public static double[] sphereStep(double radius, int sideSize, double phi) {
		return sphereStep(radius, radius, sideSize, phi);
	}

	public static double[] sphereStep(double radius, double height, int sideSize, double phi) {
		double[] coordinates = new double[(1 + (sideSize * 2)) * 3];
		int i = 0;
		double y = height * FastMath.cos(phi);
		for (double theta = 0; theta <= FastMath.PI * 2; theta += FastMath.PI / sideSize) {
			coordinates[i] = radius * FastMath.cos(theta) * FastMath.sin(phi);
			coordinates[i + 1] = y;
			coordinates[i + 2] = radius * FastMath.sin(theta) * FastMath.sin(phi);
			i += 3;
		}
		return coordinates;
	}

	public static double[] halfSphere(double radius, int upSize, int sideSize) {
		return halfSphere(radius, radius, upSize, sideSize);
	}

	public static double[] halfSphere(double radius, double height, int upSize, int sideSize) {
		double[] coordinates = new double[(upSize * sideSize) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI; phi += FastMath.PI / upSize) {
			double y = height * FastMath.cos(phi);
			for (double theta = 0; theta < FastMath.PI; theta += FastMath.PI / sideSize) {
				coordinates[i] = radius * FastMath.cos(theta) * FastMath.sin(phi);
				coordinates[i + 1] = y;
				coordinates[i + 2] = radius * FastMath.sin(theta) * FastMath.sin(phi);
				i += 3;
			}
		}
		return coordinates;
	}

	public static double[] horizontalRing(double radius, int size) {
		double[] coordinates = new double[(1 + (size * 2)) * 2];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			coordinates[i] = radius * FastMath.cos(phi); // x
			coordinates[i + 1] = radius * FastMath.sin(phi); // z
			i += 2;
		}
		return coordinates;
	}

	public static double[] horizontalRingStep(double radius, int size, double phi) {
		double[] coordinates = new double[2];
		coordinates[0] = radius * FastMath.cos(phi); // x
		coordinates[1] = radius * FastMath.sin(phi); // z
		return coordinates;
	}

	public static double[] verticalRing(double radius, int size) {
		double[] coordinates = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			coordinates[i] = radius * FastMath.sin(phi);
			coordinates[i + 1] = radius * FastMath.cos(phi);
			coordinates[i + 2] = radius * FastMath.sin(phi);
			i += 3;
		}
		return coordinates;
	}

	public static double[] verticalRing(double radius, int size, double yaw) {
		double[] coordinates = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			coordinates[i] = radius * FastMath.cos(yaw) * FastMath.sin(phi);
			coordinates[i + 1] = radius * FastMath.cos(phi);
			coordinates[i + 2] = radius * FastMath.sin(yaw) * FastMath.sin(phi);
			i += 3;
		}
		return coordinates;
	}

	public static double[] verticalRingStep(double radius, int size, double phi) {
		double[] coordinates = new double[3];
		coordinates[0] = radius * FastMath.sin(phi);
		coordinates[1] = radius * FastMath.cos(phi);
		coordinates[2] = radius * FastMath.sin(phi);
		return coordinates;
	}

	public static double[] doubleRing(double radius, int size) {
		double[] coordinates = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi < FastMath.PI * 2; phi += FastMath.PI / size) {
			coordinates[i] = radius * FastMath.cos(phi);
			coordinates[i + 1] = FastMath.cos(phi) / 4;
			coordinates[i + 2] = radius * FastMath.sin(phi);
			i += 3;
		}
		return coordinates;
	}

	public static double[] doubleRingStep(double radius, double phi) {
		double[] coordinates = new double[3];
		coordinates[0] = radius * FastMath.cos(phi);
		coordinates[1] = FastMath.sin(phi) / 4;
		coordinates[2] = radius * FastMath.sin(phi);
		return coordinates;
	}

	public static double[] beam(double range, double yaw, double pitch) {
		double[] coordinates = new double[(int) (3 + 10 * range * 3)];
		double xzLen = FastMath.cos(pitch);
		double x = xzLen * FastMath.cos(yaw);
		double y = FastMath.sin(pitch);
		double z = xzLen * FastMath.sin(-yaw);
		int i = 0;
		for (double d = 0; d < range; d += 0.1) {
			coordinates[i] = x + d;
			coordinates[i + 1] = y + d;
			coordinates[i + 2] = z + d;
			i += 3;
		}
		return coordinates;
	}

	public static double[] wings(double radius, int size) {
		double[] coordinates = new double[(1 + (size * 2)) * 3];
		int i = 0;
		for (double phi = 0; phi <= FastMath.PI * 2; phi += FastMath.PI / size) {
			coordinates[i] = radius * FastMath.cos(phi);
			coordinates[i + 1] = radius * FastMath.sin(phi * 2) * FastMath.cos(phi);
			coordinates[i + 2] = radius * FastMath.cos(phi);
			i += 3;
		}
		return coordinates;
	}
}
