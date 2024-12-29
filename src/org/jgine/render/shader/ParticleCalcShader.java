package org.jgine.render.shader;

import org.eclipse.jdt.annotation.Nullable;
import org.jgine.system.systems.particle.Particle;
import org.jgine.utils.math.FastMath;

public class ParticleCalcShader extends Shader {

	public final int uniform_timePassed;
	public final int uniform_genPosition;
	public final int uniform_genPositionRange;
	public final int uniform_numToGenerate;
	public final int uniform_genGravityVector;
	public final int uniform_randomSeed;
	public final int uniform_genVelocityMin;
	public final int uniform_genVelocityRange;
	public final int uniform_genLiveMin;
	public final int uniform_genLiveRange;
	public final int uniform_genSizeMin;
	public final int uniform_genSizeRange;
	public final int uniform_genColorMin;
	public final int uniform_genColorRange;

	public ParticleCalcShader(@Nullable String vertex, @Nullable String geometry, @Nullable String fragment) {
		super();
		compile(VERTEX_SHADER, vertex);
		compile(GEOMETRY_SHADER, geometry);
		compile(FRAGMENT_SHADER, fragment);
		setTransformFeedback(INTERLEAVED_ATTRIBS, "positionOut", "velocityOut", "colorOut", "lifeTimeOut", "sizeOut",
				"typeOut");
		link();
		uniform_timePassed = addUniform("timePassed");
		uniform_genPosition = addUniform("genPosition");
		uniform_genPositionRange = addUniform("genPositionRange");
		uniform_numToGenerate = addUniform("numToGenerate");
		uniform_genGravityVector = addUniform("genGravityVector");
		uniform_randomSeed = addUniform("randomSeed");
		uniform_genVelocityMin = addUniform("genVelocityMin");
		uniform_genVelocityRange = addUniform("genVelocityRange");
		uniform_genLiveMin = addUniform("genLiveMin");
		uniform_genLiveRange = addUniform("genLiveRange");
		uniform_genSizeMin = addUniform("genSizeMin");
		uniform_genSizeRange = addUniform("genSizeRange");
		uniform_genColorMin = addUniform("genColorMin");
		uniform_genColorRange = addUniform("genColorRange");
	}

	public void setParticle(Particle particle, float dt) {
		setUniformf(uniform_timePassed, dt);
		if (particle.checkSpawnTime(dt)) {
			setUniformi(uniform_numToGenerate, particle.amount);
			setUniform3f(uniform_randomSeed, FastMath.random(-10.0f, 20.0f), FastMath.random(-10.0f, 20.0f),
					FastMath.random(-10.0f, 20.0f));
		} else
			setUniformi(uniform_numToGenerate, 0);

		setUniform3f(uniform_genPosition, particle.getTransform().getPosition());
		setUniform3f(uniform_genPositionRange, particle.positionRange);
		setUniform3f(uniform_genVelocityMin, particle.velocityMin);
		setUniform3f(uniform_genVelocityRange, particle.velocityRange);
		setUniform3f(uniform_genGravityVector, particle.gravity);
		setUniformf(uniform_genLiveMin, particle.liveMin);
		setUniformf(uniform_genLiveRange, particle.liveRange);
		setUniformf(uniform_genSizeMin, particle.sizeMin);
		setUniformf(uniform_genSizeRange, particle.sizeRange);
		setUniformRGB(uniform_genColorMin, particle.colorMin);
		setUniformRGB(uniform_genColorRange, particle.colorRange);
	}
}
