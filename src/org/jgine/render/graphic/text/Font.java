package org.jgine.render.graphic.text;

public interface Font {

	public String getName();
	
	public float getStringHeight(int fontHeight);
	
	public float getStringWidth(String text, int fontHeight);
	
	public float getStringWidth(String text, int from, int to, int fontHeight);
}
