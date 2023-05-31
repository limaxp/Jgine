package org.jgine.system.systems.graphic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.render.FrustumCulling;
import org.jgine.render.Renderer;
import org.jgine.system.data.TransformListSystemScene;

public class GraphicScene extends TransformListSystemScene<GraphicSystem, GraphicObject> {

	private final FrustumCulling frustumCulling = new FrustumCulling();

	public GraphicScene(GraphicSystem system, Scene scene) {
		super(system, scene, GraphicObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, GraphicObject object) {
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void render(float dt) {
		frustumCulling.applyCamera(Renderer.getCamera(), 0);

		for (int i = 0; i < size; i++) {
			Transform transform = transforms[i];
//			if (frustumCulling.containsPoint(transform.getPosition()))
			Renderer.render(transform.getMatrix(), objects[i].model, Renderer.PHONG_SHADER);
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
		size = in.readInt();
		ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			GraphicObject object = new GraphicObject();
			object.model = ResourceManager.getModel(in.readUTF());
			objects[i] = object;
		}
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			out.writeUTF(objects[i].model.name);
	}
}
