#version 330
#extension GL_ARB_explicit_attrib_location : enable

layout (location = 0) in vec2 pos;
layout (location = 1) in vec2 textCoordinates;
layout (location = 2) in vec4 pos2;

out vec2 textCoord;

uniform mat4 transformProjected;
uniform vec4 textureOffsets;


void main() {
	vec3 cameraRight_worldspace = vec3(transformProjected[0][0], transformProjected[1][0], transformProjected[2][0]);
	vec3 cameraUp_worldspace = vec3(transformProjected[0][1], transformProjected[1][1], transformProjected[2][1]);

	vec3 billboardPos = cameraRight_worldspace * pos.x *  pos2.w + cameraUp_worldspace * pos.y *  pos2.w;
	
	gl_Position = transformProjected * (vec4(billboardPos, 1)  + vec4(pos2.xyz, 0));
	
	float textX = textureOffsets.x + textCoordinates.x * textureOffsets.z;
    float textY = textureOffsets.y + textCoordinates.y * textureOffsets.w;
    textCoord = vec2(textX, textY);
}