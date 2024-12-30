package org.jgine.system.systems.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.input.Input;
import org.jgine.render.Renderer;
import org.jgine.render.UIRenderer;
import org.jgine.render.material.Material;
import org.jgine.render.material.Texture;
import org.jgine.render.mesh.text.BitmapFont;
import org.jgine.render.mesh.text.BitmapText;
import org.jgine.render.mesh.text.Font;
import org.jgine.render.mesh.text.Text;
import org.jgine.render.mesh.text.TextBuilder;
import org.jgine.render.mesh.text.TrueTypeFont;
import org.jgine.render.mesh.text.TrueTypeText;
import org.jgine.system.systems.ui.UIObject;
import org.jgine.system.systems.ui.UIObjectType;
import org.jgine.system.systems.ui.UIObjectTypes;
import org.jgine.utils.loader.ResourceManager;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.math.Matrix;
import org.jgine.utils.math.vector.Vector2i;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.scheduler.Scheduler;

public class UILabel extends UIObject {

	private Material background;
	private Material backgroundFocused;
	private Material usedBackground;
	private Text text;
	private float textOffsetX;
	private float textOffsetY;
	private Matrix textTransform;

	public UILabel() {
		background = new Material(BACKGROUND_COLOR);
		backgroundFocused = new Material(FOCUS_COLOR);
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
			obj.text = new TrueTypeText((TrueTypeFont) text.getFont(), text.getSize(), text.getText(),
					text.getxOffset(), text.getyOffset());
		else if (text instanceof BitmapText)
			obj.text = new BitmapText((BitmapFont) text.getFont(), text.getSize(), text.getText(), text.getxOffset(),
					text.getyOffset());
		obj.textTransform = new Matrix(textTransform);
		return obj;
	}

	@Override
	protected void free() {
		if (text != null)
			text.close();
	}

	@Override
	public void render(int depth) {
		UIRenderer.renderQuad(getTransform(), usedBackground, depth);
		if (text != null) {
			Renderer.setShader(Renderer.TEXT_SHADER);
			UIRenderer.render(textTransform, text.getMesh(), text.getMaterial(), depth + 2);
			Renderer.setShader(Renderer.TEXTURE_SHADER);
		}
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
		if (text != null)
			calculateTextTransform();
	}

	protected void calculateTextTransform() {
		textTransform.clear();
		textTransform.setPosition(-1.0f + (getX() + textOffsetX) * 2.0f, -1.0f + (getY() + textOffsetY) * 2.0f, 0.0f);
		getParent().updateTransform(textTransform);
		Vector3f windowScale = getWindow().getTransform().getScaling();
		textTransform.scaling(1 / windowScale.x * 0.00225f, 1 / windowScale.y * 0.004f, 0.0f);
	}

	public void buildText(String text, Font font) {
		buildText(text, font, TextBuilder.MAX_SIZE);
	}

	public void buildText(String text, Font font, int size) {
		Vector3f scale = getTransform().getScaling();
		Vector3f windowScale = getWindow().getTransform().getScaling();
		Vector2i windowSize = Input.getWindowSize();
		int width = (int) (windowSize.x * scale.x * windowScale.x);
		int height = (int) (windowSize.y * scale.y * windowScale.y);
		setText(TextBuilder.createText(text, font, size, width, height));
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
			int textType = YamlHelper.toTextType(data.get("textType"));
			int textSize = YamlHelper.toInt(data.get("textSize"), TextBuilder.MAX_SIZE);
			if (textType == Text.TYPE_TRUETYPE) {
				TrueTypeFont font = TrueTypeFont.ARIAL;
				Object fontData = data.get("font");
				if (fontData instanceof String) {
					TrueTypeFont font2 = TrueTypeFont.get((String) textData);
					if (font2 != null)
						font = font2;
				}
				buildText((String) textData, font, textSize);
			} else if (textType == Text.TYPE_BITMAP) {
				BitmapFont font = BitmapFont.CONSOLAS;
				Object fontData = data.get("font");
				if (fontData instanceof String) {
					BitmapFont font2 = BitmapFont.get((String) textData);
					if (font2 != null)
						font = font2;
				}
				buildText((String) textData, font, textSize);
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
		int textType = in.readInt();
		if (textType == Text.TYPE_TRUETYPE) {
			TrueTypeFont font = TrueTypeFont.get(in.readUTF());
			if (font == null)
				font = TrueTypeFont.ARIAL;
			setText(this.text = new TrueTypeText(font, in.readInt(), in.readUTF()));
		} else if (textType == Text.TYPE_BITMAP) {
			BitmapFont font = BitmapFont.get(in.readUTF());
			if (font == null)
				font = BitmapFont.CONSOLAS;
			setText(this.text = new BitmapText(font, in.readInt(), in.readUTF()));
		}
		textOffsetX = in.readInt();
		textOffsetY = in.readInt();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		super.save(out);
		background.save(out);
		backgroundFocused.save(out);
		out.writeInt(text.getType());
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
		if (this.text != null)
			Scheduler.runTaskSynchron(this.text.getMesh()::close);
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
