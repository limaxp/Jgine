#version 330


layout(points) in;
layout(points) out;
layout(max_vertices = 40) out;

in vec3[] positionPass;
in vec3[] velocityPass;
in vec3[] colorPass;
in float[] lifeTimePass;
in float[] sizePass;
in int[] typePass;

out vec3 positionOut;
out vec3 velocityOut;
out vec3 colorOut;
out float lifeTimeOut;
out float sizeOut;
out int typeOut;

uniform float timePassed;
uniform vec3 genPosition;
uniform vec3 genPositionRange;
uniform int numToGenerate;
uniform vec3 genGravityVector;
uniform vec3 randomSeed;
uniform vec3 genVelocityMin;
uniform vec3 genVelocityRange;
uniform float genLiveMin;
uniform float genLiveRange;
uniform float genSizeMin;
uniform float genSizeRange;
uniform vec3 genColorMin;
uniform vec3 genColorRange;

vec3 localSeed;

float randZeroOne() {
    uint n = floatBitsToUint(localSeed.y * 214013.0 + localSeed.x * 2531011.0 + localSeed.z * 141251.0);
    n = n * (n * n * 15731u + 789221u);
    n = (n >> 9u) | 0x3F800000u;
 
    float fRes =  2.0 - uintBitsToFloat(n);
    localSeed = vec3(localSeed.x + 147158.0 * fRes, localSeed.y*fRes  + 415161.0 * fRes, localSeed.z + 324154.0*fRes);
    return fRes;
}

void main() {
	positionOut = positionPass[0];
	velocityOut = velocityPass[0];
	colorOut = colorPass[0];
	lifeTimeOut = lifeTimePass[0];
	sizeOut = sizePass[0];
	typeOut = typePass[0];
		
	if(typeOut == 0) {
		EmitVertex();
    	EndPrimitive();
		localSeed = randomSeed;
	
		for(int i = 0; i < numToGenerate; i++) {
			positionOut = genPosition - genPositionRange + vec3(genPositionRange.x * 2 * randZeroOne(), genPositionRange.y * 2 * randZeroOne(), genPositionRange.z * 2 * randZeroOne());
			velocityOut = genVelocityMin + vec3(genVelocityRange.x * randZeroOne(), genVelocityRange.y * randZeroOne(), genVelocityRange.z * randZeroOne());
			colorOut = genColorMin + vec3(genColorRange.x * randZeroOne(), genColorRange.y * randZeroOne(), genColorRange.z * randZeroOne());
			lifeTimeOut = genLiveMin + genLiveRange * randZeroOne();
			sizeOut = genSizeMin + genSizeRange * randZeroOne();
			typeOut = 1;
			EmitVertex();
			EndPrimitive();
		}
	}
	else if(lifeTimeOut > 0.0) {
		positionOut += velocityOut * timePassed;
		velocityOut += genGravityVector * timePassed;
		lifeTimeOut -= timePassed;
		EmitVertex();
		EndPrimitive(); 
	}
}