package jgine.system.graphic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import jgine.core.Engine;
import jgine.core.Entity;
import jgine.core.Scene;
import jgine.render.FrustumCulling2D;
import jgine.render.Renderer;
import jgine.render.Renderer2D;
import jgine.render.material.Material;
import jgine.system.ObjectSystemScene.TransformSystemScene;

public class Graphic2DScene extends TransformSystemScene<Graphic2DSystem, Material> {

	private final FrustumCulling2D frustumCulling = new FrustumCulling2D();

	public Graphic2DScene(Graphic2DSystem system, Scene scene) {
		super(system, scene, Material.class, 100000);
	}

	@Override
	public void free() {
	}

	@Override
	public void onInit(Entity entity, Material object) {
	}

	@Override
	public void render(float dt) {
		frustumCulling.applyCamera(Renderer.getCamera(), 50);

//		for (int i = 0; i < size; i++) {
//			Transform transform = transforms[i];
//			if (frustumCulling.containsPoint(transform.getPosition()))
//				Renderer2D.renderQuad(transform.getMatrix(), Renderer.PHONG_2D_SHADER, objects[i]);
//		}

		Renderer.enableDepthTest();
		Renderer.setShader(Renderer.PHONG_2D_SHADER);
		scene.getSpacePartitioning().forEach(frustumCulling.x1, frustumCulling.y1, 0.0f, frustumCulling.x2,
				frustumCulling.y2, 0.0f, (entity) -> {
					entity.forEach(Engine.GRAPHIC_2D, (material) -> Renderer2D
							.renderQuad(entity.getTransform().getMatrix(), (Material) material));
				});
		Renderer.disableDepthTest();
	}

	@Override
	protected void saveData(Material object, DataOutput out) throws IOException {
		object.save(out);
	}

	@Override
	protected Material loadData(DataInput in) throws IOException {
		Material object = new Material();
		object.load(in);
		return object;
	}
}
