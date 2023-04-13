package org.jgine.utils.loader;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.render.material.TextureAnimationHandler.TextureAnimation;

public class TextureAnimationLoader {

	@Nullable
	public static TextureAnimation loadAnimation(File file) {
		Map<String, Object> data = YamlLoader.load(file);
		if (data == null)
			return null;
		return loadAnimation(data);
	}

	@Nullable
	public static TextureAnimation loadAnimation(InputStream is) {
		Map<String, Object> data = YamlLoader.load(is);
		if (data == null)
			return null;
		return loadAnimation(data);
	}

	@SuppressWarnings("unchecked")
	public static TextureAnimation loadAnimation(Map<String, Object> data) {
		int columsValue;
		int rowsValue;
		Object colums = data.get("colums");
		if (colums != null && colums instanceof Number)
			columsValue = (int) colums;
		else
			columsValue = 1;
		Object rows = data.get("rows");
		if (rows != null && rows instanceof Number)
			rowsValue = (int) rows;
		else
			rowsValue = 1;

		int baseFrameTime = 100;
		Object frameTime = data.get("frameTime");
		if (frameTime != null && frameTime instanceof Number)
			baseFrameTime = (int) frameTime;

		Object frames = data.get("frames");
		TextureAnimation animation;
		if (frames != null && frames instanceof List) {
			List<Object> frameList = (List<Object>) frames;
			animation = new TextureAnimation(frameList.size());
			for (Object frame : frameList) {
				if (frame instanceof Integer)
					animation.addFrame(baseFrameTime, ((Integer) frame).intValue(), columsValue, rowsValue);
				else if (frame instanceof String) {
					String frameString = (String) frame;
					int index = frameString.indexOf(" to ");
					int size;
					if (index != -1)
						size = Integer.parseUnsignedInt(frameString, index + 4, frameString.length(), 10);
					else if ((index = frameString.indexOf(" - ")) != -1)
						size = Integer.parseUnsignedInt(frameString, index + 3, frameString.length(), 10);
					else
						continue;
					int i = Integer.parseUnsignedInt(frameString, 0, index, 10);
					for (; i <= size; i++)
						animation.addFrame(baseFrameTime, i, columsValue, rowsValue);
				}
			}
		} else {
			int size = columsValue * rowsValue;
			animation = new TextureAnimation(size);
			for (int i = 0; i < size; i++)
				animation.setFrame(i, baseFrameTime, i + 1, columsValue, rowsValue);
		}

		Object frameTimes = data.get("frameTimes");
		if (frameTimes != null && frameTimes instanceof Map) {
			for (Entry<Integer, Object> entry : ((Map<Integer, Object>) frameTimes).entrySet()) {
				Object value = entry.getValue();
				if (value instanceof Number)
					animation.setTime(entry.getKey() - 1, (int) value);
			}
		}
		return animation;
	}
}
