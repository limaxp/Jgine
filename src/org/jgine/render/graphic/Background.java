package org.jgine.render.graphic;

import org.jgine.core.Scene;
import org.jgine.misc.collection.list.indexList.IndexList;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.Transform3f;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraScene;
import org.jgine.system.systems.camera.CameraSystem;

public class Background extends IndexList<Material> {

	public final Transform3f transform = new Transform3f();

	public void render(Scene scene) {
		transform.calculateMatrix();
		Matrix matrix = transform.getMatrix();
		Renderer2D.setShader(Renderer.TEXTURE_SHADER);
		CameraScene cameraScene = (CameraScene) scene.getSystem(CameraSystem.class);
		for (Camera camera : cameraScene.getObjects()) {
			Renderer2D.setCamera(camera);
			for (int i = 0; i < size; i++)
				Renderer2D.renderQuad(matrix, getIntern(i));
		}
	}
}
