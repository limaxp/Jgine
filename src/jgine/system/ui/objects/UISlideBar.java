package jgine.system.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jgine.core.input.Input;
import jgine.render.UIRenderer;
import jgine.render.material.Material;
import jgine.system.ui.UICompound;
import jgine.system.ui.UIObject;
import jgine.system.ui.UIObjectType;
import jgine.system.ui.UIWindow;
import jgine.utils.ObjectUtils;
import jgine.utils.math.vector.Vector2f;
import jgine.utils.math.vector.Vector2i;
import jgine.utils.scheduler.Scheduler;
import jgine.utils.scheduler.TaskBuffer.Task;

public class UISlideBar extends UICompound {

	private Material background;
	private DragTask dragTask;
	private float value;

	public UISlideBar() {
		background = new Material(BACKGROUND_COLOR);

		UILabel button = new UILabel();
		addChild(button);
		button.setPos(0.0f, 0.0f);
		button.setScale(0.1f, 1.0f);
		button.getBackground().color = BORDER_COLOR;
		button.setClickFunction((object, _) -> {
			Scheduler.runTaskTimerAsynchron(20, dragTask = new DragTask(object));
		});
		button.setReleaseFunction((_, _) -> {
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
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		super.render();
	}

	@Override
	public void onScroll(float scroll) {
		super.onScroll(scroll);
		addScroll(scroll * 0.1f);
	}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		ObjectUtils.toMaterial(background, data.get("background"));
	}

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		Map<String, Object> backgroundMap = new HashMap<String, Object>();
		background.save(backgroundMap);
		data.put("background", backgroundMap);
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
		return UIObjectType.SLIDEBAR;
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
			Vector2i windowSize = Input.getWindowSize();
			float mouseX = cursorPos.x / windowSize.x;
			float windowX = (mouseX - window.getX()) / window.getWidth();
			return (windowX - bar.getX()) / bar.getWidth();
		}
	}
}
