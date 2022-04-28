package org.jgine.misc.collection.list;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.jgine.misc.collection.list.offheap.OffheapList;
import org.jgine.misc.collection.list.offheap.OffheapObject;
import org.junit.jupiter.api.Test;

public class OffheapListTest {

	@Test
	void whenAddingElements_ThenContainsThoseElements() {
		try (OffheapList<TestOffheapObject> offheapList = new OffheapList<TestOffheapObject>(TestOffheapObject.class)) {
			TestOffheapObjectPrototype in = new TestOffheapObjectPrototype();
			in.b = (byte) 155;
			in.c = 'h';
			in.s = 22000;
			in.i = 1000000;
			in.l = 100000000000L;
			in.f = 22.22f;
			in.d = 33.3333333333333;
			offheapList.add(in);

			TestOffheapObject out = offheapList.get(0);
			assertEquals(out, in);
			assertEquals(out.b, in.b);
			assertEquals(out.c, in.c);
			assertEquals(out.s, in.s);
			assertEquals(out.i, in.i);
			assertEquals(out.l, in.l);
			assertEquals(out.f, in.f);
			assertEquals(out.d, in.d);

			assertEquals(out.getB(), in.getB());
			assertEquals(out.getC(), in.getC());
			assertEquals(out.getS(), in.getS());
			assertEquals(out.getI(), in.getI());
			assertEquals(out.getL(), in.getL());
			assertEquals(out.getF(), in.getF());
			assertEquals(out.getD(), in.getD());

			assertEquals(out.getB(), in.b);
			assertEquals(out.getC(), in.c);
			assertEquals(out.getS(), in.s);
			assertEquals(out.getI(), in.i);
			assertEquals(out.getL(), in.l);
			assertEquals(out.getF(), in.f);
			assertEquals(out.getD(), in.d);
		}
	}

	public static class TestOffheapObjectPrototype extends TestOffheapObject {

	}

	public static class TestOffheapObject extends OffheapObject {

		byte b;
		char c;
		short s;
		int i;
		long l;
		float f;
		double d;

		@Override
		public void save(ByteBuffer buffer) {
			setB(b);
			setC(c);
			setS(s);
			setI(i);
			setL(l);
			setF(f);
			setD(d);
		}

		public void setB(byte b) {
			putByte(0, b);
		}

		public void setC(char c) {
			putChar(1, c);
		}

		public void setS(short s) {
			putShort(3, s);
		}

		public void setI(int i) {
			putInt(5, i);
		}

		public void setL(long l) {
			putLong(9, l);
		}

		public void setF(float f) {
			putFloat(17, f);
		}

		public void setD(double d) {
			putDouble(21, d);
		}

		public byte getB() {
			return getByte(0);
		}

		public char getC() {
			return getChar(1);
		}

		public short getS() {
			return getShort(3);
		}

		public int getI() {
			return getInt(5);
		}

		public long getL() {
			return getLong(9);
		}

		public float getF() {
			return getFloat(17);
		}

		public double getD() {
			return getDouble(21);
		}
	}
}
