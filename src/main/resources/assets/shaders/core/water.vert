#version 330 core
layout (location = 0) in vec3 inXYZ;
layout (location = 1) in vec2 inUV;

out vec2 uv;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float Time;

vec2 wavedx(vec2 position, vec2 direction, float speed, float frequency, float timeshift) {
    float x = dot(direction, position) * frequency + timeshift * speed;
    float wave = exp(sin(x) - 1.0);
    float dx = wave * cos(x);
    return vec2(wave, -dx);
}

float getwaves(vec2 position, int iterations){
    float iter = 0.0;
    float phase = 6.0;
    float speed = 2.0;
    float weight = 1.0;
    float w = 0.0;
    float ws = 0.0;
    for(int i=0;i<iterations;i++){
        vec2 p = vec2(sin(iter), cos(iter));
        vec2 res = wavedx(position, p, speed, phase, Time);
        position += p * res.y * weight * 0.048;
        w += res.x * weight;
        iter += 12.0;
        ws += weight;
        weight = mix(weight, 0.0, 0.2);
        phase *= 1.18;
        speed *= 1.07;
    }
    return w / ws;
}

void main() {
    gl_Position = projection * view * model * vec4(inXYZ, 1.) + vec4(0, getwaves(vec2(inXYZ.x, inXYZ.z), 48) - .5, 0, 0);
    uv = inUV;
}