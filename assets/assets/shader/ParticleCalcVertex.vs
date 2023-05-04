#version 330


layout (location = 0) in vec3 position;
layout (location = 1) in int type;

out vec3 positionPass;
out int typePass;

void main() {
    positionPass = position;
    typePass = type;
}