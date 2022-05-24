#version 330


in vec2 textCoord;

out vec4 fragColor;

uniform vec3 baseColor;
uniform sampler2D uTexture;

void main() {
    float thickness = 0.5;
    float fade = 0.005;

  	// -1 -> 1 local space, adjusted for aspect ratio
    vec2 uv = textCoord / iResolution.xy * 2.0 - 1.0;
    float aspect = iResolution.x / iResolution.y;
    uv.x *= aspect;
    
    // Calculate distance and fill circle with white
    float distance = 1.0 - length(uv);
    vec3 color = vec3(smoothstep(0.0, fade, distance));
    color *= vec3(smoothstep(thickness + fade, thickness, distance));

    // Set output color
    fragColor = vec4(color, 1.0);
    fragColor.rgb *= color;
}