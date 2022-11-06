package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.system.systems.ui.UIWindow.DragTask;

public class UIHotbar extends UIObject {

	private Material background;
	private float thickness;
	private DragTask dragTask;

	public UIHotbar() {
		this(0.05f);
	}

	public UIHotbar(float thickness) {
		setThickness(thickness);
		background = new Material(Color.TRANSLUCENT_MID);
	}

	@Override
	public UIHotbar clone() {
		UIHotbar obj = (UIHotbar) super.clone();
		obj.background = background.clone();
		obj.dragTask = null;
		return obj;
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

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object backgroundData = data.get("background");
		if (backgroundData instanceof String)
			background.setTexture(ResourceManager.getTexture((String) backgroundData));
		else if (backgroundData instanceof Map)
			background.load((Map<String, Object>) backgroundData);
		setThickness(YamlHelper.toFloat(data.get("thickness"), 0.05f));
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		background.load(in);
		setThickness(in.readFloat());
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		background.save(out);
		out.writeFloat(thickness);
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
