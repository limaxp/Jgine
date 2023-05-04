#version 330

in vec2 textCoord;

out vec4 fragColor;

uniform vec4 baseColor;
uniform sampler2D uTexture;

void main() {
	vec4 texColor = texture(uTexture, textCoord) * baseColor;
    if(texColor.a < 0.01)
        discard;

	fragColor = texColor;
}