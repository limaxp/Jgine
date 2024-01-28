package org.jgine.system.systems.graphic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.render.FrustumCulling2D;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.render.material.Material;
import org.jgine.system.SystemObject;
import org.jgine.system.data.TransformListSystemScene;

public class Graphic2DScene extends TransformListSystemScene<Graphic2DSystem, Material> {

	private final FrustumCulling2D frustumCulling = new FrustumCulling2D();

	public Graphic2DScene(Graphic2DSystem system, Scene scene) {
		super(system, scene, Material.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Material object) {
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render(float dt) {
		frustumCulling.applyCamera(Renderer.getCamera(), 50);

//		for (int i = 0; i < size; i++) {
//			Transform transform = transforms[i];
//			if (frustumCulling.containsPoint(transform.getPosition()))
//				Renderer2D.renderQuad(transform.getMatrix(), Renderer.PHONG_2D_SHADER, objects[i]);
//		}

		scene.getSpacePartitioning().forNear(frustumCulling.x1, frustumCulling.y1, 0.0f, frustumCulling.x2,
				frustumCulling.y2, 0.0f, (entity) -> {
					List<Material> materials = entity.getSystems(Engine.GRAPHIC_2D_SYSTEM);
					for (SystemObject material : materials)
						Renderer2D.renderQuad(entity.transform.getMatrix(), Renderer.PHONG_2D_SHADER,
								(Material) material);
				});
	}

	@Override
	public void load(DataInput in) throws IOException {
		size = in.readInt();
		ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			Material object = new Material();
			object.load(in);
			objects[i] = object;
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			objects[i].save(out);
	}
}
