package org.jgine.system;

import maxLibs.utils.Reflection;

public interface SystemObject {

	@SuppressWarnings("unchecked")
	public default <T extends SystemObject> T copy() {
		return (T) Reflection.clone(this);
	}
}
