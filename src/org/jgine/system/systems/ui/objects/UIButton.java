package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.misc.utils.Color;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

public class UIButton extends UILabel {

	private static final Runnable NULL = new Runnable() {

		@Override
		public void run() {
		}
	};

	private Runnable clickCallback;

	public UIButton() {
		clickCallback = NULL;
	}

	@Override
	public UIButton clone() {
		UIButton obj = (UIButton) super.clone();
		return obj;
	}

	@Override
	public void onFocus() {
		super.onFocus();
		getBackground().color = Color.TRANSLUCENT_STRONG;
	}

	@Override
	public void onDefocus() {
		super.onDefocus();
		getBackground().color = Color.TRANSLUCENT_WEAK;
	}

	@Override
	public void onClick(float mouseX, float mouseY) {
		super.onClick(mouseX, mouseY);
		clickCallback.run();
	}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
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
}
