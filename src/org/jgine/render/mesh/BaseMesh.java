package org.jgine.render.mesh;

import java.nio.FloatBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.lwjgl.BufferUtils;

/**
 * Represents a NOT indexed Mesh!
 */
public class BaseMesh extends Mesh {

	public BaseMesh(int dimension, boolean hasNormals) {
		super(dimension, hasNormals);
	}

	public BaseMesh(int dimension, int type, boolean hasNormals) {
		super(dimension, type, hasNormals);
	}

	@Override
	public void render() {
		drawArrays();
	}

	public void loadVertices(float[] vertices, @Nullable float[] textureChords) {
		loadVertices(FloatBuffer.wrap(vertices), textureChords != null ? FloatBuffer.wrap(textureChords) : null);
	}

	public void loadVertices(float[] vertices, @Nullable float[] textureChords, float[] normals) {
		loadVertices(FloatBuffer.wrap(vertices), textureChords != null ? FloatBuffer.wrap(textureChords) : null,
				FloatBuffer.wrap(normals));
	}

	public void loadVertices(FloatBuffer vertices, @Nullable FloatBuffer textureChords) {
		int dimension = getDimension();
		if (textureChords == null || textureChords.remaining() == 0)
			textureChords = generateTextureChords(dimension, vertices);

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
			}
		} else if (dimension == 2) {
			for (int i = 0; i < vertexSize; i++) {
				combinedVertices.put(vertices.get());
				combinedVertices.put(vertices.get());
				combinedVertices.put(textureChords.get());
				combinedVertices.put(textureChords.get());
			}
		}
		combinedVertices.flip();
		loadVertices(combinedVertices);
	}

	public void loadVertices(FloatBuffer vertices, @Nullable FloatBuffer textureChords, FloatBuffer normals) {
		int dimension = getDimension();
		if (textureChords == null || textureChords.remaining() == 0)
			textureChords = generateTextureChords(dimension, vertices);

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
	}
}
