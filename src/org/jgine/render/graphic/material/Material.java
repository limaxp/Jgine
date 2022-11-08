package org.jgine.render.graphic.material;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Map;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.Color;
import org.jgine.misc.utils.loader.YamlHelper;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.render.shader.Shader;
import org.jgine.system.SystemObject;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

public class Material implements SystemObject, Cloneable {

	public int ambientColor;
	public int diffuseColor;
	public int specularColor;
	public int emissiveColor;
	public int transparentColor;
	public int color;
	public float specularIntesity;
	public float specularPower; // how wide
	private ITexture texture = Texture.NONE;
	// this here makes every entity to move at same time
	private TextureAnimationHandler animationHandler = TextureAnimationHandler.NONE;
	private int texturePos = 1;

	public Material() {
		this.color = Color.WHITE;
	}

	public Material(int color) {
		this.color = color;
	}

	public Material(Vector3f color) {
		this.color = Color.rgb(color);
	}

	public Material(float r, float g, float b) {
		this.color = Color.rgb(r, g, b);
	}

	public Material(Vector4f color) {
		this.color = Color.rgba(color);
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
		this.color = Color.rgb(color);
		setTexture(texture);
	}

	public Material(float r, float g, float b, ITexture texture) {
		this.color = Color.rgb(r, g, b);
		setTexture(texture);
	}

	public Material(Vector4f color, ITexture texture) {
		this.color = Color.rgba(color);
		setTexture(texture);
	}

	public Material(float r, float g, float b, float a, ITexture texture) {
		this.color = Color.rgba(r, g, b, a);
		setTexture(texture);
	}

	public final void bind(Shader shader) {
		texturePos = animationHandler.getTexturePosition();
		texture.bind();
		shader.setMaterial(this);
	}

	public final void unbind() {
		texture.unbind();
	}

	public final void set(Material material) {
		color = material.color;
		specularIntesity = material.specularIntesity;
		specularPower = material.specularPower;
		ambientColor = material.ambientColor;
		diffuseColor = material.diffuseColor;
		specularColor = material.specularColor;
		emissiveColor = material.emissiveColor;
		transparentColor = material.transparentColor;
		texture = material.texture;
		texturePos = material.texturePos;
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
	}

	public final void setTexture(ITexture texture) {
		this.texture = texture;
		this.animationHandler = texture.createAnimationHandler();
		texturePos = 1;
	}

	public final ITexture getTexture() {
		return texture;
	}

	public int getTexturePosition() {
		return texturePos;
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
	}

	public void load(DataInput in) throws IOException {
		color = in.readInt();
		ambientColor = in.readInt();
		diffuseColor = in.readInt();
		specularColor = in.readInt();
		emissiveColor = in.readInt();
		transparentColor = in.readInt();
		setTexture(ResourceManager.getTexture(in.readUTF()));
	}

	public void save(DataOutput out) throws IOException {
		out.writeInt(color);
		out.writeInt(ambientColor);
		out.writeInt(diffuseColor);
		out.writeInt(specularColor);
		out.writeInt(emissiveColor);
		out.writeInt(transparentColor);
		out.writeUTF(texture.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends SystemObject> T copy() {
		return (T) clone();
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