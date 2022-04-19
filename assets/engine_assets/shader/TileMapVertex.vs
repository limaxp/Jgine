#version 330
#extension GL_ARB_explicit_attrib_location : enable

layout (location = 0) in vec2 pos;
layout (location = 1) in vec2 textCoordinates;
layout (location = 2) in vec2 tilePos;
layout (location = 3) in vec2 tileText;
layout (location = 4) in vec2 tileData;

out vec2 textCoord;

uniform mat4 transformProjected;
uniform int textureColums;
uniform int textureRows;


vec2 rotateUV(vec2 uv, float rotation)
{
    float mid = 0.5;
    float cosAngle = cos(rotation);
    float sinAngle = sin(rotation);
    return vec2(
        cosAngle * (uv.x - mid) + sinAngle * (uv.y - mid) + mid,
        cosAngle * (uv.y - mid) - sinAngle * (uv.x - mid) + mid
    );
}

vec2 rotateUV(vec2 uv, float rotation, vec2 mid)
{
    float cosAngle = cos(rotation);
    float sinAngle = sin(rotation);
    return vec2(
        cosAngle * (uv.x - mid.x) + sinAngle * (uv.y - mid.y) + mid.x,
        cosAngle * (uv.y - mid.y) - sinAngle * (uv.x - mid.x) + mid.y
    );
}

vec2 rotateUV(vec2 uv, float rotation, float mid)
{
    float cosAngle = cos(rotation);
    float sinAngle = sin(rotation);
    return vec2(
        cosAngle * (uv.x - mid) + sinAngle * (uv.y - mid) + mid,
        cosAngle * (uv.y - mid) - sinAngle * (uv.x - mid) + mid
    );
}

void main() {	
	gl_Position = transformProjected * vec4(pos.x + tilePos.x, pos.y - tilePos.y, 0, 1);
	
	float rotation = tileData.x;
	vec2 uv;
	if(rotation == 1) 
		uv = rotateUV(textCoordinates, 1.5707963268);
	else if(rotation == 2) 
		uv = rotateUV(textCoordinates, 3.1415926536);
	else if(rotation == 3) 
		uv = rotateUV(textCoordinates, 4.7123889804);
	else 
		uv = textCoordinates;
	
	float textX;
    if(tileData.y == 1.0f) 
  	  	textX = -uv.x / textureColums + (tileText.x + 1) / textureColums;
    else 
    	textX = uv.x / textureColums + tileText.x / textureColums;
    float textY = uv.y / textureRows + tileText.y / textureColums;
    textCoord = vec2(textX, textY);
}