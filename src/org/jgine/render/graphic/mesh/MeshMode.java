package org.jgine.render.graphic.mesh;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class MeshMode {

	public static final int TRIANGLES = GL_TRIANGLES;
	public static final int TRIANGLE_STRIP = GL_TRIANGLE_STRIP;
	public static final int LINES = GL_LINES;
	public static final int LINE_STRIP = GL_LINE_STRIP;
	public static final int LINE_LOOP = GL_LINE_LOOP;
	public static final int POINTS = GL_POINTS;
}
