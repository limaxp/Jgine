package org.jgine.system.systems.ui;

import java.util.Map;

import org.jgine.core.Scene;
import org.jgine.system.EngineSystem;

public class UISystem extends EngineSystem {

	@Override
	public UIScene createScene(Scene scene) {
		return new UIScene(this, scene);
	}

	@Override
	public UIObject load(Map<String, Object> data) {
		UIObjectType<?> uiObjectType;
		Object type = data.get("type");
		if (type instanceof String) {
			uiObjectType = UIObjectTypes.get((String) type);
			if (uiObjectType == null)
				uiObjectType = UIObjectTypes.LABEL;
		} else
			uiObjectType = UIObjectTypes.LABEL;
		UIObject object = uiObjectType.get();
		object.load(data);
		return object;
	}
}
