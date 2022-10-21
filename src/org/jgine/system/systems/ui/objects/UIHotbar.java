package org.jgine.system.systems.ui.objects;

import java.util.Map;

import org.jgine.core.entity.Entity;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.system.systems.ui.UIWindow.DragTask;

public class UIHotbar extends UIObject {

	private float thickness;
	private Material background;
	private DragTask dragTask;

	public UIHotbar() {
		this(0.05f);
	}

	public UIHotbar(float thickness) {
		setThickness(thickness);
		background = new Material(new Vector4f(1, 1, 1, 0.4f));
	}

	@Override
	public UIHotbar clone() {
		UIHotbar obj = (UIHotbar) super.clone();
		obj.background = background.clone();
		obj.dragTask = null;
		return obj;
	}

	@Override
	protected void create(Entity entity) {
	}

	@Override
	protected void free() {
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
	}

	@Override
	public void onFocus() {
	}

	@Override
	public void onDefocus() {
	}

	@Override
	public void onClick(float mouseX, float mouseY) {
		if (getWindow().isMoveAble())
			Scheduler.runTaskAsynchron(dragTask = new DragTask(getWindow()));
	}

	@Override
	public void onRelease(float mouseX, float mouseY) {
		if (dragTask != null && !dragTask.isCanceled())
			dragTask.cancel();
	}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
	}

	@Override
	public UIObjectType<? extends UIHotbar> getType() {
		return UIObjectTypes.HOTBAR;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setThickness(float thickness) {
		this.thickness = thickness;
		setPos(0, 1 - thickness);
		setScale(1.0f, thickness);
	}

	public float getThickness() {
		return thickness;
	}
}
