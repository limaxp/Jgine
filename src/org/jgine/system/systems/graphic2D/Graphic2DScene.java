package org.jgine.system.systems.graphic2D;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jgine.core.Scene;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.SystemManager;
import org.jgine.core.manager.TaskManager;
import org.jgine.render.FrustumCulling2D;
import org.jgine.render.Renderer;
import org.jgine.render.Renderer2D;
import org.jgine.system.data.ListSystemScene;
import org.jgine.system.systems.camera.Camera;
import org.jgine.system.systems.camera.CameraSystem;
import org.jgine.system.systems.transform.TransformSystem;

public class Graphic2DScene extends ListSystemScene<Graphic2DSystem, Graphic2DObject> {

	private final Queue<Graphic2DObject> renderQueue = new ConcurrentLinkedQueue<Graphic2DObject>();

	public Graphic2DScene(Graphic2DSystem system, Scene scene) {
		super(system, scene, Graphic2DObject.class);
	}

	@Override
	public void free() {
	}

	@Override
	public void initObject(Entity entity, Graphic2DObject object) {
		object.transform = entity.getSystem(scene.getSystem(TransformSystem.class));
	}

	@Override
	public void update() {
		renderQueue.clear();
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		FrustumCulling2D frustumCulling = new FrustumCulling2D();
		frustumCulling.applyCamera(camera);
		TaskManager.execute(size, (index, size) -> update(frustumCulling, index, size));
	}

	private void update(FrustumCulling2D frustumCulling, int index, int size) {
		size = index + size;
		for (; index < size; index++) {
			Graphic2DObject object = objects[index];
			// TODO use collider
//			if (frustumCulling.containsPoint(object.transform.getPosition()))
			renderQueue.add(object);
		}
	}

	@Override
	public void render() {
		Renderer2D.setShader(Renderer.TEXTURE_SHADER);
		Camera camera = SystemManager.get(CameraSystem.class).getCamera();
		Renderer2D.setCamera(camera);
		for (Graphic2DObject object : renderQueue)
			Renderer2D.renderQuad(object.transform.getMatrix(), object.material);
	}
}
