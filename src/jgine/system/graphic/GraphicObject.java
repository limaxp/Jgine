package jgine.system.graphic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import jgine.core.Engine;
import jgine.render.mesh.Model;
import jgine.system.SystemObject;
import jgine.utils.loader.ResourceManager;

public class GraphicObject implements SystemObject {

	protected Model model;

	public GraphicObject() {
	}

	public GraphicObject(Model model) {
		this.model = model;
	}

	@Override
	public void load(Map<String, Object> data) {
		Object model = data.get("model");
		if (model instanceof String)
			this.model = ResourceManager.getModel((String) model);
	}

	@Override
	public void save(Map<String, Object> data) {
		data.put("model", model.name);
	}

	@Override
	public void load(DataInput in) throws IOException {
		String modelName = in.readUTF();
		if (!modelName.isEmpty())
			this.model = ResourceManager.getModel(modelName);
	}

	@Override
	public void save(DataOutput out) throws IOException {
		out.writeUTF(model.name);
	}

	@Override
	public int system() {
		return Engine.GRAPHIC;
	}

	@Override
	public GraphicObject clone() {
		try {
			return (GraphicObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return model;
	}
}
