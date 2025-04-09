package org.jgine.system.systems.physic;

import org.jgine.utils.memory.ByteLayout;
import org.jgine.utils.memory.Struct;
import org.lwjgl.system.MemoryStack;

/**
 * Represents a physic object with position.
 */
public class PhysicObjectStruct extends Struct {

	public static final int SIZEOF;
	public static final int ALIGNOF;

	/** The struct member offsets. */
	public static final int X;
	public static final int Y;
	public static final int Z;

	static {
		ByteLayout layout = ByteLayout.struct(ByteLayout.member(4), ByteLayout.member(4), ByteLayout.member(4));
		SIZEOF = layout.size;
		ALIGNOF = layout.alignment;
		X = layout.offsetof(0);
		Y = layout.offsetof(1);
		Z = layout.offsetof(2);
	}

	public PhysicObjectStruct(MemoryStack stack) {
		super(stack, SIZEOF);
	}

	public PhysicObjectStruct() {
		super(SIZEOF);
	}

	public PhysicObjectStruct(long address) {
		super(address);
	}

	@Override
	public int sizeof() {
		return SIZEOF;
	}

	public PhysicObjectStruct x(float value) {
		UNSAFE.putFloat(null, address + X, value);
		return this;
	}

	public float x() {
		return UNSAFE.getFloat(null, address + X);
	}

	public PhysicObjectStruct y(float value) {
		UNSAFE.putFloat(null, address + Y, value);
		return this;
	}

	public float y() {
		return UNSAFE.getFloat(null, address + Y);
	}

	public PhysicObjectStruct z(float value) {
		UNSAFE.putFloat(null, address + Z, value);
		return this;
	}

	public float z() {
		return UNSAFE.getFloat(null, address + Z);
	}
}