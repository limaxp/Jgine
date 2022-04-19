#version 330
#extension GL_ARB_explicit_attrib_location : enable

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 textCoordinates;

out vec2 textCoord;

uniform mat4 transformProjected;
uniform vec2 textureOffsets;
uniform int textureColums;
uniform int textureRows;

void main() {
	gl_Position = transformProjected * vec4(pos, 1.0);
	
	float textX = (textCoordinates.x / textureColums + textureOffsets.x);
    float textY = (textCoordinates.y / textureRows + textureOffsets.y);
    textCoord = vec2(textX, textY);
}