package org.jgine.render.graphic.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
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
import org.jgine.misc.utils.BufferHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAABB;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryUtil;

public class Mesh implements AutoCloseable {
	
	public static final int STATIC  = GL_STATIC_DRAW;
	public static final int DYNAMIC  = GL_DYNAMIC_DRAW;
	
	public static final int POINTS = GL_POINTS;
	public static final int LINES = GL_LINES;
	public static final int LINE_LOOP = GL_LINE_LOOP;
	public static final int LINE_STRIP = GL_LINE_STRIP;
	public static final int TRIANGLES = GL_TRIANGLES;
	public static final int TRIANGLE_STRIP = GL_TRIANGLE_STRIP;
	public static final int TRIANGLE_FAN =  GL_TRIANGLE_FAN;
	public static final int QUADS = GL_QUADS;
	public static final int LINES_ADJACENCY = GL_LINES_ADJACENCY;
	public static final int LINE_STRIP_ADJACENCY =  GL_LINE_STRIP_ADJACENCY;
	public static final int TRIANGLES_ADJACENCY  =  GL_TRIANGLES_ADJACENCY ;
	public static final int TRIANGLE_STRIP_ADJACENCY = GL_TRIANGLE_STRIP_ADJACENCY;
	public static final int PATCHES  = GL_PATCHES ;

	public static final int TEXT_CORD_SIZE = 2;

	public final int type;
	public int mode = TRIANGLES;
	protected int vao;
	protected int vbo;
	protected int ibo;
	protected int size;
	
	public Mesh() {
		this(STATIC);
	}
	
	public Mesh(int type) {
		this.type = type;
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
	
	public void render() {
		drawElements();
	}

	protected final void drawArrays() {
		glBindVertexArray(vao);
		glDrawArrays(mode, 0, size);
		glBindVertexArray(0);
	}

	protected final void drawElements() {
		glBindVertexArray(vao);
		glDrawElements(mode, size, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}
	
	public void loadData(int dimension, float[] vertices) {
		loadData(dimension, vertices, (float[]) null);
	}
	
	public void loadData(int dimension, float[] vertices, @Nullable float[] textureChords) {
		if (textureChords == null || textureChords.length == 0)
			textureChords = BufferHelper.generateTextureChords(vertices);

		FloatBuffer verticesBuffer = BufferHelper.createFloatBuffer(dimension, TEXT_CORD_SIZE, vertices, textureChords);
		loadDataNoNormals(dimension, verticesBuffer);
	}
	
	public void loadData(int dimension, float[] vertices, int[] indices) {
		loadData(dimension, vertices, indices, null, null);
	}

	public void loadData(int dimension, float[] vertices, int[] indices, @Nullable float[] textureChords) {
		loadData(dimension, vertices, indices, textureChords, null);
	}

	public void loadData(int dimension, float[] vertices, int[] indices, @Nullable float[] textureChords,
			@Nullable float[] normals) {
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		
		if (textureChords == null || textureChords.length == 0)
			textureChords = BufferHelper.generateTextureChords(vertices);

		if (normals == null || normals.length == 0) {
			FloatBuffer verticesBuffer = BufferHelper.createFloatBuffer(dimension, TEXT_CORD_SIZE, vertices,
					textureChords);
			loadDataNoNormals(dimension, verticesBuffer, indicesBuffer);
		} else {
			FloatBuffer verticesBuffer = BufferHelper.createFloatBuffer(dimension, TEXT_CORD_SIZE, dimension, vertices,
					textureChords, normals);
			loadData(dimension, verticesBuffer, indicesBuffer);
		}
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
			long aiColor = aiColors.get();
			// TODO use colors!
		}

		PointerBuffer aiBones = mesh.mBones();
		if (aiBones != null) {
			while (aiBones.remaining() > 0) {
				long aiBone = aiBones.get();
				// TODO use bones!
			}
		}

		PointerBuffer aiAnimMeshes = mesh.mAnimMeshes();
		if (aiAnimMeshes != null) {
			while (aiAnimMeshes.remaining() > 0) {
				long aiAnimMesh = aiAnimMeshes.get();
				// TODO use animation meshes!
			}
		}

		AIAABB aiAABB = mesh.mAABB();
		// TODO use Axial Aligned Bounding Box!

		loadData(mesh.mVertices(), indices, mesh.mTextureCoords(0), mesh.mNormals());
		MemoryUtil.memFree(indices);
	}

	public void loadData(AIVector3D.Buffer vertices, IntBuffer indices, AIVector3D.Buffer textureChords,
			AIVector3D.Buffer normals) {
		if (normals == null || normals.remaining() == 0) {
			normals = BufferHelper.calculateNormals(vertices, indices);
			loadData_(vertices, indices, textureChords, normals);
			MemoryUtil.memFree(normals);
			return;
		}
		loadData_(vertices, indices, textureChords, normals);
	}

	private void loadData_(AIVector3D.Buffer vertices, IntBuffer indices, AIVector3D.Buffer textureChords,
			AIVector3D.Buffer normals) {
		loadData(3, BufferHelper.createFloatBuffer(3, TEXT_CORD_SIZE, 3, vertices, textureChords, normals), indices);
	}

	public void loadDataNoNormals(int dimension, FloatBuffer vertices) {
		close();
		int dataSize = dimension + TEXT_CORD_SIZE;
		size = vertices.remaining() / dataSize;
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, type);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, dimension, GL_FLOAT, false, dataSize * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, dataSize * Float.BYTES, dimension * Float.BYTES);

		glBindVertexArray(0);
	}

	public void loadData(int dimension, FloatBuffer vertices) {
		close();
		int dataSize = dimension + TEXT_CORD_SIZE + dimension;
		size = vertices.remaining() / dataSize;
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, type);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, dimension, GL_FLOAT, false, dataSize * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, dataSize * Float.BYTES, dimension * Float.BYTES);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, dimension, GL_FLOAT, false, dataSize * Float.BYTES, (dimension + 2) * Float.BYTES);

		glBindVertexArray(0);
	}

	public void loadDataNoNormals(int dimension, FloatBuffer vertices, IntBuffer indices) {
		close();
		int dataSize = dimension + TEXT_CORD_SIZE;
		size = indices.remaining();
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, type);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, dimension, GL_FLOAT, false, dataSize * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, dataSize * Float.BYTES, dimension * Float.BYTES);

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, type);

		glBindVertexArray(0);
	}

	public void loadData(int dimension, FloatBuffer vertices, IntBuffer indices) {
		close();
		int dataSize = dimension + TEXT_CORD_SIZE + dimension;
		size = indices.remaining();
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, type);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, dimension, GL_FLOAT, false, dataSize * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, dataSize * Float.BYTES, dimension * Float.BYTES);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, dimension, GL_FLOAT, false, dataSize * Float.BYTES, (dimension + 2) * Float.BYTES);

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, type);

		glBindVertexArray(0);
	}
}
