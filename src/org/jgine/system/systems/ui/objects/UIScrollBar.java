package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
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

	public UIScrollBar() {
		this(0.05f);
	}

	public UIScrollBar(float thickness) {
		setThickness(thickness);
		background = new Material(Color.DARK_GRAY);
	}

	@Override
	public UIScrollBar clone() {
		UIScrollBar obj = (UIScrollBar) super.clone();
		obj.background = background.clone();
		return obj;
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		for (UIObject child : getChilds())
			child.render();
		UIRenderer.renderLine(getTransform(), ((UIWindow) getParent()).getBorder(), -1.0f + thickness, -1.0f,
				-1.0f + thickness, 1.0f);
	}

	@Override
	public void onClick(int key) {
		super.onClick(key);
		UIWindow window = (UIWindow) getParent();
		System.out.println("scrollbarClick");
		window.addScrollY(1);
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
		set(1 - thickness, 0, thickness, 1.0f);
	}

	public float getThickness() {
		return thickness;
	}
}
