#version 330


const int MAX_POINT_LIGHTS = 8;

struct BaseLight {
	vec3 color;
	float intensity;
};

struct Attenuation {
	float constant;
	float linear;
	float exponent;
};

struct PointLight {
	BaseLight base;
	Attenuation atten;
	vec3 pos; 
	float range;
};

in vec2 textCoord;
in vec3 position;

uniform vec4 baseColor;
uniform sampler2D uTexture;
uniform vec3 ambientLight;
uniform int pointLightSize;
uniform PointLight pointLights[MAX_POINT_LIGHTS];

out vec4 fragColor;

vec4 calcLight(BaseLight base) {
	return vec4(base.color, 1.0) * base.intensity;
}

vec4 calcPointLight(PointLight pointLight) {
	vec3 lightDirection = position - pointLight.pos;
	float distanceToPoint = length(lightDirection);
	
	if(distanceToPoint > pointLight.range) 
		return vec4(0.0, 0.0, 0.0, 0.0);

	vec4 color = calcLight(pointLight.base);
	
	float attenuation = pointLight.atten.constant 
		+ pointLight.atten.linear * distanceToPoint
		+ pointLight.atten.exponent * distanceToPoint * distanceToPoint
		+ 0.0001;
	
	return color / attenuation;
}

void main() {
	vec4 totalLight = vec4(ambientLight, 1.0);
	for(int i = 0; i < pointLightSize; i++) 
		totalLight += calcPointLight(pointLights[i]);

	fragColor = texture(uTexture, textCoord) * baseColor * totalLight;
}