#version 330


layout (location = 0) in vec3 position;
layout (location = 1) in int type;

out int typePass;

void main() {
	gl_Position = vec4(position, 1.0);
	typePass = type;
}