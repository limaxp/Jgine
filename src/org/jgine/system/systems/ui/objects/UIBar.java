package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.Transform;
import org.jgine.render.UIRenderer;
import org.jgine.render.material.Material;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.utils.loader.ResourceManager;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;

public class UIBar extends UIObject {

	public static final byte TYPE_NORMAL = 0;
	public static final byte TYPE_MIDDLE = 1;

	private Material background;
	private Material bar;
	private float filled;
	private Matrix barTransform;
	private byte barType;

	public UIBar() {
		background = new Material(BORDER_COLOR);
		bar = new Material(FOCUS_COLOR);
		filled = 1.0f;
		barTransform = new Matrix();
		barType = TYPE_NORMAL;
	}

	@Override
	public UIBar clone() {
		UIBar obj = (UIBar) super.clone();
		obj.background = background.clone();
		obj.bar = bar.clone();
		obj.barTransform = new Matrix(barTransform);
		return obj;
	}

	@Override
	protected void free() {
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		UIRenderer.renderQuad(barTransform, bar);
	}

	@Override
	protected void calculateTransform() {
		super.calculateTransform();
		calculateBarTransform();
	}

	protected void calculateBarTransform() {
		if (barType == TYPE_NORMAL)
			Transform.calculateMatrix(barTransform, -1 + (getX() + getWidth() * filled * 0.5f) * 2,
					-1 + (getY() + getHeight() * 0.5f) * 2, 0, getWidth() * filled, getHeight(), 0);
		else if (barType == TYPE_MIDDLE)
			Transform.calculateMatrix(barTransform, -1 + (getX() + getWidth() * 0.5f) * 2,
					-1 + (getY() + getHeight() * 0.5f) * 2, 0, getWidth() * filled, getHeight(), 0);
		getParent().updateTransform(barTransform);
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

		Object barData = data.get("bar");
		if (barData instanceof String)
			bar.setTexture(ResourceManager.getTexture((String) barData));
		else if (barData instanceof Map)
			bar.load((Map<String, Object>) barData);

		Object filledData = data.get("filled");
		if (filledData != null)
			filled = YamlHelper.toFloat(filledData, 1.0f);
		Object barTypeData = data.get("barType");
		if (barTypeData != null)
			barType = YamlHelper.toByte(barTypeData);
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		background.load(in);
		bar.load(in);
		filled = in.readFloat();
		barType = in.readByte();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		background.save(out);
		bar.save(out);
		out.writeFloat(filled);
		out.writeByte(barType);
	}

	@Override
	public UIObjectType<? extends UIBar> getType() {
		return UIObjectTypes.BAR;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setBar(Material bar) {
		this.bar = bar;
	}

	public Material getBar() {
		return bar;
	}

	public void setFilled(float filled) {
		this.filled = filled;
		calculateBarTransform();
	}

	public float getFilled() {
		return filled;
	}

	public void setBarType(byte barType) {
		this.barType = barType;
		calculateBarTransform();
	}

	public byte getBarType() {
		return barType;
	}
}
