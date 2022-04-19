package org.jgine.render.graphic.material;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

import java.nio.IntBuffer;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.math.vector.Vector3f;
import org.jgine.misc.math.vector.Vector4f;
import org.jgine.misc.utils.logger.Logger;
import org.jgine.render.shader.Shader;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

public class Material implements Cloneable {

	public Vector4f color;
	public float specularIntesity;
	public float specularPower; // how wide
	public Vector4f ambientColor;
	public Vector4f diffuseColor;
	public Vector4f specularColor;
	private Texture texture = Texture.NONE;
	// this here makes every entity to move at same time
	private TextureAnimationHandler animationHandler = TextureAnimationHandler.NONE;
	private int texturePos = 1;

	public Material() {
		this.color = Vector4f.FULL;
	}

	public Material(Vector3f color) {
		this.color = new Vector4f(color);
	}

	public Material(Vector4f color) {
		this.color = color;
	}

	public Material(Texture texture) {
		this.color = Vector4f.FULL;
		setTexture(texture);
	}

	public Material(Vector3f color, Texture texture) {
		this.color = new Vector4f(color);
		setTexture(texture);
	}

	public Material(Vector4f color, Texture texture) {
		this.color = color;
		setTexture(texture);
	}

	public Material(AIMaterial material) {
		this.color = Vector4f.FULL;
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null,
				null, null);
		String texturePath = path.dataString();
		if (texturePath != null && texturePath.length() > 0)
			texture = ResourceManager.getTexture(texturePath, this);

		AIColor4D mAmbientColor = AIColor4D.create();
		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_AMBIENT,
				aiTextureType_NONE, 0, mAmbientColor) != 0) {
			throw new IllegalStateException(aiGetErrorString());
		}
		ambientColor = new Vector4f(mAmbientColor.r(), mAmbientColor.g(), mAmbientColor.b(), mAmbientColor.a());

		AIColor4D mDiffuseColor = AIColor4D.create();
		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE,
				aiTextureType_NONE, 0, mDiffuseColor) != 0) {
			throw new IllegalStateException(aiGetErrorString());
		}
		diffuseColor = new Vector4f(mDiffuseColor.r(), mDiffuseColor.g(), mDiffuseColor.b(), mDiffuseColor.a());

		AIColor4D mSpecularColor = AIColor4D.create();
		if (aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR,
				aiTextureType_NONE, 0, mSpecularColor) != 0) {
			throw new IllegalStateException(aiGetErrorString());
		}
		specularColor = new Vector4f(mSpecularColor.r(), mSpecularColor.g(), mSpecularColor.b(), mSpecularColor.a());
	}

	@Override
	public Material clone() {
		Material obj;
		try {
			obj = (Material) super.clone();
			obj.color = color;
			obj.specularIntesity = specularIntesity;
			obj.specularPower = specularPower;
			obj.ambientColor = ambientColor;
			obj.diffuseColor = diffuseColor;
			obj.specularColor = specularColor;
			obj.texture = texture;
			obj.texturePos = texturePos;
			return obj;
		} catch (CloneNotSupportedException e) {
			Logger.err("Material: Error on clone!", e);
			return null;
		}
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
	}

	public final void setTexture(Texture texture) {
		this.texture = texture;
		this.animationHandler = texture.createAnimationHandler();
		texturePos = 1;
	}

	public final Texture getTexture() {
		return texture;
	}

	public int getTexturePosition() {
		return texturePos;
	}
}