package org.jgine.render.graphic.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.misc.utils.BufferHelper;
import org.jgine.render.graphic.material.Material;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAABB;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryUtil;

public class Mesh implements AutoCloseable {

	public static final int VERTEX_SIZE = 3;
	public static final int TEXT_CORD_SIZE = 2;
	public static final int NORMAL_SIZE = 3;
	public static final int SIZE = VERTEX_SIZE + TEXT_CORD_SIZE + NORMAL_SIZE;

	public final String name;
	protected int mode = GL_TRIANGLES;
	public Material material;
	protected int vao;
	protected int vbo;
	protected int ibo;
	protected int size;

	public static class Mode {

		public static final int TRIANGLES = GL_TRIANGLES;
		public static final int TRIANGLE_STRIP = GL_TRIANGLE_STRIP;
		public static final int LINES = GL_LINES;
		public static final int LINE_STRIP = GL_LINE_STRIP;
		public static final int LINE_LOOP = GL_LINE_LOOP;
		public static final int POINTS = GL_POINTS;
	}

	public Mesh(AIMesh mesh) {
		name = mesh.mName().dataString();
		loadData(mesh);
	}

	public Mesh(float[] vertices, int[] indices) {
		this("", vertices, indices, null, null);
	}

	public Mesh(String name, float[] vertices, int[] indices) {
		this(name, vertices, indices, null, null);
	}

	public Mesh(float[] vertices, int[] indices, @Nullable float[] textureChords, @Nullable float[] normals) {
		this("", vertices, indices, textureChords, normals);
	}

	public Mesh(String name, float[] vertices, int[] indices, @Nullable float[] textureChords,
			@Nullable float[] normals) {
		this.name = name;
		loadData(vertices, indices, textureChords, normals);
	}

	@Override
	public final void close() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ibo);
		vao = 0;
		vbo = 0;
		ibo = 0;
	}

	public final void render() {
		glBindVertexArray(vao);
		glDrawElements(mode, size, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	protected void loadData(AIMesh mesh) {
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

	protected void loadData(float[] vertices, int[] indices, @Nullable float[] textureChords,
			@Nullable float[] normals) {
		if (normals == null || normals.length == 0)
			normals = BufferHelper.calculateNormals(vertices, indices);
		if (textureChords == null || textureChords.length == 0)
			textureChords = BufferHelper.generateTextureChords(vertices);

		FloatBuffer verticesBuffer = BufferHelper.createFloatBuffer(3, 2, 3, vertices, textureChords, normals);
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();

		loadData(verticesBuffer, indicesBuffer);
	}

	protected void loadData(AIVector3D.Buffer vertices, IntBuffer indices, AIVector3D.Buffer textureChords,
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
		FloatBuffer buildVertices = BufferHelper.createFloatBuffer(3, 2, 3, vertices, textureChords, normals);
		loadData(buildVertices, indices);
	}

	protected void loadData(FloatBuffer vertices, IntBuffer indices) {
		size = indices.remaining();
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW); // OR GL_DYNAMIC_DRAW if data changes
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 0 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, TEXT_CORD_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, NORMAL_SIZE, GL_FLOAT, false, SIZE * Float.BYTES, 5 * Float.BYTES);

		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		glBindVertexArray(0);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}
}