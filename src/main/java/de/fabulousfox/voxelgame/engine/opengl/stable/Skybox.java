package de.fabulousfox.voxelgame.engine.opengl.stable;

import static org.lwjgl.opengl.GL46.*;

public class Skybox {
    private final int VBO;

    private static final float[] colorTop = new float[] {0.215f,0.776f,1f};
    private static final float[] colorBottom = new float[] {0.549f,1f,0.784f};

    private static final float[] VERTICES = {
            -1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],
            -1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],
            -1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],

            -1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            -1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            -1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],
            -1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],
            -1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            -1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],

            1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],

            -1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            -1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            -1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],

            -1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            -1.0f,  1.0f,  1.0f, colorTop[0], colorTop[1], colorTop[2],
            -1.0f,  1.0f, -1.0f, colorTop[0], colorTop[1], colorTop[2],

            -1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            -1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f, -1.0f, -1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            -1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2],
            1.0f, -1.0f,  1.0f, colorBottom[0], colorBottom[1], colorBottom[2]
    };



    public Skybox() {
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, VERTICES, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getVBO(){
        return VBO;
    }
}
