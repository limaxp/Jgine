#version 330

const int MAX_KERNELS = 4;

in vec2 textCoord;

uniform vec4 baseColor;
uniform sampler2D uTexture;
uniform vec2 offsets[9];
uniform int kernelSize;
uniform mat3 kernel[MAX_KERNELS];

out vec4 fragColor;

void invert() {
	fragColor = vec4(vec3(1.0 - texture(uTexture, textCoord) * baseColor), 1.0);
}

void grayScale(){
    fragColor = texture(uTexture, textCoord) * baseColor;
    float average = (fragColor.r + fragColor.g + fragColor.b) / 3.0;
    fragColor = vec4(average, average, average, 1.0);
}   

void grayScaleAdjusted() {
    fragColor = texture(uTexture, textCoord) * baseColor;
    float average = 0.2126 * fragColor.r + 0.7152 * fragColor.g + 0.0722 * fragColor.b;
    fragColor = vec4(average, average, average, 1.0);
}   

void main() {
    if(kernelSize > 0) {
     	vec3 sampleTex[9];
	    for(int i = 0; i < 9; i++) 
	        sampleTex[i] = vec3(texture(uTexture, textCoord.st + offsets[i]));
	        
	   	fragColor = vec4(0.0f, 0.0f, 0.0f, 1.0f);
	   	for(int i = 0; i < kernelSize; i++) {
	   		mat3 currentKernel = kernel[i];
	   		for(int i = 0; i < 3; i++)
	       		for(int j = 0; j < 3; j++)
	        		fragColor += vec4(sampleTex[i + j] * currentKernel[i][j], 0.0f);
	   	}
    }
   	else {
   		fragColor = texture(uTexture, textCoord) * baseColor;
   	}
}