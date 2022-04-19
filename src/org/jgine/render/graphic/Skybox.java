package org.jgine.render.graphic;

import static org.lwjgl.opengl.GL11.*;

import org.jgine.core.Scene;
import org.jgine.misc.math.Transform3f;
import org.jgine.render.Renderer;
import org.jgine.render.graphic.material.Material;

public class Skybox {

	public final Transform3f transform = new Transform3f();
	public Material material;

	public void render(Scene scene) {
//		glCullFace(GL_FRONT);
//		transform.calculateMatrix();
//		Renderer.renderCube(transform.getMatrix(), material);
//		glCullFace(GL_BACK);
	}
}
