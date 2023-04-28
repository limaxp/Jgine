#version 330
#extension GL_ARB_explicit_attrib_location : enable

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 textCoordinates;

out vec2 textCoord;

uniform mat4 transformProjected;
uniform float time;
uniform float shakeStrength;

void main() {
	gl_Position = transformProjected * vec4(pos, 1.0);
    textCoord = vec2(textCoordinates.x, -textCoordinates.y);
    
    if (shakeStrength > 0.0) {
        float strength = 0.01;
        gl_Position.x += cos(time * 10) * shakeStrength;        
        gl_Position.y += cos(time * 15) * shakeStrength;        
    }
}