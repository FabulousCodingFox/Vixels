package de.fabulousfox.voxelgame.engine;

import de.fabulousfox.voxelgame.engine.AssetLoader;
import org.joml.Matrix4f;

import java.io.IOException;

import static org.lwjgl.opengl.GL46.*;

public class Shader {
    int ID;

    public Shader(String vertexPath, String fragmentPath) {
        // 1. retrieve the vertex/fragment source code from filePath
        String vertexCode = "";
        String fragmentCode = "";
        try {
            vertexCode = AssetLoader.readTextFile(vertexPath);
            fragmentCode = AssetLoader.readTextFile(fragmentPath);
        }catch (IOException e) {
            e.printStackTrace();
        }

        // 2. compile shaders
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexCode);
        glCompileShader(vertexShader);
        if(glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) throw new RuntimeException("Vertex shader failed to compile: "+vertexPath + " Reason: " +  glGetShaderInfoLog(vertexShader));


        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentCode);
        glCompileShader(fragmentShader);
        if(glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) throw new RuntimeException("Fragment shader failed to compile: "+fragmentPath + " Reason: " +  glGetShaderInfoLog(fragmentShader));

        // 3. create shader program
        ID = glCreateProgram();
        glAttachShader(ID, vertexShader);
        glAttachShader(ID, fragmentShader);
        glLinkProgram(ID);
        if(glGetProgrami(ID, GL_LINK_STATUS) == GL_FALSE) throw new RuntimeException("Shader program failed to link: "+vertexPath+" and "+fragmentPath);

        // 4. clean up
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use() {
        glUseProgram(ID);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(ID, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(ID, name), value ? 1 : 0);
    }

    public void setMatrix4f(String name, Matrix4f value) {
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, value.get(new float[16]));
    }


}
