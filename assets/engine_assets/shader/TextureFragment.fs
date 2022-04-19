#version 330


in vec2 textCoord;

uniform vec4 color;
uniform sampler2D uTexture;

out vec4 fragColor;

void main() {
	fragColor = texture(uTexture, textCoord) * color;
}