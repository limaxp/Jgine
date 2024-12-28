package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.input.Input;
import org.jgine.render.UIRenderer;
import org.jgine.render.material.Material;
import org.jgine.system.systems.ui.UICompound;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.system.systems.ui.UIWindow;
import org.jgine.utils.loader.ResourceManager;
import org.jgine.utils.math.vector.Vector2f;
import org.jgine.utils.math.vector.Vector2i;

import maxLibs.utils.scheduler.Scheduler;
import maxLibs.utils.scheduler.Task;

public class UISlideBar extends UICompound {

	private Material background;
	private DragTask dragTask;
	private float value;

	public UISlideBar() {
		background = new Material(BACKGROUND_COLOR);

		UILabel button = new UILabel();
		addChild(button);
		button.set(0.0f, 0.0f, 0.1f, 1.0f);
		button.getBackground().color = BORDER_COLOR;
		button.setClickFunction((object, key) -> {
			Scheduler.runTaskTimerAsynchron(20, dragTask = new DragTask(object));
		});
		button.setReleaseFunction((object, key) -> {
			if (dragTask != null && !dragTask.isCanceled())
				dragTask.cancel();
		});
		button.setScrollFunction((object, scroll) -> {
			((UISlideBar) object.getParent()).addScroll(scroll * 0.1f);
		});
	}

	@Override
	protected void free() {
	}

	@Override
	public UISlideBar clone() {
		UISlideBar obj = (UISlideBar) super.clone();
		obj.background = background.clone();
		obj.dragTask = null;
		return obj;
	}

	@Override
	public void render(int depth) {
		UIRenderer.renderQuad(getTransform(), UIRenderer.TEXTURE_SHADER, background, depth);
		super.render(depth);
	}

	@Override
	public void onScroll(float scroll) {
		super.onScroll(scroll);
		addScroll(scroll * 0.1f);
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
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		background.load(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		background.save(out);
	}

	@Override
	public UIObjectType<? extends UISlideBar> getType() {
		return UIObjectTypes.SLIDEBAR;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	protected void addScroll(float scroll) {
		UILabel label = getButton();
		float newX = label.getX() + scroll;
		if (newX < 0.0f)
			newX = 0.0f;
		if (newX > 1.0f - label.getWidth())
			newX = 1.0f - label.getWidth();
		label.setX(newX);
		value = newX / (1.0f - label.getWidth());
	}

	public void setValue(float value) {
		if (value < 0.0f)
			value = 0.0f;
		if (value > 1.0f)
			value = 1.0f;
		this.value = value;
		UILabel label = getButton();
		label.setX(value * (1.0f - label.getWidth()));
	}

	public float getValue() {
		return value;
	}

	public UILabel getButton() {
		return (UILabel) getChilds().get(0);
	}

	public static class DragTask extends Task {

		private UISlideBar bar;
		private UIWindow window;
		private float dragX;

		public DragTask(UIObject label) {
			this.bar = (UISlideBar) label.getParent();
			window = bar.getWindow();
			this.dragX = calculateX();
		}

		@Override
		public void run() {
			float x = calculateX();
			bar.addScroll(x - dragX);
			this.dragX = x;
		}

		protected float calculateX() {
			Vector2f cursorPos = Input.getCursorPos();
			Vector2i windowSize = Input.getWindow().getSize();
			float mouseX = cursorPos.x / windowSize.x;
			float windowX = (mouseX - window.getX()) / window.getWidth();
			return (windowX - bar.getX()) / bar.getWidth();
		}
	}
}
