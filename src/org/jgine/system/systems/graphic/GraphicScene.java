package org.jgine.system.systems.graphic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.render.FrustumCulling;
import org.jgine.render.Renderer;
import org.jgine.system.data.ObjectSystemScene.TransformSystemScene;
import org.jgine.utils.loader.ResourceManager;

public class GraphicScene extends TransformSystemScene<GraphicSystem, GraphicObject> {

	private final FrustumCulling frustumCulling = new FrustumCulling();

	public GraphicScene(GraphicSystem system, Scene scene) {
		super(system, scene, GraphicObject.class, 100000);
	}

	@Override
	public void free() {
	}

	@Override
	public void init(Entity entity, GraphicObject object) {
	}

	@Override
	public void render(float dt) {
		frustumCulling.applyCamera(Renderer.getCamera(), 0);

		Renderer.enableDepthTest();
		Renderer.setShader(Renderer.PHONG_SHADER);
		for (int i = 0; i < size(); i++) {
			Transform transform = getTransform(i);
//			if (frustumCulling.containsPoint(transform.getPosition()))
			Renderer.render(transform.getMatrix(), get(i).model);
		}
		Renderer.disableDepthTest();
	}

	@Override
	protected void saveData(GraphicObject object, DataOutput out) throws IOException {
		out.writeUTF(object.model.name);
	}

	@Override
	protected GraphicObject loadData(DataInput in) throws IOException {
		GraphicObject object = new GraphicObject();
		object.model = ResourceManager.getModel(in.readUTF());
		return object;
	}
}
