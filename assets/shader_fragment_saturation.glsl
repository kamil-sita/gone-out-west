#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

uniform float saturation;

void main() {
    vec4 color = (v_color * texture2D(u_texture, v_texCoords)).rgba;
    float gray = (color.r + color.g + color.b) / 3.0;

    float r = color.r * saturation + gray * (1-saturation);
    float g = color.g * saturation + gray * (1-saturation);
    float b = color.b * saturation + gray * (1-saturation);

    gl_FragColor = vec4(r, g, b, color.a);
}