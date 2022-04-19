package org.jgine.render.graphic.mesh;

import java.util.Collection;

import org.jgine.misc.collection.list.arrayList.unordered.UnorderedIdentityArrayList;

public class Model2D extends UnorderedIdentityArrayList<Mesh2D> implements AutoCloseable {

	public Model2D() {}

	public Model2D(Collection<? extends Mesh2D> meshes) {
		super(meshes);
	}

	public Model2D(Mesh2D[] meshes) {
		super(meshes);
	}

	@Override
	public void close() {
		clear();
	}

	@Override
	public void clear() {
		for (Mesh2D mesh : this)
			mesh.close();
		super.clear();
	}
}