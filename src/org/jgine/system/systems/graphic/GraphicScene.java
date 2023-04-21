package org.jgine.system.systems.graphic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jgine.core.Engine;
import org.jgine.core.Scene;
import org.jgine.core.Transform;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.render.FrustumCulling;
import org.jgine.render.Renderer;
import org.jgine.system.data.TransformListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.utils.scheduler.TaskHelper;

public class GraphicScene extends TransformListSystemScene<GraphicSystem, GraphicObject> {

	private final Queue<Object> renderQueue = new ConcurrentLinkedQueue<Object>();
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
		renderQueue.clear();
		Camera camera = Engine.CAMERA_SYSTEM.getMainCamera();
		frustumCulling.applyCamera(camera, 0);
		TaskHelper.execute(size, (index, size) -> update(frustumCulling, index, size));
	}

	private void update(FrustumCulling frustumCulling, int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			GraphicObject object = objects[index];
//			if (frustumCulling.containsPoint(transforms[index].getPosition()))
			renderQueue.addAll(Arrays.asList(transforms[index], object));
		}
	}

	@Override
	public void render() {
		Renderer.enableDepthTest();
		Iterator<Object> iter = renderQueue.iterator();
		while (iter.hasNext())
			Renderer.render(((Transform) iter.next()).getMatrix(), ((GraphicObject) iter.next()).model,
					Renderer.PHONG_SHADER);
		Renderer.disableDepthTest();
	}

	@Override
	public GraphicObject load(DataInput in) throws IOException {
		GraphicObject object = new GraphicObject();
		object.model = ResourceManager.getModel(in.readUTF());
		return object;
	}

	@Override
	public void save(GraphicObject object, DataOutput out) throws IOException {
		out.writeUTF(object.model.name);
	}
}
