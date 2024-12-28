package org.jgine.render.material;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Map;

import org.jgine.render.material.TextureAnimationHandler.TextureAnimation;
import org.jgine.render.shader.Shader;
import org.jgine.system.SystemObject;
import org.jgine.utils.Color;
import org.jgine.utils.loader.ResourceManager;
import org.jgine.utils.loader.YamlHelper;
import org.jgine.utils.logger.Logger;
import org.jgine.utils.math.vector.Vector3f;
import org.jgine.utils.math.vector.Vector4f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

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

	public Material(Vector3f color) {
		this.color = Color.rgb(color.x, color.y, color.z);
	}

	public Material(float r, float g, float b) {
		this.color = Color.rgb(r, g, b);
	}

	public Material(Vector4f color) {
		this.color = Color.rgba(color.x, color.y, color.z, color.w);
	}

	public Material(float r, float g, float b, float a) {
		this.color = Color.rgba(r, g, b, a);
	}

	public Material(ITexture texture) {
		this.color = Color.WHITE;
		setTexture(texture);
	}

	public Material(int color, ITexture texture) {
		this.color = color;
		setTexture(texture);
	}

	public Material(Vector3f color, ITexture texture) {
		this.color = Color.rgb(color.x, color.y, color.z);
		setTexture(texture);
	}

	public Material(float r, float g, float b, ITexture texture) {
		this.color = Color.rgb(r, g, b);
		setTexture(texture);
	}

	public Material(Vector4f color, ITexture texture) {
		this.color = Color.rgba(color.x, color.y, color.z, color.w);
		setTexture(texture);
	}

	public Material(float r, float g, float b, float a, ITexture texture) {
		this.color = Color.rgba(r, g, b, a);
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
		if (texture.length() > 0)
			setTexture(ResourceManager.getTexture(texture, this));
	}

	public void load(Map<String, Object> data) {
		Object colorData = data.get("color");
		if (colorData != null)
			this.color = YamlHelper.toColor(colorData, Color.WHITE);
		Object ambientColorData = data.get("ambientColor");
		if (ambientColorData != null)
			this.ambientColor = YamlHelper.toColor(ambientColorData);
		Object diffuseColorData = data.get("diffuseColor");
		if (diffuseColorData != null)
			this.diffuseColor = YamlHelper.toColor(diffuseColorData);
		Object specularColorData = data.get("specularColor");
		if (specularColorData != null)
			this.specularColor = YamlHelper.toColor(specularColorData);
		Object emissiveColorData = data.get("emissiveColor");
		if (emissiveColorData != null)
			this.emissiveColor = YamlHelper.toColor(emissiveColorData);
		Object transparentColorData = data.get("transparentColor");
		if (transparentColorData != null)
			this.transparentColor = YamlHelper.toColor(transparentColorData);
		Object texture = data.get("texture");
		if (texture instanceof String)
			setTexture(ResourceManager.getTexture((String) texture));
		Object textureX = data.get("textureX");
		if (textureX != null)
			this.textureX = YamlHelper.toFloat(textureX, 0.0f);
		Object textureY = data.get("textureY");
		if (textureY != null)
			this.textureY = YamlHelper.toFloat(textureY, 0.0f);
		Object textureWidth = data.get("textureWidth");
		if (textureWidth != null)
			this.textureWidth = YamlHelper.toFloat(textureWidth, 1.0f);
		Object textureHeight = data.get("textureHeight");
		if (textureHeight != null)
			this.textureHeight = YamlHelper.toFloat(textureHeight, 1.0f);
		Object flipX = data.get("flipX");
		if (flipX instanceof Boolean)
			if ((Boolean) flipX)
				flipX();
		Object flipY = data.get("flipY");
		if (flipY instanceof Boolean)
			if ((Boolean) flipY)
				flipY();
	}

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