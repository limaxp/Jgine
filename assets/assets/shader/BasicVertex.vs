#version 330
#extension GL_ARB_explicit_attrib_location : enable


layout (location = 0) in vec3 pos;

uniform mat4 transformProjected;

void main() {
	gl_Position = transformProjected * vec4(pos, 1.0);
}