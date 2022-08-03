#version 330


in vec2 textCoord;

out vec4 fragColor;

uniform vec4 baseColor;
uniform sampler2D uTexture;

float circle(vec2 st, float radius){
    vec2 dist = st - vec2(0.5);
	return 1.0 - smoothstep(radius - (radius * 0.01),
                         radius + (radius * 0.01),
                         dot(dist, dist) * 4.0);
}

void main() {
    float radius = 0.95;
    float thickness = 0.1;
    float fade = 0.005;
    vec2 resolution = vec2(1 , 1);
   
    vec2 uv = textCoord / resolution * 2.0 - 1.0;
    float distance = radius - length(uv);
    float t = smoothstep(0.0, fade, distance);
    t *= smoothstep(thickness + fade, thickness, distance);
    
    //vec2 uv = textCoord / resolution;
	//float t = circle(uv, radius);

    fragColor =  texture(uTexture, textCoord) * vec4(baseColor.rgb, baseColor.a * t);
}