package org.jgine.render.graphic.mesh;

import java.nio.FloatBuffer;

import org.eclipse.jdt.annotation.Nullable;
import org.lwjgl.BufferUtils;

public class BaseMesh extends Mesh {

	@Override
	public void render() {
		drawArrays();
	}

	public void loadData(int dimension, float[] vertices, @Nullable float[] textureChords) {
		loadData(dimension, FloatBuffer.wrap(vertices), textureChords != null ? FloatBuffer.wrap(textureChords) : null);
	}

	public void loadData(int dimension, float[] vertices, @Nullable float[] textureChords, float[] normals) {
		loadData(dimension, FloatBuffer.wrap(vertices), textureChords != null ? FloatBuffer.wrap(textureChords) : null,
				FloatBuffer.wrap(normals));
	}

	public void loadData(int dimension, FloatBuffer vertices, @Nullable FloatBuffer textureChords) {
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
		loadDataNoNormals(dimension, combinedVertices);
	}

	public void loadData(int dimension, FloatBuffer vertices, @Nullable FloatBuffer textureChords,
			FloatBuffer normals) {
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
		loadData(dimension, combinedVertices);
	}
}
