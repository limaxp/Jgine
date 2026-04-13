package jgine.render.material;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Map;

import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

import jgine.core.Engine;
import jgine.render.material.TextureAnimationHandler.TextureAnimation;
import jgine.render.shader.Shader;
import jgine.system.SystemObject;
import jgine.utils.Color;
import jgine.utils.Logger;
import jgine.utils.ObjectUtils;
import jgine.utils.loader.ResourceManager;
import jgine.utils.math.vector.Vector4f;

public class Material implements SystemObject {

	public int ambientColor;
	public int diffuseColor;
	public int specularColor;
	public int emissiveColor;
	public int transparentColor;
	public int color;
	public float specularIntesity;
	public float specularPower; // how wide
	private ITexture texture = Texture.NONE;
	private TextureAnimationHandler animationHandler = TextureAnimationHandler.NONE;
	private float textureX = 0.0f;
	private float textureY = 0.0f;
	private float textureWidth = 1.0f;
	private float textureHeight = 1.0f;
	private float flipX = 1.0f;
	private float flipY = 1.0f;

	public Material() {
		this.color = Color.WHITE;
	}

	public Material(int color) {
		this.color = color;
	}

	public Material(ITexture texture) {
		this(Color.WHITE, texture);
	}

	public Material(int color, ITexture texture) {
		this.color = color;
		setTexture(texture);
	}

	public final void bind(Shader shader) {
		if (animationHandler != TextureAnimationHandler.NONE)
			setAnimationFrame(animationHandler.getAnimation(), animationHandler.getAnimationFrame());
		texture.bind();
		shader.setMaterial(this);
	}

	public final void unbind() {
		texture.unbind();
	}

	public final void set(Material material) {
		apply(material);
		setTexture(material.texture);
		textureX = material.textureX;
		textureY = material.textureY;
		textureWidth = material.textureWidth;
		textureHeight = material.textureHeight;
	}

	public final void apply(Material material) {
		color = material.color;
		specularIntesity = material.specularIntesity;
		specularPower = material.specularPower;
		ambientColor = material.ambientColor;
		diffuseColor = material.diffuseColor;
		specularColor = material.specularColor;
		emissiveColor = material.emissiveColor;
		transparentColor = material.transparentColor;
		flipX = material.flipX;
		flipY = material.flipY;
	}

	public final void setTexture(ITexture texture) {
		this.texture = texture;
		this.animationHandler = texture.createAnimationHandler();
	}

	public final ITexture getTexture() {
		return texture;
	}

	public void setTextureX(float textureX) {
		this.textureX = textureX;
	}

	public float getTextureX() {
		return textureX;
	}

	public void setTextureY(float textureY) {
		this.textureY = textureY;
	}

	public float getTextureY() {
		return textureY;
	}

	public void setTextureWidth(float textureWidth) {
		this.textureWidth = textureWidth;
	}

	public float getTextureWidth() {
		return textureWidth;
	}

	public void setTextureHeight(float textureHeight) {
		this.textureHeight = textureHeight;
	}

	public float getTextureHeight() {
		return textureHeight;
	}

	public void setTextureOffsets(float x, float y, float width, float height) {
		textureX = x;
		textureY = y;
		textureWidth = width;
		textureHeight = height;
	}

	public Vector4f getTextureOffsets() {
		return new Vector4f(textureX, textureY, textureWidth, textureHeight);
	}

	private void setAnimationFrame(TextureAnimation animation, int animationFrame) {
		textureX = animation.getX(animationFrame);
		textureY = animation.getY(animationFrame);
		textureWidth = animation.getWidth(animationFrame) * flipX;
		textureHeight = animation.getHeight(animationFrame) * flipY;
	}

	public void flipX() {
		flipX *= -1.0f;
		textureWidth *= -1;
	}

	public boolean isflippedX() {
		return flipX < 0.0f;
	}

	public void flipY() {
		flipY *= -1.0f;
		textureHeight *= -1;
	}

	public boolean isflippedY() {
		return flipY < 0.0f;
	}

	public void load(AIMaterial material) {
		ambientColor = loadColorAssimp(material, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0);
		diffuseColor = loadColorAssimp(material, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0);
		specularColor = loadColorAssimp(material, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0);
		emissiveColor = loadColorAssimp(material, Assimp.AI_MATKEY_COLOR_EMISSIVE, Assimp.aiTextureType_NONE, 0);
		transparentColor = loadColorAssimp(material, Assimp.AI_MATKEY_COLOR_TRANSPARENT, Assimp.aiTextureType_NONE, 0);
		String texture = loadTextureAssimp(material, Assimp.aiTextureType_DIFFUSE, 0);
		if (texture.length() > 0) {
			int index = texture.lastIndexOf(".");
			if (index != -1)
				texture = texture.substring(0, index);
			setTexture(ResourceManager.getTexture(texture));
		}
	}

	@Override
	public void load(Map<String, Object> data) {
		this.color = ObjectUtils.toColor(data.get("color"), color);
		this.ambientColor = ObjectUtils.toColor(data.get("ambientColor"), ambientColor);
		this.diffuseColor = ObjectUtils.toColor(data.get("diffuseColor"), diffuseColor);
		this.specularColor = ObjectUtils.toColor(data.get("specularColor"), specularColor);
		this.emissiveColor = ObjectUtils.toColor(data.get("emissiveColor"), emissiveColor);
		this.transparentColor = ObjectUtils.toColor(data.get("transparentColor"), transparentColor);
		Object texture = data.get("texture");
		if (texture instanceof String)
			setTexture(ResourceManager.getTexture((String) texture));
		this.textureX = ObjectUtils.toFloat(data.get("textureX"), textureX);
		this.textureY = ObjectUtils.toFloat(data.get("textureY"), textureY);
		this.textureWidth = ObjectUtils.toFloat(data.get("textureWidth"), textureWidth);
		this.textureHeight = ObjectUtils.toFloat(data.get("textureHeight"), textureHeight);
		Object flipX = data.get("flipX");
		if (flipX instanceof Boolean)
			if ((Boolean) flipX)
				flipX();
		Object flipY = data.get("flipY");
		if (flipY instanceof Boolean)
			if ((Boolean) flipY)
				flipY();
	}

	@Override
	public final void save(Map<String, Object> data) {
		data.put("color", color);
		data.put("ambientColor", ambientColor);
		data.put("diffuseColor", diffuseColor);
		data.put("specularColor", specularColor);
		data.put("emissiveColor", emissiveColor);
		data.put("transparentColor", transparentColor);
		data.put("texture", texture.getName());
		data.put("textureX", textureX);
		data.put("textureY", textureY);
		data.put("textureWidth", textureWidth);
		data.put("textureHeight", textureHeight);
		data.put("flipX", isflippedX());
		data.put("flipY", isflippedY());
	}

	@Override
	public void load(DataInput in) throws IOException {
		color = in.readInt();
		ambientColor = in.readInt();
		diffuseColor = in.readInt();
		specularColor = in.readInt();
		emissiveColor = in.readInt();
		transparentColor = in.readInt();
		String textureName = in.readUTF();
		if (!textureName.isEmpty())
			setTexture(ResourceManager.getTexture(textureName));
		textureX = in.readFloat();
		textureY = in.readFloat();
		textureWidth = in.readFloat();
		textureHeight = in.readFloat();
		flipX = in.readFloat();
		flipY = in.readFloat();
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeInt(color);
		out.writeInt(ambientColor);
		out.writeInt(diffuseColor);
		out.writeInt(specularColor);
		out.writeInt(emissiveColor);
		out.writeInt(transparentColor);
		out.writeUTF(texture.getName());
		out.writeFloat(textureX);
		out.writeFloat(textureY);
		out.writeFloat(textureWidth);
		out.writeFloat(textureHeight);
		out.writeFloat(flipX);
		out.writeFloat(flipY);
	}

	@Override
	public int system() {
		return Engine.GRAPHIC_2D;
	}

	@Override
	public Material clone() {
		try {
			return (Material) super.clone();
		} catch (CloneNotSupportedException e) {
			Logger.err("Material: Error on clone!", e);
			return null;
		}
	}

	@Override
	public String toString() {
		return super.toString() + " [texture: " + (texture.getName().isEmpty() ? "none" : texture.getName()) + "("
				+ textureX + "," + textureX + "," + textureWidth + "," + textureHeight
				+ (isflippedX() ? ",xFlipped" : "") + (isflippedY() ? ",yFlipped" : "") + ")" + " | color: "
				+ Color.toString(color) + "]";
	}

	protected static String loadStringAssimp(AIMaterial material, String key, int textureType, int textureIndex) {
		AIString aiString = AIString.calloc();
		if (Assimp.aiGetMaterialString(material, key, textureType, textureIndex, aiString) != 0)
			throw new IllegalStateException(Assimp.aiGetErrorString());
		return aiString.dataString();
	}

	protected static int loadColorAssimp(AIMaterial material, String key, int textureType, int textureIndex) {
		AIColor4D aiColor = AIColor4D.create();
		if (Assimp.aiGetMaterialColor(material, key, textureType, textureIndex, aiColor) != 0)
			throw new IllegalStateException(Assimp.aiGetErrorString());
		return Color.rgba(aiColor.r(), aiColor.g(), aiColor.b(), aiColor.a());
	}

	protected static String loadTextureAssimp(AIMaterial material, int textureType, int textureIndex) {
		AIString aiString = AIString.calloc();
		Assimp.aiGetMaterialTexture(material, textureType, textureIndex, aiString, (IntBuffer) null, null, null, null,
				null, null);
		return aiString.dataString();
	}
}