package org.jgine.render.graphic.mesh;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_LINE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class MeshMode {

	public static final int POINTS = GL_POINTS;
	public static final int LINES = GL_LINES;
	public static final int LINE_LOOP = GL_LINE_LOOP;
	public static final int LINE_STRIP = GL_LINE_STRIP;
	public static final int TRIANGLES = GL_TRIANGLES;
	public static final int TRIANGLE_STRIP = GL_TRIANGLE_STRIP;
	public static final int TRIANGLE_FAN =  GL_TRIANGLE_FAN;
	public static final int QUADS = GL_QUADS;
	public static final int LINES_ADJACENCY = GL_LINES_ADJACENCY;
	public static final int LINE_STRIP_ADJACENCY =  GL_LINE_STRIP_ADJACENCY;
	public static final int TRIANGLES_ADJACENCY  =  GL_TRIANGLES_ADJACENCY ;
	public static final int TRIANGLE_STRIP_ADJACENCY = GL_TRIANGLE_STRIP_ADJACENCY;
	public static final int PATCHES  = GL_PATCHES ;
}
