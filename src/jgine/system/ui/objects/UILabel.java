package jgine.system.ui.objects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import jgine.core.input.Input;
import jgine.render.Renderer;
import jgine.render.UIRenderer;
import jgine.render.material.Material;
import jgine.render.text.BitmapFont;
import jgine.render.text.BitmapText;
import jgine.render.text.Font;
import jgine.render.text.Text;
import jgine.render.text.TextBuilder;
import jgine.render.text.TrueTypeFont;
import jgine.render.text.TrueTypeText;
import jgine.system.ui.UIObject;
import jgine.system.ui.UIObjectType;
import jgine.utils.ObjectUtils;
import jgine.utils.math.Matrix;
import jgine.utils.math.vector.Vector2i;
import jgine.utils.math.vector.Vector3f;
import jgine.utils.scheduler.Scheduler;

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
			Scheduler.runTask(text::close);
	}

	@Override
	public void render() {
		UIRenderer.renderQuad(getTransform(), usedBackground);
		if (text != null) {
			Renderer.setShader(Renderer.TEXT_SHADER);
			UIRenderer.render(textTransform, text.getMesh(), text.getMaterial());
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

	@Override
	public void load(Map<String, Object> data) {
		super.load(data);
		ObjectUtils.toMaterial(background, data.get("background"));
		ObjectUtils.toMaterial(backgroundFocused, data.get("focused"));

		Object textData = data.get("text");
		if (textData instanceof String) {
			int textType = ObjectUtils.toTextType(data.get("textType"));
			int textSize = ObjectUtils.toInt(data.get("textSize"), TextBuilder.MAX_SIZE);
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
		textOffsetX = ObjectUtils.toFloat(data.get("textOffsetX"), textOffsetX);
		textOffsetY = ObjectUtils.toFloat(data.get("textOffsetY"), textOffsetY);
		Object textOffsetData = data.get("textOffset");
		if (textOffsetData != null) {
			if (textOffsetData instanceof Number)
				textOffsetX = textOffsetY = ((Number) textOffsetData).floatValue();
			else if (textOffsetData instanceof List textOffsetList) {
				if (textOffsetList.size() >= 2) {
					textOffsetX = ObjectUtils.toFloat(textOffsetList.get(0));
					textOffsetX = ObjectUtils.toFloat(textOffsetList.get(1));
				}
			}
		}
	}

	@Override
	public void save(Map<String, Object> data) {
		super.save(data);
		Map<String, Object> backgroundMap = new HashMap<String, Object>();
		background.save(backgroundMap);
		data.put("background", backgroundMap);
		Map<String, Object> focusedMap = new HashMap<String, Object>();
		backgroundFocused.save(focusedMap);
		data.put("focused", focusedMap);
		data.put("text", text.getText());
		data.put("textType", text.getType() == Text.TYPE_BITMAP ? "bitmap" : "truetype");
		data.put("textSize", text.getSize());
		data.put("font", text.getFont().getName());
		data.put("textOffsetX", textOffsetX);
		data.put("textOffsetY", textOffsetY);
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
		return UIObjectType.LABEL;
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
			Scheduler.runTask(text::close);
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
