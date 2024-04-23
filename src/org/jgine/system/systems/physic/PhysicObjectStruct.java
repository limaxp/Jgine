package org.jgine.system.systems.physic;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memCopy;

import java.nio.ByteBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.memory.ByteLayout;
import org.jgine.utils.memory.NativeResource;
import org.jgine.utils.memory.Struct;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.StructBuffer;

/**
 * Represents a physic object with position.
 * 
 * <h3>Layout</h3>
 * 
 * <pre>
 * <code>
 * struct physicObjectStruct {
 *     float {@link #x};
 *     float {@link #y};
 *     float {@link #z};
 * }</code>
 * </pre>
 */
@NativeType("struct physicObjectStruct")
public class PhysicObjectStruct extends Struct implements NativeResource {

	/** The struct size in bytes. */
	public static final int SIZEOF;

	/** The struct alignment in bytes. */
	public static final int ALIGNOF;

	/** The struct member offsets. */
	public static final int X, Y, Z;

	static {
		ByteLayout layout = ByteLayout.struct(ByteLayout.member(4), ByteLayout.member(4), ByteLayout.member(4));
		SIZEOF = layout.size;
		ALIGNOF = layout.alignment;
		X = layout.offsetof(0);
		Y = layout.offsetof(1);
		Z = layout.offsetof(2);
	}

	public PhysicObjectStruct() {
		this(MemoryUtil.memAlloc(SIZEOF));
	}

	public PhysicObjectStruct(ByteBuffer container) {
		super(memAddress(container), container);
	}

	/**
	 * Returns a new {@code PhysicObjectStruct} instance allocated with
	 * {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
	 */
//	public static PhysicObjectStruct malloc() {
//		return wrap(PhysicObjectStruct.class, nmemAllocChecked(SIZEOF));
//	}
//
//	/**
//	 * Returns a new {@code PhysicObjectStruct} instance allocated with
//	 * {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly
//	 * freed.
//	 */
//	public static PhysicObjectStruct calloc() {
//		return wrap(PhysicObjectStruct.class, nmemCallocChecked(1, SIZEOF));
//	}
//
//	/**
//	 * Returns a new {@code PhysicObjectStruct} instance allocated with
//	 * {@link BufferUtils}.
//	 */
//	public static PhysicObjectStruct create() {
//		ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
//		return wrap(PhysicObjectStruct.class, memAddress(container), container);
//	}
//
//	/**
//	 * Returns a new {@code PhysicObjectStruct} instance for the specified memory
//	 * address.
//	 */
//	public static PhysicObjectStruct create(long address) {
//		return wrap(PhysicObjectStruct.class, address);
//	}
//
//	/**
//	 * Like {@link #create(long) create}, but returns {@code null} if
//	 * {@code address} is {@code NULL}.
//	 */
//	@Nullable
//	public static PhysicObjectStruct createSafe(long address) {
//		return address == NULL ? null : wrap(PhysicObjectStruct.class, address);
//	}
//
//	/**
//	 * Returns a new {@link PhysicObjectStruct.Buffer} instance allocated with
//	 * {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
//	 *
//	 * @param capacity the buffer capacity
//	 */
//	public static PhysicObjectStruct.Buffer malloc(int capacity) {
//		return wrap(Buffer.class, nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
//	}
//
//	/**
//	 * Returns a new {@link PhysicObjectStruct.Buffer} instance allocated with
//	 * {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly
//	 * freed.
//	 *
//	 * @param capacity the buffer capacity
//	 */
//	public static PhysicObjectStruct.Buffer calloc(int capacity) {
//		return wrap(Buffer.class, nmemCallocChecked(capacity, SIZEOF), capacity);
//	}
//
//	/**
//	 * Returns a new {@link PhysicObjectStruct.Buffer} instance allocated with
//	 * {@link BufferUtils}.
//	 *
//	 * @param capacity the buffer capacity
//	 */
//	public static PhysicObjectStruct.Buffer create(int capacity) {
//		ByteBuffer container = __create(capacity, SIZEOF);
//		return wrap(Buffer.class, memAddress(container), capacity, container);
//	}
//
//	/**
//	 * Create a {@link PhysicObjectStruct.Buffer} instance at the specified memory.
//	 *
//	 * @param address  the memory address
//	 * @param capacity the buffer capacity
//	 */
//	public static PhysicObjectStruct.Buffer create(long address, int capacity) {
//		return wrap(Buffer.class, address, capacity);
//	}
//
//	/**
//	 * Like {@link #create(long, int) create}, but returns {@code null} if
//	 * {@code address} is {@code NULL}.
//	 */
//	// TODO update java
////	@Nullable
//	public static PhysicObjectStruct.Buffer createSafe(long address, int capacity) {
//		return address == NULL ? null : wrap(Buffer.class, address, capacity);
//	}
//
//	/**
//	 * Returns a new {@code PhysicObjectStruct} instance allocated on the specified
//	 * {@link MemoryStack}.
//	 *
//	 * @param stack the stack from which to allocate
//	 */
//	public static PhysicObjectStruct malloc(MemoryStack stack) {
//		return wrap(PhysicObjectStruct.class, stack.nmalloc(ALIGNOF, SIZEOF));
//	}
//
//	/**
//	 * Returns a new {@code PhysicObjectStruct} instance allocated on the specified
//	 * {@link MemoryStack} and initializes all its bits to zero.
//	 *
//	 * @param stack the stack from which to allocate
//	 */
//	public static PhysicObjectStruct calloc(MemoryStack stack) {
//		return wrap(PhysicObjectStruct.class, stack.ncalloc(ALIGNOF, 1, SIZEOF));
//	}
//
//	/**
//	 * Returns a new {@link PhysicObjectStruct.Buffer} instance allocated on the
//	 * specified {@link MemoryStack}.
//	 *
//	 * @param stack    the stack from which to allocate
//	 * @param capacity the buffer capacity
//	 */
//	public static PhysicObjectStruct.Buffer malloc(int capacity, MemoryStack stack) {
//		return wrap(Buffer.class, stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
//	}
//
//	/**
//	 * Returns a new {@link PhysicObjectStruct.Buffer} instance allocated on the
//	 * specified {@link MemoryStack} and initializes all its bits to zero.
//	 *
//	 * @param stack    the stack from which to allocate
//	 * @param capacity the buffer capacity
//	 */
//	public static PhysicObjectStruct.Buffer calloc(int capacity, MemoryStack stack) {
//		return wrap(Buffer.class, stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
//	}
//
	@Override
	public int sizeof() {
		return SIZEOF;
	}

	public float x() {
		return nx(address);
	}

	public float y() {
		return ny(address);
	}

	public float z() {
		return nz(address);
	}

	public PhysicObjectStruct x(float value) {
		nx(address, value);
		return this;
	}

	public PhysicObjectStruct y(float value) {
		ny(address, value);
		return this;
	}

	public PhysicObjectStruct z(float value) {
		nz(address, value);
		return this;
	}

	public PhysicObjectStruct set(float x, float y, float z) {
		x(x);
		y(y);
		z(z);
		return this;
	}

	public PhysicObjectStruct set(PhysicObjectStruct src) {
		memCopy(src.address, address, SIZEOF);
		return this;
	}

	public static float nx(long struct) {
		return UNSAFE.getFloat(null, struct + PhysicObjectStruct.X);
	}

	public static float ny(long struct) {
		return UNSAFE.getFloat(null, struct + PhysicObjectStruct.Y);
	}

	public static float nz(long struct) {
		return UNSAFE.getFloat(null, struct + PhysicObjectStruct.Z);
	}

	public static void nx(long struct, float value) {
		UNSAFE.putFloat(null, struct + PhysicObjectStruct.X, value);
	}

	public static void ny(long struct, float value) {
		UNSAFE.putFloat(null, struct + PhysicObjectStruct.Y, value);
	}

	public static void nz(long struct, float value) {
		UNSAFE.putFloat(null, struct + PhysicObjectStruct.Z, value);
	}

//	/** An array of {@link PhysicObjectStruct} structs. */
//	public static class Buffer extends StructBuffer<PhysicObjectStruct, Buffer> implements NativeResource {
//
//		private static final PhysicObjectStruct ELEMENT_FACTORY = PhysicObjectStruct.create(-1L);
//
//		/**
//		 * Creates a new {@code PhysicObjectStruct.Buffer} instance backed by the
//		 * specified container.
//		 *
//		 * Changes to the container's content will be visible to the struct buffer
//		 * instance and vice versa. The two buffers' position, limit, and mark values
//		 * will be independent. The new buffer's position will be zero, its capacity and
//		 * its limit will be the number of bytes remaining in this buffer divided by
//		 * {@link PhysicObjectStruct#SIZEOF}, and its mark will be undefined.
//		 *
//		 * <p>
//		 * The created buffer instance holds a strong reference to the container object.
//		 * </p>
//		 */
//		public Buffer(ByteBuffer container) {
//			super(container, container.remaining() / SIZEOF);
//		}
//
//		public Buffer(long address, int cap) {
//			super(address, null, -1, 0, cap, cap);
//		}
//
//		Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
//			super(address, container, mark, pos, lim, cap);
//		}
//
//		@Override
//		protected Buffer self() {
//			return this;
//		}
//
//		@Override
//		protected PhysicObjectStruct getElementFactory() {
//			return ELEMENT_FACTORY;
//		}
//
//		public float x() {
//			return PhysicObjectStruct.nx(address());
//		}
//
//		public float y() {
//			return PhysicObjectStruct.ny(address());
//		}
//
//		public float z() {
//			return PhysicObjectStruct.nz(address());
//		}
//
//		public PhysicObjectStruct.Buffer x(float value) {
//			PhysicObjectStruct.nx(address(), value);
//			return this;
//		}
//
//		public PhysicObjectStruct.Buffer y(float value) {
//			PhysicObjectStruct.ny(address(), value);
//			return this;
//		}
//
//		public PhysicObjectStruct.Buffer z(float value) {
//			PhysicObjectStruct.nz(address(), value);
//			return this;
//		}
//	}
}
