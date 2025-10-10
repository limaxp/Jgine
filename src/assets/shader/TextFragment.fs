#version 330


in vec2 textCoord;

uniform vec4 baseColor;
uniform sampler2D uTexture;

out vec4 fragColor;

void main() {
	vec4 texColor = texture(uTexture, textCoord) * baseColor;
    if(texColor.a < 0.01)
        discard;

	fragColor = texColor;
}