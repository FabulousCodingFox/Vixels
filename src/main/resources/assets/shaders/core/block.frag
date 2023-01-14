#version 330 core
out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D atlas;

void main()
{
    FragColor = texture(atlas, vec2(TexCoord.x, TexCoord.y));
    //FragColor = vec4(vec3(1 * ao), 1);
}