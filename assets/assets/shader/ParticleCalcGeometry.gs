#version 330


layout(points) in;
layout(points) out;
layout(max_vertices = 40) out;

in vec3[] positionPass;
in int[] typePass;

out vec3 positionOut;
out int typeOut;

uniform vec3 genPosition;
uniform int numToGenerate;

void main() {
	positionOut = positionPass[0];
	typeOut = typePass[0];
		
	if(typeOut == 0) {
		EmitVertex();
    	EndPrimitive();
	
		for(int i = 0; i < numToGenerate; i++) {
			positionOut = genPosition + vec3(0,i,0);
			typeOut = 1;
			EmitVertex();
			EndPrimitive();
		}
	}
}