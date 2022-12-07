package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.Matrix;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.render.UIRenderer;
import org.jgine.render.graphic.material.Material;
import org.jgine.render.graphic.material.Texture;
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
	private Material backgroundFocused;
	private Material usedBackground;
	private Text text;
	private float textOffsetX;
	private float textOffsetY;
	private Matrix textTransform;

	public UILabel() {
		background = new Material(Color.DARK_GRAY);
		backgroundFocused = new Material(Color.GRAY);
		usedBackground = background;
		textTransform = new Matrix();
	}

	@Override
	public UILabel clone() {
		UILabel obj = (UILabel) super.clone();
		obj.background = background.clone();
		obj.backgroundFocused = backgroundFocused.clone();
		obj.usedBackground = obj.background;
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
		UIRenderer.renderQuad(getTransform(), usedBackground);
		if (text != null)
			UIRenderer.render(textTransform, text.getMesh(), text.getMaterial());
	}

	@Override
	public void onFocus() {
		super.onFocus();
		usedBackground = backgroundFocused;
	}

	@Override
	public void onDefocus() {
		super.onDefocus();
		usedBackground = background;
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

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		Object backgroundData = data.get("background");
		if (backgroundData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) backgroundData);
			if (texture != null)
				background.setTexture(texture);
		} else if (backgroundData instanceof Map)
			background.load((Map<String, Object>) backgroundData);
		Object backgroundFocusedData = data.get("focused");
		if (backgroundFocusedData instanceof String) {
			Texture texture = ResourceManager.getTexture((String) backgroundFocusedData);
			if (texture != null)
				backgroundFocused.setTexture(texture);
		} else if (backgroundData instanceof Map)
			backgroundFocused.load((Map<String, Object>) backgroundFocusedData);

		Object textData = data.get("text");
		if (textData instanceof String) {
			int type = YamlHelper.toTextType(data.get("textType"));
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
		Object textOffsetXData = data.get("textOffsetX");
		if (textOffsetXData != null)
			textOffsetX = YamlHelper.toFloat(textOffsetXData);
		Object textOffsetYData = data.get("textOffsetY");
		if (textOffsetYData != null)
			textOffsetY = YamlHelper.toFloat(textOffsetYData);
		Object textOffsetData = data.get("textOffset");
		if (textOffsetData != null) {
			if (textOffsetData instanceof Number)
				textOffsetX = textOffsetY = ((Number) textOffsetData).floatValue();
			else if (textOffsetData instanceof List) {
				List<Object> textOffsetList = (List<Object>) textOffsetData;
				if (textOffsetList.size() >= 2) {
					textOffsetX = YamlHelper.toFloat(textOffsetList.get(0));
					textOffsetX = YamlHelper.toFloat(textOffsetList.get(1));
				}
			}
		}
	}

	@Override
	public void load(DataInput in) throws IOException {
		super.load(in);
		background.load(in);
		backgroundFocused.load(in);
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
		backgroundFocused.save(out);
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

	public void setBackgroundFocused(Material backgroundFocused) {
		this.backgroundFocused = backgroundFocused;
	}

	public Material getBackgroundFocused() {
		return backgroundFocused;
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
