package org.jgine.render.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glGetBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_LINE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.utils.BufferHelper;
import org.jgine.utils.math.vector.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Represents an indexed Mesh!
 *
 */
public class Mesh implements AutoCloseable {

	public static final int STATIC = GL_STATIC_DRAW;
	public static final int DYNAMIC = GL_DYNAMIC_DRAW;

	public static final int POINTS = GL_POINTS;
	public static final int LINES = GL_LINES;
	public static final int LINE_LOOP = GL_LINE_LOOP;
	public static final int LINE_STRIP = GL_LINE_STRIP;
	public static final int TRIANGLES = GL_TRIANGLES;
	public static final int TRIANGLE_STRIP = GL_TRIANGLE_STRIP;
	public static final int TRIANGLE_FAN = GL_TRIANGLE_FAN;
	public static final int QUADS = GL_QUADS;
	public static final int LINES_ADJACENCY = GL_LINES_ADJACENCY;
	public static final int LINE_STRIP_ADJACENCY = GL_LINE_STRIP_ADJACENCY;
	public static final int TRIANGLES_ADJACENCY = GL_TRIANGLES_ADJACENCY;
	public static final int TRIANGLE_STRIP_ADJACENCY = GL_TRIANGLE_STRIP_ADJACENCY;
	public static final int PATCHES = GL_PATCHES;

	public static final int TEXT_CORD_SIZE = 2;

	public final int type;
	public int mode;
	protected int vao;
	protected int vbo;
	protected int ibo;
	protected int size;
	protected int vertexSize;

	public Mesh(int dimension, boolean hasNormals) {
		this(dimension, STATIC, hasNormals);
	}

	public Mesh(int dimension, int type, boolean hasNormals) {
		this.type = type;
		mode = TRIANGLES;
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ibo = glGenBuffers();

		vertexSize = dimension + TEXT_CORD_SIZE;
		if (hasNormals)
			vertexSize += dimension;
		glBindVertexArray(vao);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, dimension, GL_FLOAT, false, vertexSize * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, vertexSize * Float.BYTES, dimension * Float.BYTES);
		if (hasNormals) {
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(2, dimension, GL_FLOAT, false, vertexSize * Float.BYTES,
					(dimension + TEXT_CORD_SIZE) * Float.BYTES);
		}
		glBindVertexArray(0);
	}

	@Override
	public void close() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ibo);
		vao = 0;
		vbo = 0;
		ibo = 0;
	}

	public boolean isClosed() {
		return vao == 0;
	}

	public void loadData(AIMesh mesh) {
		int faceCount = mesh.mNumFaces();
		IntBuffer indices = MemoryUtil.memAllocInt(faceCount * 3);
		AIFace.Buffer facesBuffer = mesh.mFaces();
		for (int i = 0; i < faceCount; ++i) {
			AIFace face = facesBuffer.get(i);
			if (face.mNumIndices() != 3) {
				throw new IllegalStateException("AIFace.mNumIndices() != 3");
			}
			indices.put(face.mIndices());
		}
		indices.flip();

		PointerBuffer aiColors = mesh.mColors();
		while (aiColors.remaining() > 0) {
//			long aiColor = aiColors.get();
			// TODO use colors!
		}

		PointerBuffer aiBones = mesh.mBones();
		if (aiBones != null) {
			while (aiBones.remaining() > 0) {
//				long aiBone = aiBones.get();
				// TODO use bones!
			}
		}

		PointerBuffer aiAnimMeshes = mesh.mAnimMeshes();
		if (aiAnimMeshes != null) {
			while (aiAnimMeshes.remaining() > 0) {
//				long aiAnimMesh = aiAnimMeshes.get();
				// TODO use animation meshes!
			}
		}

//		AIAABB aiAABB = mesh.mAABB();
		// TODO use Axial Aligned Bounding Box!

		loadData(mesh.mVertices(), indices, mesh.mTextureCoords(0), mesh.mNormals());
		MemoryUtil.memFree(indices);
	}

	public void loadData(AIVector3D.Buffer vertices, IntBuffer indices, AIVector3D.Buffer textureChords,
			AIVector3D.Buffer normals) {
		if (normals == null || normals.remaining() == 0) {
			normals = calculateNormals(vertices, indices);
			loadData_(vertices, indices, textureChords, normals);
			MemoryUtil.memFree(normals);
			return;
		}
		loadData_(vertices, indices, textureChords, normals);
	}

	private final void loadData_(AIVector3D.Buffer vertices, IntBuffer indices, AIVector3D.Buffer textureChords,
			AIVector3D.Buffer normals) {
		loadVertices(BufferHelper.createFloatBuffer(3, TEXT_CORD_SIZE, 3, vertices, textureChords, normals));
		loadIndices(indices);
	}

	public void loadVertices(float[] vertices, int[] indices, @Nullable float[] textureChords,
			@Nullable float[] normals) {
		loadVertices(FloatBuffer.wrap(vertices), IntBuffer.wrap(indices),
				textureChords != null ? FloatBuffer.wrap(textureChords) : null,
				normals != null ? FloatBuffer.wrap(normals) : null);
	}

	public void loadVertices(FloatBuffer vertices, IntBuffer indices, @Nullable FloatBuffer textureChords,
			@Nullable FloatBuffer normals) {
		int dimension = getDimension();
		if (textureChords == null || textureChords.remaining() == 0)
			textureChords = generateTextureChords(dimension, vertices);

		if (normals == null || normals.remaining() == 0)
			if (dimension == 3)
				normals = calculateNormals3d(vertices, indices);
			else if (dimension == 2)
				normals = calculateNormals2d(vertices, indices);

		int dataSize = dimension + TEXT_CORD_SIZE + dimension;
		int vertexSize = vertices.remaining() / dimension;
		FloatBuffer combinedVertices = BufferUtils.createFloatBuffer(dataSize * vertexSize);
		if (dimension == 3) {
			for (int i = 0; i < vertexSize; i++) {
				combinedVertices.put(vertices.get());
				combinedVertices.put(vertices.get());
				combinedVertices.put(vertices.get());
				combinedVertices.put(textureChords.get());
				combinedVertices.put(textureChords.get());
				combinedVertices.put(normals.get());
				combinedVertices.put(normals.get());
				combinedVertices.put(normals.get());
			}
		} else if (dimension == 2) {
			for (int i = 0; i < vertexSize; i++) {
				combinedVertices.put(vertices.get());
				combinedVertices.put(vertices.get());
				combinedVertices.put(textureChords.get());
				combinedVertices.put(textureChords.get());
				combinedVertices.put(normals.get());
				combinedVertices.put(normals.get());
			}
		}
		combinedVertices.flip();
		loadVertices(combinedVertices);
		loadIndices(indices);
	}

	public final void loadVertices(FloatBuffer vertices) {
		size = vertices.remaining() / vertexSize;
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, type);
	}

	public final void setVertices(FloatBuffer data) {
		setVertices(0, data);
	}

	public final void setVertices(int index, FloatBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferSubData(GL_ARRAY_BUFFER, index * vertexSize * Float.BYTES, data);
	}

	public final FloatBuffer getVertices(FloatBuffer target) {
		return getVertices(0, target);
	}

	public final FloatBuffer getVertices(int index, FloatBuffer target) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glGetBufferSubData(GL_ARRAY_BUFFER, index * vertexSize * Float.BYTES, target);
		return target;
	}

	public final void setVertex(int index, VertexData data) {
		setVertex(index, data.x, data.y, data.z, data.textureChordX, data.textureChordY, data.normalX, data.normalY,
				data.normalZ);
		int dimension = getDimension();
		if (dimension == 2) {
			if (hasNormals())
				setVertex(index, data.x, data.y, data.textureChordX, data.textureChordY, data.normalX, data.normalY);
			else
				setVertex(index, data.x, data.y, data.textureChordX, data.textureChordY);
		} else if (dimension == 3) {
			if (hasNormals())
				setVertex(index, data.x, data.y, data.z, data.textureChordX, data.textureChordY, data.normalX,
						data.normalY, data.normalZ);
			else
				setVertex(index, data.x, data.y, data.z, data.textureChordX, data.textureChordY);
		}
	}

	public final void setVertex(int index, float x, float y, float textureChordX, float textureChordY) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer target = stack.mallocFloat(vertexSize);
			target.put(x);
			target.put(y);
			target.put(textureChordX);
			target.put(textureChordY);
			target.flip();
			glBufferSubData(GL_ARRAY_BUFFER, index * vertexSize * Float.BYTES, target);
		}
	}

	public final void setVertex(int index, float x, float y, float textureChordX, float textureChordY, float normalX,
			float normalY) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer target = stack.mallocFloat(vertexSize);
			target.put(x);
			target.put(y);
			target.put(textureChordX);
			target.put(textureChordY);
			target.put(normalX);
			target.put(normalY);
			target.flip();
			glBufferSubData(GL_ARRAY_BUFFER, index * vertexSize * Float.BYTES, target);
		}
	}

	public final void setVertex(int index, float x, float y, float z, float textureChordX, float textureChordY,
			float normalX, float normalY, float normalZ) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer target = stack.mallocFloat(vertexSize);
			target.put(x);
			target.put(y);
			target.put(z);
			target.put(textureChordX);
			target.put(textureChordY);
			target.put(normalX);
			target.put(normalY);
			target.put(normalZ);
			target.flip();
			glBufferSubData(GL_ARRAY_BUFFER, index * vertexSize * Float.BYTES, target);
		}
	}

	public final void setVertex(int index, float x, float y, float z, float textureChordX, float textureChordY) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer target = stack.mallocFloat(vertexSize);
			target.put(x);
			target.put(y);
			target.put(z);
			target.put(textureChordX);
			target.put(textureChordY);
			target.flip();
			glBufferSubData(GL_ARRAY_BUFFER, index * vertexSize * Float.BYTES, target);
		}
	}

	public final VertexData getVertex(int index) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer target = stack.mallocFloat(vertexSize);
			glGetBufferSubData(GL_ARRAY_BUFFER, index * vertexSize * Float.BYTES, target);
			VertexData data = new VertexData();
			int dimension = getDimension();
			if (dimension == 2) {
				data.x = target.get();
				data.y = target.get();
				data.textureChordX = target.get();
				data.textureChordY = target.get();
				if (hasNormals()) {
					data.normalX = target.get();
					data.normalY = target.get();
				}
			} else if (dimension == 3) {
				data.x = target.get();
				data.y = target.get();
				data.z = target.get();
				data.textureChordX = target.get();
				data.textureChordY = target.get();
				if (hasNormals()) {
					data.normalX = target.get();
					data.normalY = target.get();
					data.normalZ = target.get();
				}
			}
			return data;
		}
	}

	public final void loadIndices(IntBuffer indices) {
		size = indices.remaining();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, type);
	}

	public final void setIndices(IntBuffer data) {
		setIndices(0, data);
	}

	public final void setIndices(int index, IntBuffer data) {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, index * Integer.BYTES, data);
	}

	public final IntBuffer getIndices(IntBuffer target) {
		return getIndices(0, target);
	}

	public final IntBuffer getIndices(int index, IntBuffer target) {
		glBindBuffer(GL_ARRAY_BUFFER, ibo);
		glGetBufferSubData(GL_ARRAY_BUFFER, index * Integer.BYTES, target);
		return target;
	}

	public final void setIndex(int data) {
		setIndex(0, data);
	}

	public final void setIndex(int index, int data) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer target = stack.mallocInt(1);
			target.put(data);
			glBufferSubData(GL_ARRAY_BUFFER, index * Integer.BYTES, target);
		}
	}

	public final int getIndex(int index) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer target = stack.mallocInt(1);
			glGetBufferSubData(GL_ARRAY_BUFFER, index * Integer.BYTES, target);
			return target.get();
		}
	}

	public final int getDimension() {
		return (vertexSize - TEXT_CORD_SIZE) % 3 == 0 ? 3 : 2;
	}

	public final boolean hasNormals() {
		return vertexSize - TEXT_CORD_SIZE - getDimension() != 0;
	}

	public final int getVao() {
		return vao;
	}

	public final int getVbo() {
		return vbo;
	}

	public final int getIbo() {
		return ibo;
	}

	public final int getSize() {
		return size;
	}

	public static FloatBuffer calculateNormals2d(FloatBuffer vertices, IntBuffer indices) {
		float[] normals = new float[vertices.remaining()];
		while (indices.hasRemaining()) {
			int i0 = indices.get();
			int i1 = indices.get();
			int i2 = indices.get();

			Vector3f pos0 = new Vector3f(vertices.get(i0 * 3), vertices.get(i0 * 3 + 1), vertices.get(i0 * 3 + 2));
			Vector3f pos1 = new Vector3f(vertices.get(i1 * 3), vertices.get(i1 * 3 + 1), vertices.get(i1 * 3 + 2));
			Vector3f pos2 = new Vector3f(vertices.get(i2 * 3), vertices.get(i2 * 3 + 1), vertices.get(i2 * 3 + 2));
			Vector3f v1 = Vector3f.sub(pos1, pos0);
			Vector3f v2 = Vector3f.sub(pos2, pos0);
			Vector3f normal = Vector3f.cross(v1, v2);
			normal = Vector3f.normalize(normal);

			normals[i0 * 3] += normal.x;
			normals[i0 * 3 + 1] += normal.y;

			normals[i1 * 3] += normal.x;
			normals[i1 * 3 + 1] += normal.y;

			normals[i2 * 3] += normal.x;
			normals[i2 * 3 + 1] += normal.y;
		}

		for (int i = 0; i < normals.length; i += 3) {
			float x = normals[i];
			float y = normals[i + 1];
			float length = (float) Math.sqrt(x * x + y * y);
			normals[i] /= length;
			normals[i + 1] /= length;
		}
		return FloatBuffer.wrap(normals);
	}

	public static FloatBuffer calculateNormals3d(FloatBuffer vertices, IntBuffer indices) {
		float[] normals = new float[vertices.remaining()];
		while (indices.hasRemaining()) {
			int i0 = indices.get();
			int i1 = indices.get();
			int i2 = indices.get();

			Vector3f pos0 = new Vector3f(vertices.get(i0 * 3), vertices.get(i0 * 3 + 1), vertices.get(i0 * 3 + 2));
			Vector3f pos1 = new Vector3f(vertices.get(i1 * 3), vertices.get(i1 * 3 + 1), vertices.get(i1 * 3 + 2));
			Vector3f pos2 = new Vector3f(vertices.get(i2 * 3), vertices.get(i2 * 3 + 1), vertices.get(i2 * 3 + 2));
			Vector3f v1 = Vector3f.sub(pos1, pos0);
			Vector3f v2 = Vector3f.sub(pos2, pos0);
			Vector3f normal = Vector3f.cross(v1, v2);
			normal = Vector3f.normalize(normal);

			normals[i0 * 3] += normal.x;
			normals[i0 * 3 + 1] += normal.y;
			normals[i0 * 3 + 2] += normal.z;

			normals[i1 * 3] += normal.x;
			normals[i1 * 3 + 1] += normal.y;
			normals[i1 * 3 + 2] += normal.z;

			normals[i2 * 3] += normal.x;
			normals[i2 * 3 + 1] += normal.y;
			normals[i2 * 3 + 2] += normal.z;
		}

		for (int i = 0; i < normals.length; i += 3) {
			float x = normals[i];
			float y = normals[i + 1];
			float z = normals[i + 2];
			float length = (float) Math.sqrt(x * x + y * y + z * z);
			normals[i] /= length;
			normals[i + 1] /= length;
			normals[i + 2] /= length;
		}
		return FloatBuffer.wrap(normals);
	}

	public static AIVector3D.Buffer calculateNormals(AIVector3D.Buffer vertices, IntBuffer indices) {
		AIVector3D.Buffer normals = AIVector3D.malloc(vertices.remaining());
		int indicesSize = indices.remaining();
		for (int i = 0; i < indicesSize; i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);
			AIVector3D pos0 = vertices.get(i0);
			AIVector3D pos1 = vertices.get(i1);
			AIVector3D pos2 = vertices.get(i2);

			Vector3f v1 = Vector3f.sub(pos1.x(), pos1.y(), pos1.z(), pos0.x(), pos0.y(), pos0.z());
			Vector3f v2 = Vector3f.sub(pos2.x(), pos2.y(), pos2.z(), pos0.x(), pos0.y(), pos0.z());
			Vector3f normal = Vector3f.cross(v1, v2);
			normal = Vector3f.normalize(normal);

			AIVector3D n0 = normals.get(i0);
			n0.set(n0.x() + normal.x, n0.y() + normal.y, n0.z() + normal.z);
			AIVector3D n1 = normals.get(i1);
			n1.set(n1.x() + normal.x, n1.y() + normal.y, n1.z() + normal.z);
			AIVector3D n2 = normals.get(i2);
			n2.set(n2.x() + normal.x, n2.y() + normal.y, n2.z() + normal.z);
		}

		int normalsSize = normals.remaining();
		for (int i = 0; i < normalsSize; i++) {
			AIVector3D normal = vertices.get(i);
			float x = normal.x();
			float y = normal.y();
			float z = normal.z();
			float length = (float) Math.sqrt(x * x + y * y + z * z);
			normal.set(x / length, y / length, z / length);
		}
		return normals;
	}

	public static FloatBuffer generateTextureChords(int dimension, FloatBuffer vertices) {
		int vertexSize = vertices.remaining() / dimension;
		FloatBuffer textureCords = BufferUtils.createFloatBuffer(vertexSize * 2);
		for (int i = 0; i < vertexSize; i += 2) {
			textureCords.put(0);
			textureCords.put(0);
			textureCords.put(1);
			textureCords.put(1);
		}
		textureCords.flip();
		return textureCords;
	}

	public static class VertexData {

		public float x;
		public float y;
		public float z;
		public float textureChordX;
		public float textureChordY;
		public float normalX;
		public float normalY;
		public float normalZ;
	}
}
