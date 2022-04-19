package org.jgine.system.systems.ui.objects;

import java.util.Map;

import org.jgine.misc.math.vector.Vector4f;
import org.jgine.render.graphic.material.Material;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

public class UIButton extends UILabel {

	private static final Runnable NULL = new Runnable() {

		@Override
		public void run() {}
	};

	private Runnable clickCallback;
	private Material focusBackground;
	private Material originalBackground;

	public UIButton() {
		clickCallback = NULL;
		focusBackground = new Material(new Vector4f(1, 1, 1, 0.6f));
	}

	@Override
	public UIButton clone() {
		UIButton obj = (UIButton) super.clone();
		obj.focusBackground = focusBackground.clone();
		if (originalBackground != null)
			obj.setBackground(originalBackground.clone());
		return obj;
	}

	@Override
	public void onFocus() {
		originalBackground = getBackground();
		setBackground(focusBackground);
	}

	@Override
	public void onDefocus() {
		setBackground(originalBackground);
		originalBackground = null;
	}

	@Override
	public void onClick(int mouseX, int mouseY) {
		clickCallback.run();
	}

	@Override
	public void onRelease(int mouseX, int mouseY) {}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
	}

	@Override
	public UIObjectType<? extends UIButton> getType() {
		return UIObjectTypes.BUTTON;
	}

	public void setClickCallback(Runnable clickCallback) {
		this.clickCallback = clickCallback;
	}

	public Runnable getClickCallback() {
		return clickCallback;
	}

	public void setFocusBackground(Material focusBackground) {
		this.focusBackground = focusBackground;
	}

	public Material getFocusBackground() {
		return focusBackground;
	}
}
