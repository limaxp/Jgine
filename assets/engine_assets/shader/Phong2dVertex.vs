#version 330
#extension GL_ARB_explicit_attrib_location : enable

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 textCoordinates;

out vec2 textCoord;
out vec3 position;

uniform mat4 transformProjected;
uniform mat4 transform;
uniform vec4 textureOffsets;

void main() {
	position = (transform * vec4(pos, 1.0)).xyz;
	gl_Position = transformProjected * vec4(pos, 1.0);
	
	float textX = textureOffsets.x + textCoordinates.x * textureOffsets.z;
    float textY = textureOffsets.y + textCoordinates.y * textureOffsets.w;
    textCoord = vec2(textX, textY);
}