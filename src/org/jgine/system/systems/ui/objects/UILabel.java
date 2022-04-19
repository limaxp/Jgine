package org.jgine.system.systems.ui.objects;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.entity.Entity;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.text.Text;
import org.jgine.render.graphic.text.TrueTypeFont;
import org.jgine.render.graphic.text.TrueTypeText;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

public class UILabel extends UIObject {

	private Material background;
	private Text text;
	private Material material;

	public UILabel() {
		background = new Material(new Vector4f(1, 1, 1, 0.2f));
		material = new Material();
	}

	@Override
	public UILabel clone() {
		UILabel obj = (UILabel) super.clone();
		obj.background = background.clone();
		obj.material = material.clone();
		// TODO clone text!
		return obj;
	}

	@Override
	protected void create(Entity entity) {}

	@Override
	protected void free() {
		if (text != null)
			text.close();
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		if (text != null) {
			Matrix transform = new Matrix(getTransform());
			transform.scaling(0.001f, 0.001f, 0.001f);
			UIRenderer.render(transform, text.getMesh(), text.getMaterial());
		}
	}

	@Override
	public void onFocus() {}

	@Override
	public void onDefocus() {}

	@Override
	public void onClick(int mouseX, int mouseY) {}

	@Override
	public void onRelease(int mouseX, int mouseY) {}

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object texture = data.get("background");
		if (texture != null && texture instanceof String)
			background.setTexture(ResourceManager.getTexture((String) texture, background));

		Object text = data.get("text");
		if (text != null && text instanceof String) {
			this.text = new TrueTypeText(TrueTypeFont.CONSOLAS, (String) text);
		}

		// Object text = data.get("text");
		// if (text != null && text instanceof String)
		// this.text = new BitmapText(BitmapFont.CONSOLAS, (String) text);
	}

	@Override
	public UIObjectType<? extends UILabel> getType() {
		return UIObjectTypes.LABEL;
	}

	public void setBackground(Material background) {
		this.background = background;
	}

	public Material getBackground() {
		return background;
	}

	public void setText(@Nullable Text text) {
		this.text = text;
	}

	@Nullable
	public Text getText() {
		return text;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
}
