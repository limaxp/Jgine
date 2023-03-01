package org.jgine.utils.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jgine.utils.math.vector.Vector3f;
import org.junit.jupiter.api.Test;

public class Vector3fTest {

	Vector3f a = new Vector3f(2.0f, 4.0f, 7.0f);
	Vector3f b = new Vector3f(12.0f);
	Vector3f c = new Vector3f(3.0f, 9.0f, 5.0f);

	@Test
	void add_Vector_Vector() {
		Vector3f vec = Vector3f.add(a, b);
		assertEquals(vec.x, a.x + b.x);
		assertEquals(vec.y, a.y + b.y);
		assertEquals(vec.z, a.z + b.z);
	}

	@Test
	void add_Vector_Float() {
		Vector3f vec = Vector3f.add(a, 5.0f);
		assertEquals(vec.x, a.x + 5.0f);
		assertEquals(vec.y, a.y + 5.0f);
		assertEquals(vec.z, a.z + 5.0f);
	}

	@Test
	void add_3Float_Float() {
		Vector3f vec = Vector3f.add(1.0f, 2.0f, 3.0f, 5.0f);
		assertEquals(vec.x, 1.0f + 5.0f);
		assertEquals(vec.y, 2.0f + 5.0f);
		assertEquals(vec.z, 3.0f + 5.0f);
	}

	@Test
	void add_Vector_3Float() {
		Vector3f vec = Vector3f.add(a, 1.0f, 2.0f, 3.0f);
		assertEquals(vec.x, a.x + 1.0f);
		assertEquals(vec.y, a.y + 2.0f);
		assertEquals(vec.z, a.z + 3.0f);
	}

	@Test
	void add_3Float_3Float() {
		Vector3f vec = Vector3f.add(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f);
		assertEquals(vec.x, 1.0f + 4.0f);
		assertEquals(vec.y, 2.0f + 5.0f);
		assertEquals(vec.z, 3.0f + 6.0f);
	}

	@Test
	void sub_Vector_Vector() {
		Vector3f vec = Vector3f.sub(a, b);
		assertEquals(vec.x, a.x - b.x);
		assertEquals(vec.y, a.y - b.y);
		assertEquals(vec.z, a.z - b.z);
	}

	@Test
	void sub_Vector_Float() {
		Vector3f vec = Vector3f.sub(a, 5.0f);
		assertEquals(vec.x, a.x - 5.0f);
		assertEquals(vec.y, a.y - 5.0f);
		assertEquals(vec.z, a.z - 5.0f);
	}

	@Test
	void sub_3Float_Float() {
		Vector3f vec = Vector3f.sub(1.0f, 2.0f, 3.0f, 5.0f);
		assertEquals(vec.x, 1.0f - 5.0f);
		assertEquals(vec.y, 2.0f - 5.0f);
		assertEquals(vec.z, 3.0f - 5.0f);
	}

	@Test
	void sub_Vector_3Float() {
		Vector3f vec = Vector3f.sub(a, 1.0f, 2.0f, 3.0f);
		assertEquals(vec.x, a.x - 1.0f);
		assertEquals(vec.y, a.y - 2.0f);
		assertEquals(vec.z, a.z - 3.0f);
	}

	@Test
	void sub_3Float_3Float() {
		Vector3f vec = Vector3f.sub(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f);
		assertEquals(vec.x, 1.0f - 4.0f);
		assertEquals(vec.y, 2.0f - 5.0f);
		assertEquals(vec.z, 3.0f - 6.0f);
	}

	@Test
	void mult_Vector_Float() {
		Vector3f vec = Vector3f.mult(a, 5.0f);
		assertEquals(vec.x, a.x * 5.0f);
		assertEquals(vec.y, a.y * 5.0f);
		assertEquals(vec.z, a.z * 5.0f);
	}

	@Test
	void mult_3Float_Float() {
		Vector3f vec = Vector3f.mult(1.0f, 2.0f, 3.0f, 5.0f);
		assertEquals(vec.x, 1.0f * 5.0f);
		assertEquals(vec.y, 2.0f * 5.0f);
		assertEquals(vec.z, 3.0f * 5.0f);
	}

	@Test
	void div_Vector_Float() {
		Vector3f vec = Vector3f.div(a, 5.0f);
		assertEquals(vec.x, a.x / 5.0f);
		assertEquals(vec.y, a.y / 5.0f);
		assertEquals(vec.z, a.z / 5.0f);
	}

	@Test
	void div_3Float_Float() {
		Vector3f vec = Vector3f.div(1.0f, 2.0f, 3.0f, 5.0f);
		assertEquals(vec.x, 1.0f / 5.0f);
		assertEquals(vec.y, 2.0f / 5.0f);
		assertEquals(vec.z, 3.0f / 5.0f);
	}

	@Test
	void dot_Vector_Vector() {
		double dot1 = Vector3f.dot(a, c);
		double dot2 = a.x * c.x + a.y * c.y + a.z * c.z;
		assertEquals(dot1, dot2);
	}

	@Test
	void dot_3Float_3Float() {
		double dot1 = Vector3f.dot(a.x, a.y, a.z, c.x, c.y, c.z);
		double dot2 = a.x * c.x + a.y * c.y + a.z * c.z;
		assertEquals(dot1, dot2);
	}

	@Test
	void cross_Vector_Vector() {
		Vector3f cross = Vector3f.cross(a, c);
		float xCross = a.y * c.z - a.z * c.y;
		float yCross = a.z * c.x - a.x * c.z;
		float zCross = a.x * c.y - a.y * c.x;
		assertEquals(cross.x, xCross);
		assertEquals(cross.y, yCross);
		assertEquals(cross.z, zCross);
	}

	@Test
	void cross_3Float_3Float() {
		Vector3f cross = Vector3f.cross(a.x, a.y, a.z, c.x, c.y, c.z);
		float xCross = a.y * c.z - a.z * c.y;
		float yCross = a.z * c.x - a.x * c.z;
		float zCross = a.x * c.y - a.y * c.x;
		assertEquals(cross.x, xCross);
		assertEquals(cross.y, yCross);
		assertEquals(cross.z, zCross);
	}
}
