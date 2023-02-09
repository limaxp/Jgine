package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.input.Input;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.vector.Vector2f;
import org.jgine.misc.math.vector.Vector2i;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.scheduler.Scheduler;
import org.jgine.misc.utils.scheduler.Task;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
import org.jgine.system.systems.ui.UICompound;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.system.systems.ui.UIWindow;

public class UIScrollBar extends UICompound {

	private Material background;
	private float thickness;
	private DragTask dragTask;

	public UIScrollBar() {
		this(0.05f);
	}

	public UIScrollBar(float thickness) {
		setThickness(thickness);
		background = new Material(Color.DARK_GRAY);

		UILabel button = new UILabel();
		addChild(button);
		button.set(0.0f, 0.2f, 1.0f, 0.6f);
		button.setClickFunction((object, key) -> {
			Scheduler.runTaskTimerAsynchron(20, dragTask = new DragTask(object));
		});
		button.setReleaseFunction((object, key) -> {
			if (dragTask != null && !dragTask.isCanceled())
				dragTask.cancel();
		});

		UILabel upbutton = new UILabel();
		addChild(upbutton);
		upbutton.set(0.0f, 0.95f, 1.0f, 0.05f);
		upbutton.setClickFunction((object, key) -> ((UIScrollBar) object.getParent()).scroll(5));

		UILabel downbutton = new UILabel();
		addChild(downbutton);
		downbutton.set(0.0f, 0.0f, 1.0f, 0.05f);
		downbutton.setClickFunction((object, key) -> ((UIScrollBar) object.getParent()).scroll(-5));
	}

	@Override
	public UIScrollBar clone() {
		UIScrollBar obj = (UIScrollBar) super.clone();
		obj.background = background.clone();
		obj.dragTask = null;
		return obj;
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		super.render();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		Object thicknessData = data.get("thickness");
		if (thicknessData != null)
			setThickness(YamlHelper.toFloat(thicknessData, 0.05f));
		super.load(data);
		Object backgroundData = data.get("background");
		if (backgroundData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) backgroundData);
			if (texture != null)
				background.setTexture(texture);
		} else if (backgroundData instanceof Map)
			background.load((Map<String, Object>) backgroundData);
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
	public UIObjectType<? extends UIScrollBar> getType() {
		return UIObjectTypes.SCROLLBAR;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setThickness(float thickness) {
		this.thickness = thickness;
		set(1 - thickness, 0, thickness, 0.95f);
	}

	public float getThickness() {
		return thickness;
	}

	private void scroll(int scroll) {
		UIWindow window = (UIWindow) getParent();
		window.addScrollY(scroll);
		setLabel(getChilds().get(0), scroll * 0.01f);
	}

	private static void setLabel(UIObject label, float scroll) {
		float newY = label.getY() + scroll;
		if (newY < 0.05f)
			newY = 0.05f;
		if (newY > 0.95f - label.getHeight())
			newY = 0.95f - label.getHeight();
		label.setY(newY);
	}

	public static class DragTask extends Task {

		private UIObject label;
		private float dragY;

		public DragTask(UIObject label) {
			this.label = label;
			Vector2f cursorPos = Input.getCursorPos();
			Vector2i windowSize = Input.getWindow().getSize();
			this.dragY = 1 - cursorPos.y / windowSize.y;
		}

		@Override
		public void run() {
			Vector2f cursorPos = Input.getCursorPos();
			Vector2i windowSize = Input.getWindow().getSize();
			float mouseY = 1 - cursorPos.y / windowSize.y;
			float distance = (mouseY - dragY) * (mouseY - dragY);
			if (distance > 0.001f) {
				setLabel(label, mouseY - dragY);
				this.dragY = mouseY;
			}
		}
	}
}
