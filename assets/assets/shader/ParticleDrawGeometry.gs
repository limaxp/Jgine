#version 330


layout(points) in;
layout(triangle_strip) out;
layout(max_vertices = 4) out;

in vec3[] colorPass;
in float[] liveTimePass;
in float[] sizePass;
in int[] typePass;

out vec2 textCoord;
out vec4 colorPart;

uniform mat4 transformProjected;
uniform vec4 textureOffsets;

void main() {
	if(typePass[0] == 0) 
		return;
		
	colorPart = vec4(colorPass[0], liveTimePass[0]);
		
	float textXMin = textureOffsets.x + 0.0 * textureOffsets.z;
	float textXMax = textureOffsets.x + 1.0 * textureOffsets.z;
    float textYMin = textureOffsets.y + 0.0 * textureOffsets.w;
    float textYMax = textureOffsets.y + 1.0 * textureOffsets.w;
    
    vec3 cameraRight_worldspace = vec3(transformProjected[0][0], transformProjected[1][0], transformProjected[2][0]);
	vec3 cameraUp_worldspace = vec3(transformProjected[0][1], transformProjected[1][1], transformProjected[2][1]);
	vec3 pos = gl_in[0].gl_Position.xyz;
	float size = sizePass[0];
	
	vec3 billboardPos = cameraRight_worldspace * (pos.x - size) + cameraUp_worldspace * (pos.y - size);
	textCoord = vec2(textXMax, textYMax);
	gl_Position = transformProjected * vec4(billboardPos, 1.0);
	EmitVertex();
	
	billboardPos = cameraRight_worldspace * (pos.x - size) + cameraUp_worldspace * (pos.y + size);
	textCoord = vec2(textXMax, textYMin);
	gl_Position = transformProjected * vec4(billboardPos, 1.0);
	EmitVertex();
	
	billboardPos = cameraRight_worldspace * (pos.x + size) + cameraUp_worldspace * (pos.y - size);
	textCoord = vec2(textXMin, textYMax);
	gl_Position = transformProjected * vec4(billboardPos, 1.0);
	EmitVertex();
	
	billboardPos = cameraRight_worldspace * (pos.x + size) + cameraUp_worldspace * (pos.y + size);
	textCoord = vec2(textXMin, textYMin);
	gl_Position = transformProjected * vec4(billboardPos, 1.0);
	EmitVertex();
	
	EndPrimitive();
}