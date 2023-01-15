package de.fabulousfox.voxelgame.world;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

public class SubChunk {
    private int VBO_blocks, VBO_water;
    private boolean isVBOGenerated;
    private float[] mesh_blocks, mesh_water;
    private boolean isAir;

    public SubChunk(){
        this.VBO_blocks = -1;
        this.VBO_water = -1;
        this.isVBOGenerated = false;
        this.mesh_blocks = null;
        this.mesh_water = null;
        this.isAir = false;
    }

    public boolean isAir() {
        return isAir;
    }

    public void setAir(boolean air) {
        isAir = air;
    }

    public float[] getMesh_blocks() {
        return mesh_blocks;
    }

    public float[] getMesh_water() {
        return mesh_water;
    }

    public void setMesh_blocks(float[] mesh_blocks) {
        this.mesh_blocks = mesh_blocks;
    }

    public void setMesh_water(float[] mesh_water) {
        this.mesh_water = mesh_water;
    }

    public boolean isVBOGenerated() {
        return isVBOGenerated;
    }

    public int getVBO_blocks() {
        return VBO_blocks;
    }

    public int getVBO_water() {
        return VBO_water;
    }

    public void generateVBOs(){
        if(isAir) return;

        VBO_blocks = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO_blocks);
        glBufferData(GL_ARRAY_BUFFER, mesh_blocks, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        VBO_water = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO_water);
        glBufferData(GL_ARRAY_BUFFER, mesh_water, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        isVBOGenerated = true;
    }
}