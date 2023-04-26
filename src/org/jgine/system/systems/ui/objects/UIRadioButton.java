package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Transform;
import org.jgine.core.manager.ResourceManager;
import org.jgine.render.UIRenderer;
import org.jgine.render.material.Material;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.utils.Color;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;

public class UIRadioButton extends UIObject {

	private Material background;
	private Material button;
	private Matrix buttonTransform;
	private boolean activated;

	public UIRadioButton() {
		background = new Material(Color.GRAY);
		button = new Material(Color.BLACK);
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
	public void render(int depth) {
		UIRenderer.renderQuad(getTransform(), UIRenderer.TEXTURE_SHADER, background, depth);
		if (activated)
			UIRenderer.renderQuad(buttonTransform, UIRenderer.TEXTURE_SHADER, button, depth + 1);
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

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object backgroundData = data.get("background");
		if (backgroundData instanceof String)
			background.setTexture(ResourceManager.getTexture((String) backgroundData));
		else if (backgroundData instanceof Map)
			background.load((Map<String, Object>) backgroundData);

		Object buttonData = data.get("button");
		if (buttonData instanceof String)
			button.setTexture(ResourceManager.getTexture((String) buttonData));
		else if (buttonData instanceof Map)
			button.load((Map<String, Object>) buttonData);

		Object activatedData = data.get("activated");
		if (activatedData != null)
			activated = YamlHelper.toBoolean(activatedData);
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
		return UIObjectTypes.RADIO_BUTTON;
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
