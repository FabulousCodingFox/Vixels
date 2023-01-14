#version 330 core
layout (location = 0) in vec3 inXYZ;
layout (location = 1) in vec2 inUV;

out vec2 uv;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    gl_Position = projection * view * model * vec4(inXYZ, 1.0);
    uv = inUV;
}