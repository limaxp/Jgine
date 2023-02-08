package org.jgine.render.graphic.text;

public class TextBuilder {

	public static final int MIN_SIZE = 6;
	public static final int MAX_SIZE = 96;

	public static Text createText(String text, Font font, int size, int width, int height) {
		if (font instanceof TrueTypeFont)
			return TextBuilder.createText(text, (TrueTypeFont) font, size, width, height);
		else if (font instanceof BitmapFont)
			return TextBuilder.createText(text, (BitmapFont) font, size, width, height);
		return null;
	}

	public static TrueTypeText createText(String text, TrueTypeFont font, int size, int width, int height) {
		size = calculateSize(text, font, size, width, height);
		text = formatString(text, font, size, width);
//		return new TrueTypeText(font, size, text, 0, -height + (font.getStringHeight(size) * 2));
		return new TrueTypeText(font, size, text, 0, 0);
	}

	public static BitmapText createText(String text, BitmapFont font, int size, int width, int height) {
		size = calculateSize(text, font, size, width, height);
		text = formatString(text, font, size, width);
		float scale = (float) size / 64;
//		return new BitmapText(font, size, text, 0, height * scale + font.getStringHeight(size));
		return new BitmapText(font, size, text, 0, 0);
	}

	public static String formatString(String text, Font font, int size, int width) {
		if (!text.contains(" "))
			return text;
		StringBuilder stringBuilder = new StringBuilder((int) (text.length() * 1.2f));
		float currentWidth = 0;
		int start = 0;
		int end;
		do {
			end = text.indexOf(' ', start + 1);
			if (end == -1)
				end = text.length();
			float splitWidth = font.getStringWidth(text, start, end, size);
			if (currentWidth + splitWidth > width) {
				stringBuilder.append('\n');
				currentWidth = splitWidth;
			} else
				currentWidth += splitWidth;
			stringBuilder.append(text, start, end);
			stringBuilder.append(' ');
			start = end + 1;
		} while (end != text.length());
		return stringBuilder.toString();
	}

	public static int calculateSize(String text, BitmapFont font, int size, int width, int height) {
		while (size > MIN_SIZE) {
			int maxLineSize = (int) (height / font.getStringHeight(size));
			if (maxLineSize < 1) {
				size *= 0.9f;
				continue;
			}
			int lineSize = (int) ((width * maxLineSize) / font.getStringWidth(text, size));
			if (lineSize < 1) {
				size *= 0.9f;
				continue;
			}
			break;
		}
		return size;
	}

	public static int calculateSize(String text, TrueTypeFont font, int size, int width, int height) {
		for (int i = TrueTypeFontGenerated.getSizeIndex(size); i >= 0; i--) {
			size = TrueTypeFontGenerated.getSize(i);
			int maxLineSize = (int) (height / font.getStringHeight(size));
			if (maxLineSize < 1)
				continue;
			int lineSize = (int) ((width * maxLineSize) / font.getStringWidth(text, size));
			if (lineSize < 1)
				continue;
			break;
		}
		return size;
	}
}
