#version 330 core
out vec4 FragColor;

in vec2 uv;

uniform sampler2D atlas;

void main()
{
    vec4 texColor = texture(atlas, vec2(uv.x, uv.y));
    if(texColor.a < 0.1) discard;
    FragColor = texColor;
}