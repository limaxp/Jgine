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

public class UIScrollBar extends UICompound {

	private Material background;
	private float thickness;
	private DragTask dragTask;

	public UIScrollBar() {
		this(0.05f);
	}

	public UIScrollBar(float thickness) {
		setThickness(thickness);
		background = new Material(BACKGROUND_COLOR);

		UILabel button = new UILabel();
		addChild(button);
		button.setPos(0.0f, 0.2f);
		button.setScale(1.0f, 0.5f);
		button.getBackground().color = BORDER_COLOR;
		button.setClickFunction((object, _) -> {
			Scheduler.runTaskTimerAsynchron(20, dragTask = new DragTask(object));
		});
		button.setReleaseFunction((_, _) -> {
			if (dragTask != null && !dragTask.isCanceled())
				dragTask.cancel();
		});

		UILabel upbutton = new UILabel();
		addChild(upbutton);
		upbutton.setPos(0.0f, 0.95f);
		button.setScale(1.0f, 0.05f);
		upbutton.getBackground().color = BORDER_COLOR;
		upbutton.setClickFunction((object, _) -> ((UIScrollBar) object.getParent()).scroll(5));

		UILabel downbutton = new UILabel();
		addChild(downbutton);
		downbutton.setPos(0.0f, 0.0f);
		button.setScale(1.0f, 0.05f);
		downbutton.getBackground().color = BORDER_COLOR;
		downbutton.setClickFunction((object, _) -> ((UIScrollBar) object.getParent()).scroll(-5));
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
		return UIObjectType.SCROLLBAR;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setThickness(float thickness) {
		this.thickness = thickness;
		setPos(1 - thickness, 0);
		setScale(thickness, 0.95f);
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
		float yValue = (newY - 0.05f) / (0.9f - label.getHeight());
		System.out.println(yValue);
	}

	public UILabel getScrollButton() {
		return (UILabel) getChilds().get(0);
	}

	public UILabel getUpButton() {
		return (UILabel) getChilds().get(1);
	}

	public UILabel getDownButton() {
		return (UILabel) getChilds().get(2);
	}

	public static class DragTask extends Task {

		private UIObject label;
		private UIWindow window;
		private float dragY;

		public DragTask(UIObject label) {
			this.label = label;
			window = label.getParent().getWindow();
			this.dragY = calculateY();
		}

		@Override
		public void run() {
			float y = calculateY();
			setLabel(label, y - dragY);
			this.dragY = y;
		}

		protected float calculateY() {
			Vector2f cursorPos = Input.getCursorPos();
			Vector2i windowSize = Input.getWindowSize();
			float mouseY = 1 - cursorPos.y / windowSize.y;
			float windowY = (mouseY - window.getY()) / window.getHeight();
			return (windowY - label.getParent().getY()) / label.getParent().getHeight();
		}
	}
}
