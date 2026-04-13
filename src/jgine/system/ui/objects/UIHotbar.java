package jgine.system.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jgine.render.UIRenderer;
import jgine.render.material.Material;
import jgine.system.ui.UIObject;
import jgine.system.ui.UIObjectType;
import jgine.system.ui.UIWindow;
import jgine.system.ui.UIWindow.DragTask;
import jgine.utils.ObjectUtils;
import jgine.utils.scheduler.Scheduler;

public class UIHotbar extends UIGrid {

	private Material background;
	private float thickness;
	private DragTask dragTask;

	public UIHotbar() {
		this(0.05f);
	}

	public UIHotbar(float thickness) {
		setThickness(thickness);
		background = new Material(BACKGROUND_COLOR);
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
		UIRenderer.renderQuad(getTransform(), ((UIWindow) getParent()).getBorder());
		super.render();
	}

	@Override
	public void onClick(int key) {
		super.onClick(key);
		UIWindow window = (UIWindow) getParent();
		if (window.isMoveAble())
			Scheduler.runTaskTimerAsynchron(20, dragTask = new DragTask(window));
	}

	@Override
	public void onRelease(int key) {
		super.onRelease(key);
		if (dragTask != null && !dragTask.isCanceled())
			dragTask.cancel();
	}

	@Override
	public void load(Map<String, Object> data) {
		setThickness(ObjectUtils.toFloat(data.get("thickness"), thickness));
		super.load(data);
		ObjectUtils.toMaterial(background, data.get("background"));
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("thickness", thickness);
		super.save(data);
		Map<String, Object> backgroundMap = new HashMap<String, Object>();
		background.save(backgroundMap);
		data.put("background", backgroundMap);
	}

	@Override
	public void load(DataInput in) throws IOException {
		setThickness(in.readFloat());
		super.load(in);
		background.load(in);

	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeFloat(thickness);
		super.save(out);
		background.save(out);
	}

	@Override
	public UIObjectType<? extends UIHotbar> getType() {
		return UIObjectType.HOTBAR;
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

	@Override
	public void placeChildReverse(UIObject child, int index) {
		float widthSize = elementWidth * index;
		int height = (int) widthSize;
		child.setPos(1 - elementWidth - widthSize - height, elementHeight * (height - scroll));
		child.setScale(elementWidth, elementHeight);
	}
}
