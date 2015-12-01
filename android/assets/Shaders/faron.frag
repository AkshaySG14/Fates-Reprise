varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;

void main() {
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
	vec4 nColor = vec4(color.r * 0.7, color.g * 0.9, color.b * 0.7, color.a);
	gl_FragColor = nColor;
}
