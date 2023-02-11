package org.jgine.render.mesh;

import java.util.ArrayList;
import java.util.List;

import org.jgine.render.material.Material;
import org.jgine.render.shader.Shader;
import org.jgine.system.SystemObject;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

public class Model implements SystemObject, AutoCloseable {

	private static final Mesh[] EMPTY_MESHES = new Mesh[0];

	public final String name;
	private Mesh[] meshes;
	private Material[] materials;

	public Model() {
		this("");
	}

	public Model(String name) {
		this.name = name;
		meshes = EMPTY_MESHES;
	}

	@Override
	public void close() {
		for (Mesh mesh : meshes)
			mesh.close();
	}

	public final void render(Shader shader) {
		for (int i = 0; i < meshes.length; i++) {
			materials[i].bind(shader);
			meshes[i].render();
		}
	}

	public Mesh[] setMeshes(Mesh mesh, Material material) {
		return setMeshes(new Mesh[] { mesh }, new Material[] { material });
	}

	public Mesh[] setMeshes(Mesh[] meshes, Material material) {
		return setMeshes(meshes, new Material[] { material });
	}

	public Mesh[] setMeshes(Mesh[] meshes, Material[] materials) {
		Mesh[] old = this.meshes;
		this.meshes = meshes;
		this.materials = materials;
		return old;
	}

	public Mesh[] setAIMeshes(AIScene scene) {
		Mesh[] old = this.meshes;
		int meshCount = scene.mNumMeshes();
		meshes = new Mesh[meshCount];
		this.materials = new Material[meshCount];

		int materialCount = scene.mNumMaterials();
		PointerBuffer materialsBuffer = scene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < materialCount; ++i) {
			Material material = new Material();
			material.load(AIMaterial.create(materialsBuffer.get(i)));
			materials.add(material);
		}

		PointerBuffer meshesBuffer = scene.mMeshes();
		for (int i = 0; i < meshCount; ++i) {
			AIMesh aiMesh = AIMesh.create(meshesBuffer.get(i));
			@SuppressWarnings("resource")
			Mesh mesh = new Mesh(3, true);
			mesh.loadData(aiMesh);
			meshes[i] = mesh;
			this.materials[i] = materials.get(aiMesh.mMaterialIndex());
		}
		return old;
	}

	public Mesh[] getMeshes() {
		return meshes;
	}

	public Material[] getMaterials() {
		return materials;
	}
}