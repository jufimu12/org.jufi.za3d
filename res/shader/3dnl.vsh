varying vec4 depth;

void main(void)
{
	gl_FrontColor = gl_Color;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_TexCoord[0]  = gl_MultiTexCoord0;
	depth = gl_Position;
}
