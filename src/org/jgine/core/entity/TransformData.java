package org.jgine.core.entity;

import java.util.Map;

public class TransformData {

	public float posX;
	public float posY;
	public float posZ;
	public float rotX;
	public float rotY;
	public float rotZ;
	public float scaleX = 1;
	public float scaleY = 1;
	public float scaleZ = 1;

	public TransformData load(Map<String, Object> data) {
		Object position = data.get("position");
		if (position != null) {
			if (position instanceof Number)
				posX = posY = posZ = ((Number) position).floatValue();
			else if (position instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> positionMap = (Map<String, Object>) position;
				posX = ((Number) positionMap.getOrDefault("x", 0)).floatValue();
				posY = ((Number) positionMap.getOrDefault("y", 0)).floatValue();
				posZ = ((Number) positionMap.getOrDefault("z", 0)).floatValue();
			}
		}
		Object rotation = data.get("rotation");
		if (rotation != null) {
			if (rotation instanceof Number)
				rotX = rotY = rotZ = ((Number) rotation).floatValue();
			else if (rotation instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> rotationMap = (Map<String, Object>) rotation;
				rotX = ((Number) rotationMap.getOrDefault("x", 0)).floatValue();
				rotY = ((Number) rotationMap.getOrDefault("y", 0)).floatValue();
				rotZ = ((Number) rotationMap.getOrDefault("z", 0)).floatValue();
			}
		}
		Object scale = data.get("scale");
		if (scale != null) {
			if (scale instanceof Number)
				scaleX = scaleY = scaleZ = ((Number) scale).floatValue();
			else if (scale instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> scaleMap = (Map<String, Object>) scale;
				scaleX = ((Number) scaleMap.getOrDefault("x", 1)).floatValue();
				scaleY = ((Number) scaleMap.getOrDefault("y", 1)).floatValue();
				scaleZ = ((Number) scaleMap.getOrDefault("z", 1)).floatValue();
			}
		}
		return this;
	}
}
