package jgine.system.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jgine.render.UIRenderer;
import jgine.render.material.Material;
import jgine.system.transform.Transform;
import jgine.system.ui.UIObject;
import jgine.system.ui.UIObjectType;
import jgine.utils.ObjectUtils;
import jgine.utils.math.Matrix;

public class UIRadioButton extends UIObject {

	private Material background;
	private Material button;
	private Matrix buttonTransform;
	private boolean activated;

	public UIRadioButton() {
		background = new Material(BORDER_COLOR);
		button = new Material(FOCUS_COLOR);
		buttonTransform = new Matrix();
	}

	@Override
	public UIRadioButton clone() {
		UIRadioButton obj = (UIRadioButton) super.clone();
		obj.background = background.clone();
		obj.button = button.clone();
		obj.buttonTransform = new Matrix(buttonTransform);
		return obj;
	}

	@Override
	protected void free() {
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		if (activated)
			UIRenderer.renderQuad(buttonTransform, button);
	}

	@Override
	public void onClick(int key) {
		super.onClick(key);
		activated = !activated;
	}

	@Override
	protected void calculateTransform() {
		super.calculateTransform();
		calculateButtonTransform();
	}

	protected void calculateButtonTransform() {
		Transform.calculateMatrix(buttonTransform, -1 + (getX() + getWidth() * 0.5f) * 2,
				-1 + (getY() + getHeight() * 0.5f) * 2, 0, getWidth() * 0.8f, getHeight() * 0.8f, 0);
		getParent().updateTransform(buttonTransform);
	}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		ObjectUtils.toMaterial(background, data.get("background"));
		ObjectUtils.toMaterial(button, data.get("button"));
		activated = ObjectUtils.toBoolean(data.get("activated"), activated);
	}

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		Map<String, Object> backgroundMap = new HashMap<String, Object>();
		background.save(backgroundMap);
		data.put("background", backgroundMap);
		Map<String, Object> buttonMap = new HashMap<String, Object>();
		button.save(buttonMap);
		data.put("button", buttonMap);
		data.put("activated", activated ? "true" : "false");
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		background.load(in);
		button.load(in);
		activated = in.readBoolean();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		background.save(out);
		button.save(out);
		out.writeBoolean(activated);
	}

	@Override
	public UIObjectType<? extends UIRadioButton> getType() {
		return UIObjectType.RADIO_BUTTON;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setButton(Material button) {
		this.button = button;
	}

	public Material getButton() {
		return button;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean isActivated() {
		return activated;
	}
}
