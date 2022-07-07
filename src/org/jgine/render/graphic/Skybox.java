package org.jgine.render.graphic;

import org.jgine.core.Scene;
import org.jgine.core.entity.Transform;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.render.graphic.material.Material;

public class Skybox {

	private Matrix matrix = Transform.calculateMatrix(new Matrix(), Vector3f.NULL, Vector3f.NULL, Vector3f.FULL);
	public Material material;

	public void render(Scene scene) {
//		glCullFace(GL_FRONT);
//		transform.calculateMatrix();
//		Renderer.renderCube(transform.getMatrix(), material);
//		glCullFace(GL_BACK);
	}
}
