package org.jgine.render.graphic.mesh;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;
import org.jgine.render.graphic.material.Material;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

public class Model extends UnorderedIdentityArrayList<Mesh> implements AutoCloseable {

	public Model() {}

	public Model(Collection<? extends Mesh> meshes) {
		super(meshes);
	}

	public Model(Mesh[] meshes) {
		super(meshes);
	}

	public Model(AIScene scene) {
		int materialCount = scene.mNumMaterials();
		PointerBuffer materialsBuffer = scene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < materialCount; ++i)
			materials.add(new Material(AIMaterial.create(materialsBuffer.get(i))));

		int meshCount = scene.mNumMeshes();
		PointerBuffer meshesBuffer = scene.mMeshes();
		for (int i = 0; i < meshCount; ++i) {
			AIMesh aiMesh = AIMesh.create(meshesBuffer.get(i));
			Mesh mesh = new Mesh(aiMesh);
			mesh.material = materials.get(aiMesh.mMaterialIndex());
			add(mesh);
		}
		aiReleaseImport(scene);
	}

	@Override
	public void close() {
		clear();
	}

	@Override
	public void clear() {
		for (Mesh mesh : this)
			mesh.close();
		super.clear();
	}
}