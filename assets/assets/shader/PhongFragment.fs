#version 330


const int MAX_POINT_LIGHTS = 8;

struct BaseLight {
	vec3 color;
	float intensity;
};

struct DirectionalLight {
	BaseLight base;
	vec3 direction;
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
in vec3 normal;
in vec3 position;

uniform vec4 baseColor;
uniform sampler2D uTexture;
uniform vec3 ambientLight;
uniform int pointLightSize;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirectionalLight directionalLight;
uniform vec3 camPos;
uniform float specularIntensity;
uniform float specularPower;

out vec4 fragColor;


vec4 calcLight(BaseLight base, vec3 direction, vec3 normal) {
	float diffuseFactor = dot(normal, -direction);
	
	vec4 diffuseColor = vec4(0,0,0,0);
	vec4 specularColor = vec4(0,0,0,0);
	
	if(diffuseFactor > 0) {
		diffuseColor = vec4(base.color, 1.0) * base.intensity * diffuseFactor;
		
		vec3 directionToCam = normalize(camPos - position);
		vec3 reflectDirection = normalize(reflect(direction, normal));
		
		float specularFactor = dot(directionToCam, reflectDirection);
		specularFactor = pow(specularFactor, specularPower);
		
		if(specularFactor > 0) {
			specularColor = vec4(base.color, 1.0) * specularIntensity * specularFactor;
		}
	}
	
	return diffuseColor + specularColor;
} 

vec4 calcDirectionalLight(DirectionalLight dirLight, vec3 normal) {
	return calcLight(dirLight.base, -dirLight.direction, normal);
}

vec4 calcPointLight(PointLight pointLight, vec3 normal) {
	vec3 lightDirection = position - pointLight.pos;
	float distanceToPoint = length(lightDirection);
	
	if(distanceToPoint > pointLight.range) 
		return vec4(0.0, 0.0, 0.0, 0.0);
	
	lightDirection = normalize(lightDirection);
	
	vec4 color = calcLight(pointLight.base, lightDirection, normal);
	
	float attenuation = pointLight.atten.constant 
		+ pointLight.atten.linear * distanceToPoint
		+ pointLight.atten.exponent * distanceToPoint * distanceToPoint
		+ 0.0001;
		
	return color / attenuation;
}

void main() {
 	vec4 texColor = texture(uTexture, textCoord) * baseColor;
    if(texColor.a < 0.01)
        discard;

	vec4 totalLight = vec4(ambientLight, 1.0);
	
	vec3 normal = normalize(normal);
	totalLight += calcDirectionalLight(directionalLight, normal);
	
	for(int i = 0; i < pointLightSize; i++) 
		totalLight += calcPointLight(pointLights[i], normal);
	
	fragColor = texColor * totalLight;
}