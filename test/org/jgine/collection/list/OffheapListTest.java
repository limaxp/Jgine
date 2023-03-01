package org.jgine.collection.list;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jgine.collection.list.offheap.OffheapList;
import org.jgine.collection.list.offheap.OffheapObject;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.MemoryUtil;

public class OffheapListTest {

	@Test
	void whenAddingElements_ThenContainsThoseElements() {
		try (OffheapList<TestOffheapObject> offheapList = new OffheapList<TestOffheapObject>(TestOffheapObject.class)) {
			byte b = (byte) 155;
			short s = 22000;
			int i = 1000000;
			long l = 100000000000L;
			float f = 22.22f;
			double d = 33.3333333333333;
			TestOffheapObject in = new TestOffheapObject();
			in.setB(b);
			in.setS(s);
			in.setI(i);
			in.setL(l);
			in.setF(f);
			in.setD(d);

			assertEquals(in.getB(), b);
			assertEquals(in.getS(), s);
			assertEquals(in.getI(), i);
			assertEquals(in.getL(), l);
			assertEquals(in.getF(), f);
			assertEquals(in.getD(), d);

			offheapList.add(in);

			TestOffheapObject out = offheapList.get(0);
			assertEquals(out, in);

			assertEquals(out.getB(), b);
			assertEquals(out.getS(), s);
			assertEquals(out.getI(), i);
			assertEquals(out.getL(), l);
			assertEquals(out.getF(), f);
			assertEquals(out.getD(), d);

			assertEquals(out.getB(), in.getB());
			assertEquals(out.getS(), in.getS());
			assertEquals(out.getI(), in.getI());
			assertEquals(out.getL(), in.getL());
			assertEquals(out.getF(), in.getF());
			assertEquals(out.getD(), in.getD());
		}
	}

	public static class TestOffheapObject extends OffheapObject {

		private long address;

		public TestOffheapObject(long address) {
			this.address = address;
		}

		public TestOffheapObject() {
			address = MemoryUtil.nmemAlloc(29);
		}

		@Override
		public void setAddress(long address) {
			this.address = address;
		}

		@Override
		public long getAddress() {
			return address;
		}

		@Override
		public void write(long address) {
			setByte_(address + 0, getB());
			setShort_(address + 3, getS());
			setInt_(address + 5, getI());
			setLong_(address + 9, getL());
			setFloat_(address + 17, getF());
			setDouble_(address + 21, getD());
		}

		public void setB(byte b) {
			setByte(0, b);
		}

		public void setS(short s) {
			setShort(3, s);
		}

		public void setI(int i) {
			setInt(5, i);
		}

		public void setL(long l) {
			setLong(9, l);
		}

		public void setF(float f) {
			setFloat(17, f);
		}

		public void setD(double d) {
			setDouble(21, d);
		}

		public byte getB() {
			return getByte(0);
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
