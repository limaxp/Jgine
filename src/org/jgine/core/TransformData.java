package org.jgine.core;

import java.util.List;
import java.util.Map;

import org.jgine.core.entity.Prefab;
import org.jgine.utils.loader.YamlHelper;

/**
 * Used by {@link Prefab} class to store {@link Transform} data.
 */
public class TransformData {

	public float posX;
	public float posY;
	public float posZ;
	public float rotX;
	public float rotY;
	public float rotZ;
	public float scaleX = 1.0f;
	public float scaleY = 1.0f;
	public float scaleZ = 1.0f;

	public TransformData load(Map<String, Object> data) {
		Object position = data.get("position");
		if (position instanceof Number)
			posX = posY = posZ = ((Number) position).floatValue();
		else if (position instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> positionList = (List<Object>) position;
			if (positionList.size() >= 3) {
				posX = YamlHelper.toFloat(positionList.get(0));
				posY = YamlHelper.toFloat(positionList.get(1));
				posZ = YamlHelper.toFloat(positionList.get(2));
			}
		} else if (position instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> positionMap = (Map<String, Object>) position;
			posX = YamlHelper.toFloat(positionMap.get("x"));
			posY = YamlHelper.toFloat(positionMap.get("y"));
			posZ = YamlHelper.toFloat(positionMap.get("z"));
		}

		Object rotation = data.get("rotation");
		if (rotation instanceof Number)
			rotX = rotY = rotZ = ((Number) rotation).floatValue();
		else if (rotation instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> rotationList = (List<Object>) rotation;
			if (rotationList.size() >= 3) {
				rotX = YamlHelper.toFloat(rotationList.get(0));
				rotY = YamlHelper.toFloat(rotationList.get(1));
				rotZ = YamlHelper.toFloat(rotationList.get(2));
			}
		} else if (rotation instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> rotationMap = (Map<String, Object>) rotation;
			rotX = YamlHelper.toFloat(rotationMap.get("x"));
			rotY = YamlHelper.toFloat(rotationMap.get("y"));
			rotZ = YamlHelper.toFloat(rotationMap.get("z"));
		}

		Object scale = data.get("scale");
		if (scale instanceof Number)
			scaleX = scaleY = scaleZ = ((Number) scale).floatValue();
		else if (scale instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> scaleList = (List<Object>) scale;
			if (scaleList.size() >= 3) {
				scaleX = YamlHelper.toFloat(scaleList.get(0));
				scaleY = YamlHelper.toFloat(scaleList.get(1));
				scaleZ = YamlHelper.toFloat(scaleList.get(2));
			}
		} else if (scale instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> scaleMap = (Map<String, Object>) scale;
			scaleX = YamlHelper.toFloat(scaleMap.get("x"));
			scaleY = YamlHelper.toFloat(scaleMap.get("y"));
			scaleZ = YamlHelper.toFloat(scaleMap.get("z"));
		}
		return this;
	}
}
