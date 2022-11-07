package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.text.BitmapFont;
import org.jgine.render.graphic.text.BitmapText;
import org.jgine.render.graphic.text.Text;
import org.jgine.render.graphic.text.TrueTypeFont;
import org.jgine.render.graphic.text.TrueTypeText;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;

public class UILabel extends UIObject {

	private Material background;
	private Text text;
	private float textOffsetX;
	private float textOffsetY;
	private Matrix textTransform;

	public UILabel() {
		background = new Material(Color.TRANSLUCENT_WEAK);
		textTransform = new Matrix();
	}

	@Override
	public UILabel clone() {
		UILabel obj = (UILabel) super.clone();
		obj.background = background.clone();
		if (text instanceof TrueTypeText)
			obj.text = new TrueTypeText((TrueTypeFont) text.getFont(), text.getSize(), text.getText());
		else if (text instanceof BitmapText)
			obj.text = new BitmapText((BitmapFont) text.getFont(), text.getSize(), text.getText());
		obj.textTransform = new Matrix(textTransform);
		return obj;
	}

	@Override
	protected void free() {
		if (text != null)
			text.close();
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), background);
		if (text != null)
			UIRenderer.render(textTransform, text.getMesh(), text.getMaterial());
	}

	@Override
	protected void calculateTransform() {
		super.calculateTransform();
		calculateTextTransform();
	}

	protected void calculateTextTransform() {
		Transform.calculateMatrix(textTransform, -1 + (getX() + textOffsetX) * 2, -1 + (getY() + textOffsetY) * 2, 0,
				getWidth(), getHeight(), 0);
		if (hasWindow())
			textTransform.mult(getWindow().getTransform());
		textTransform.scaling(0.001f, 0.001f, 0.001f);
	}

	@Override
	public void onFocus() {
	}

	@Override
	public void onDefocus() {
	}

	@Override
	public void onClick(float mouseX, float mouseY) {
	}

	@Override
	public void onRelease(float mouseX, float mouseY) {
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

		Object textData = data.get("text");
		if (textData instanceof String) {
			int type = YamlHelper.toTextType(data.get("type"));
			int textSize = YamlHelper.toInt(data.get("textSize"), 64);
			if (type == Text.TYPE_TRUETYPE) {
				TrueTypeFont font = TrueTypeFont.ARIAL;
				Object fontData = data.get("font");
				if (fontData instanceof String) {
					TrueTypeFont font2 = TrueTypeFont.get((String) textData);
					if (font2 != null)
						font = font2;
				}
				this.text = new TrueTypeText(font, textSize, (String) textData);
			} else if (type == Text.TYPE_BITMAP) {
				BitmapFont font = BitmapFont.CONSOLAS;
				Object fontData = data.get("font");
				if (fontData instanceof String) {
					BitmapFont font2 = BitmapFont.get((String) textData);
					if (font2 != null)
						font = font2;
				}
				this.text = new BitmapText(font, textSize, (String) textData);
			}
		}
		textOffsetX = YamlHelper.toFloat(data.get("textOffsetX"));
		textOffsetY = YamlHelper.toFloat(data.get("textOffsetY"));
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		background.load(in);
		if (in.readBoolean()) {
			TrueTypeFont font = TrueTypeFont.get(in.readUTF());
			if (font == null)
				font = TrueTypeFont.ARIAL;
			this.text = new TrueTypeText(font, in.readInt(), in.readUTF());
		} else {
			BitmapFont font = BitmapFont.get(in.readUTF());
			if (font == null)
				font = BitmapFont.CONSOLAS;
			this.text = new BitmapText(font, in.readInt(), in.readUTF());
		}
		textOffsetX = in.readInt();
		textOffsetY = in.readInt();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		background.save(out);
		out.writeBoolean(text instanceof TrueTypeText);
		out.writeUTF(text.getFont().getName());
		out.writeInt(text.getSize());
		out.writeUTF(text.getText());
		out.writeFloat(textOffsetX);
		out.writeFloat(textOffsetY);
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

	public void setTextOffset(float textOffsetX, float textOffsetY) {
		this.textOffsetX = textOffsetX;
		this.textOffsetY = textOffsetY;
		if (text != null)
			calculateTextTransform();
	}

	public void setTextOffsetX(float textOffsetX) {
		this.textOffsetX = textOffsetX;
		if (text != null)
			calculateTextTransform();
	}

	public float getTextOffsetX() {
		return textOffsetX;
	}

	public void setTextOffsetY(float textOffsetY) {
		this.textOffsetY = textOffsetY;
		if (text != null)
			calculateTextTransform();
	}

	public float getTextOffsetY() {
		return textOffsetY;
	}
}
