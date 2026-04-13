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

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		ObjectUtils.toMaterial(background, data.get("background"));
		ObjectUtils.toMaterial(bar, data.get("bar"));
		filled = ObjectUtils.toFloat(data.get("filled"), filled);
		barType = ObjectUtils.toByte(data.get("barType"), barType);
	}

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		Map<String, Object> backgroundMap = new HashMap<String, Object>();
		background.save(backgroundMap);
		data.put("background", backgroundMap);
		Map<String, Object> barMap = new HashMap<String, Object>();
		bar.save(barMap);
		data.put("bar", barMap);
		data.put("filled", filled);
		data.put("barType", barType);
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
		return UIObjectType.BAR;
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
