package org.jgine.render.graphic.text;

public class TextBuilder {

	public static TrueTypeText createText(String text, TrueTypeFont font, int size, int width, int height) {
		size = calculateSize(text, font, size, width, height);
		text = formatString(text, font, size, width);
		return new TrueTypeText(font, size, text);
	}

	public static BitmapText createText(String text, BitmapFont font, int size, int width, int height) {
		size = calculateSize(text, font, size, width, height);
		text = formatString(text, font, size, width);
		return new BitmapText(font, size, text);
	}

	public static String formatString(String text, Font font, int size, int width) {
		String[] splits = text.split(" ");
		if (splits.length == 0)
			return text;
		StringBuilder stringBuilder = new StringBuilder(text.length());
		float currentWidth = 0;
		for (int i = 0; i < splits.length; i++) {
			String split = splits[i];
			float splitWidth = font.getStringWidth(split, size);
			if (currentWidth + splitWidth > width) {
				stringBuilder.append('\n');
				stringBuilder.append(split);
				currentWidth = splitWidth;
			} else {
				stringBuilder.append(split);
				stringBuilder.append(' ');
				currentWidth += splitWidth;
			}
		}
		return stringBuilder.toString();
	}

	public static int calculateSize(String text, Font font, int size, int width, int height) {
		while (true) {
			int maxLineSize = (int) (height / font.getStringHeight(size));
			int lineSize = (int) (font.getStringWidth(text, size) / width);
			if (lineSize > maxLineSize)
				size *= 0.9f;
			else
				break;
		}
		return size;
	}
}
