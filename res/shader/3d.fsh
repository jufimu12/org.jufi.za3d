varying vec4 diffuse,ambient;
varying vec3 normal,halfVector;
uniform sampler2D tex0;

void main()
{
	vec3 n,halfV;
	float NdotL,NdotHV;
	vec4 color = ambient;
	
	n = normalize(normal);
	
	// compute the dot product between normal and ldir
	NdotL = max(dot(n,gl_LightSource[0].position),0.0);
	
	if (NdotL > 0.0) {
		color += diffuse * NdotL;
		halfV = normalize(halfVector);
		NdotHV = max(dot(n,halfV),0.0);
		color += gl_FrontMaterial.specular *
				gl_LightSource[0].specular *
				pow(NdotHV, gl_FrontMaterial.shininess);
	}
	
	gl_FragColor = color * texture2D(tex0, gl_TexCoord[0].st);
}
