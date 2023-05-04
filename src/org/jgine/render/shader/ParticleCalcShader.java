package org.jgine.render.shader;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.core.Transform;
import org.jgine.render.mesh.particle.Particle;

public class ParticleCalcShader extends Shader {

	public final int uniform_genPosition;
	public final int uniform_numToGenerate;

	public ParticleCalcShader(@Nullable String vertex, @Nullable String geometry, @Nullable String fragment) {
		super();
		compile(VERTEX_SHADER, vertex);
		compile(GEOMETRY_SHADER, geometry);
		compile(FRAGMENT_SHADER, fragment);
		setTransformFeedback(INTERLEAVED_ATTRIBS, "positionOut", "typeOut");
		link();
		uniform_genPosition = addUniform("genPosition");
		uniform_numToGenerate = addUniform("numToGenerate");
	}

	public void setParticle(Transform transform, Particle particle) {
		setUniform3f(uniform_genPosition, transform.getPosition());
		setUniformi(uniform_numToGenerate, particle.getNumToGenerate());
	}
}
