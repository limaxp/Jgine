#version 330
#extension GL_ARB_explicit_attrib_location : enable

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 textCoordinates;

out vec2 textCoord;

uniform mat4 transformProjected;

void main() {
	gl_Position = transformProjected * vec4(pos, 1.0);
    textCoord = vec2(textCoordinates.x, -textCoordinates.y);
}