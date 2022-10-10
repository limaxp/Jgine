package org.jgine.render.shader;

import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

import org.jgine.core.manager.ResourceManager;
import org.jgine.misc.utils.logger.Logger;

public abstract class ComputeShader extends Shader {

	public ComputeShader(String compute) {
		super(glCreateProgram());
		if (program == 0) {
			Logger.err("ComputeShader: Creation failed!");
			return;
		}

		int computeShader = compileShader(GL_COMPUTE_SHADER, ResourceManager.getShader(compute));
		linkShader();
		glDeleteShader(computeShader);
	}
}
