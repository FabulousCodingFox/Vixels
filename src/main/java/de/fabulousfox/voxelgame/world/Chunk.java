package de.fabulousfox.voxelgame.world;

import de.fabulousfox.voxelgame.structures.BlockSide;
import de.fabulousfox.voxelgame.structures.BlockState;
import de.fabulousfox.voxelgame.structures.biomes.Biome;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class Chunk {
    public static final int CHUNK_SIZE = 20;
    public static final int CHUNK_HEIGHT = 255;
    public static final int CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_HEIGHT;

    private final BlockState[][][] blocks;
    private float[] mesh_blocks;
    private float[] mesh_water;
    private final int x;
    private final int z;
    private int VBO_blocks;
    private int VBO_water;
    private boolean isVBOGenerated;
    private boolean isTerrainGenerated;
    private Biome biome;

    public Chunk(int x, int z) {
        this.blocks = new BlockState[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
        this.x = x;
        this.z = z;
        this.isVBOGenerated = false;
        this.isTerrainGenerated = false;
        this.VBO_water = -1;
        this.VBO_blocks = -1;
    }
    public Biome getBiome() {
        return biome;
    }
    public void setBiome(Biome biome) {
        this.biome = biome;
    }
    public BlockState getBlock(int x, int y, int z) {
        if(x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE) return null;
        return blocks[x][y][z];
    }
    public void setBlock(int x, int y, int z, BlockState block) {
        if(x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE) return;
        blocks[x][y][z] = block;
    }
    public float[] getMesh_blocks() {
        return mesh_blocks;
    }
    public float[] getMesh_water() {
        return mesh_water;
    }

    public void setMesh_blocks(float[] mesh_blocks) {this.mesh_blocks = mesh_blocks;}
    public void setMesh_water(float[] mesh_water) {this.mesh_water = mesh_water;}

    public int getX() {
        return x;
    }
    public int getZ() {
        return z;
    }
    public void generateTerrain(int seed) {
        isTerrainGenerated = true;
        TerrainGenerator.generateChunk(this, seed);
    }
    private static BlockState getBlockAt(int x, int y, int z, Chunk chunk, Chunk c0, Chunk c1, Chunk c2, Chunk c3, Chunk c4, Chunk c5, Chunk c6, Chunk c7) {
        //  x
        //
        // 0 1 2
        // 3   4   z
        // 5 6 7
        if(y<=0) return new BlockState(BlockState.STONE);
        if(y>=Chunk.CHUNK_HEIGHT) return new BlockState(BlockState.AIR);

        if(x >= 0 && x < Chunk.CHUNK_SIZE && z >= 0 && z < Chunk.CHUNK_SIZE)
            return chunk.getBlock(x, y, z); // If in Chunk "chunk": 0>=x<Chunk.CHUNK_SIZE && 0>=z<Chunk.CHUNK_SIZE

        if(x < 0 && z < 0)
            return c0.getBlock(x+Chunk.CHUNK_SIZE, y, z+Chunk.CHUNK_SIZE); // If in CHunk "c0": x<0 && z<0

        if(x >= 0 && x < Chunk.CHUNK_SIZE && z < 0)
            return c1.getBlock(x, y, z+Chunk.CHUNK_SIZE); // If in CHunk "c1": 0>=x<Chunk.CHUNK_SIZE && z<0

        if(x >= Chunk.CHUNK_SIZE && z < 0)
            return c2.getBlock(x-Chunk.CHUNK_SIZE, y, z+Chunk.CHUNK_SIZE); // If in CHunk "c2": x>=Chunk.CHUNK_SIZE && z<0

        if(x < 0 && z >= 0 && z < Chunk.CHUNK_SIZE)
            return c3.getBlock(x+Chunk.CHUNK_SIZE, y, z); // If in CHunk "c3": x<0 && 0>=z<Chunk.CHUNK_SIZE

        if(x >= Chunk.CHUNK_SIZE && z >= 0 && z < Chunk.CHUNK_SIZE)
            return c4.getBlock(x-Chunk.CHUNK_SIZE, y, z); // If in CHunk "c4": x>=Chunk.CHUNK_SIZE && 0>=z<Chunk.CHUNK_SIZE

        if(x < 0 && z >= Chunk.CHUNK_SIZE)
            return c5.getBlock(x+Chunk.CHUNK_SIZE, y, z-Chunk.CHUNK_SIZE); // If in CHunk "c5"

        if(x >= 0 && x < Chunk.CHUNK_SIZE && z >= Chunk.CHUNK_SIZE)
            return c6.getBlock(x, y, z-Chunk.CHUNK_SIZE); // If in CHunk "c6"

        if(x >= Chunk.CHUNK_SIZE && z >= Chunk.CHUNK_SIZE)
            return c7.getBlock(x-Chunk.CHUNK_SIZE, y, z-Chunk.CHUNK_SIZE); // If in CHunk "c7"

        throw new IllegalStateException("Should not be here");
    }

    public void generateMesh(Chunk c0, Chunk c1, Chunk c2, Chunk c3, Chunk c4, Chunk c5, Chunk c6, Chunk c7) {
        final float atlasWidth = 4f;
        final float atlasHeight = 4f;

        final float texWidth = 1f / atlasWidth;
        final float texHeight = 1f / atlasHeight;

        ArrayList<Float> data_blocks = new ArrayList<>();
        ArrayList<Float> data_water = new ArrayList<>();

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                int xp = x + this.getX() * Chunk.CHUNK_SIZE;
                int zp = z + this.getZ() * Chunk.CHUNK_SIZE;
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                    BlockState block = this.getBlock(x, y, z);
                    if(block.getBlockType() == BlockState.WATER){
                        BlockState up = getBlockAt(x, y + 1, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (up.getTransparent() && up.getBlockType() != block.getBlockType()) {
                            float texX = block.getTexPositions(BlockSide.UP)[0] / atlasWidth;
                            float texY = block.getTexPositions(BlockSide.UP)[1] / atlasHeight;
                            float texX2 = texX + texWidth;
                            float texY2 = texY + texHeight;
                            data_water.addAll(List.of(
                                    -0.5f + xp, 0.5f + y - 0.2f,  0.5f + zp, texX,  texY2,
                                    -0.5f + xp, 0.5f + y - 0.2f, -0.5f + zp, texX,  texY,
                                     0.5f + xp, 0.5f + y - 0.2f, -0.5f + zp, texX2, texY,
                                     0.5f + xp, 0.5f + y - 0.2f, -0.5f + zp, texX2, texY,
                                     0.5f + xp, 0.5f + y - 0.2f,  0.5f + zp, texX2, texY2,
                                    -0.5f + xp, 0.5f + y - 0.2f,  0.5f + zp, texX,  texY2
                            ));
                        }
                    }
                    else if (block.getBlockType() != BlockState.AIR) {
                        BlockState up = getBlockAt(x, y + 1, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (up.getTransparent() && up.getBlockType() != block.getBlockType()) {
                            float texX = block.getTexPositions(BlockSide.UP)[0] / atlasWidth;
                            float texY = block.getTexPositions(BlockSide.UP)[1] / atlasHeight;
                            float texX2 = texX + texWidth;
                            float texY2 = texY + texHeight;
                            data_blocks.addAll(List.of(
                                -0.5f + xp, 0.5f + y,  0.5f + zp, texX,  texY2,
                                -0.5f + xp, 0.5f + y, -0.5f + zp, texX,  texY,
                                 0.5f + xp, 0.5f + y, -0.5f + zp, texX2, texY,
                                 0.5f + xp, 0.5f + y, -0.5f + zp, texX2, texY,
                                 0.5f + xp, 0.5f + y,  0.5f + zp, texX2, texY2,
                                -0.5f + xp, 0.5f + y,  0.5f + zp, texX,  texY2
                            ));
                        }

                        BlockState left = getBlockAt(x - 1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (left.getTransparent() && left.getBlockType() != block.getBlockType()) {
                            float texX = block.getTexPositions(BlockSide.EAST)[0] / atlasWidth;
                            float texY = block.getTexPositions(BlockSide.EAST)[1] / atlasHeight;
                            float texX2 = texX + texWidth;
                            float texY2 = texY + texHeight;
                            data_blocks.addAll(List.of(
                                -0.5f + xp, -0.5f + y,  0.5f + zp, texX2, texY2,
                                -0.5f + xp,  0.5f + y, -0.5f + zp, texX,  texY,
                                -0.5f + xp,  0.5f + y,  0.5f + zp, texX2, texY,
                                -0.5f + xp,  0.5f + y, -0.5f + zp, texX,  texY,
                                -0.5f + xp, -0.5f + y,  0.5f + zp, texX2, texY2,
                                -0.5f + xp, -0.5f + y, -0.5f + zp, texX,  texY2
                            ));

                        }

                        BlockState right = getBlockAt(x + 1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (right.getTransparent() && right.getBlockType() != block.getBlockType()) {
                            float texX = block.getTexPositions(BlockSide.WEST)[0] / atlasWidth;
                            float texY = block.getTexPositions(BlockSide.WEST)[1] / atlasHeight;
                            float texX2 = texX + texWidth;
                            float texY2 = texY + texHeight;
                            data_blocks.addAll(List.of(
                                0.5f + xp,  0.5f + y,  0.5f + zp, texX,  texY,
                                0.5f + xp,  0.5f + y, -0.5f + zp, texX2, texY,
                                0.5f + xp, -0.5f + y, -0.5f + zp, texX2, texY2,

                                0.5f + xp, -0.5f + y, -0.5f + zp, texX2, texY2,
                                0.5f + xp, -0.5f + y,  0.5f + zp, texX,  texY2,
                                0.5f + xp,  0.5f + y,  0.5f + zp, texX,  texY
                            ));
                        }

                        BlockState front = getBlockAt(x, y, z + 1, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (front.getTransparent() && front.getBlockType() != block.getBlockType()) {
                            float texX = block.getTexPositions(BlockSide.NORTH)[0] / atlasWidth;
                            float texY = block.getTexPositions(BlockSide.NORTH)[1] / atlasHeight;
                            float texX2 = texX + texWidth;
                            float texY2 = texY + texHeight;
                            data_blocks.addAll(List.of(
                                    -0.5f + xp, -0.5f + y, 0.5f + zp, texX,  texY2,
                                     0.5f + xp,  0.5f + y, 0.5f + zp, texX2, texY,
                                     0.5f + xp, -0.5f + y, 0.5f + zp, texX2, texY2,

                                     0.5f + xp,  0.5f + y, 0.5f + zp, texX2, texY,
                                    -0.5f + xp, -0.5f + y, 0.5f + zp, texX,  texY2,
                                    -0.5f + xp,  0.5f + y, 0.5f + zp, texX,  texY
                            ));
                        }

                        BlockState back = getBlockAt(x, y, z - 1, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (back.getTransparent() && back.getBlockType() != block.getBlockType()) {
                            float texX = block.getTexPositions(BlockSide.SOUTH)[0] / atlasWidth;
                            float texY = block.getTexPositions(BlockSide.SOUTH)[1] / atlasHeight;
                            float texX2 = texX + texWidth;
                            float texY2 = texY + texHeight;
                            data_blocks.addAll(List.of(
                                    -0.5f + xp, -0.5f + y, -0.5f + zp, texX2, texY2,
                                     0.5f + xp, -0.5f + y, -0.5f + zp, texX,  texY2,
                                     0.5f + xp,  0.5f + y, -0.5f + zp, texX,  texY,

                                     0.5f + xp,  0.5f + y, -0.5f + zp, texX,  texY,
                                    -0.5f + xp,  0.5f + y, -0.5f + zp, texX2, texY,
                                    -0.5f + xp, -0.5f + y, -0.5f + zp, texX2, texY2
                            ));
                        }
                    }
                }
            }
        }

        float[] dataArray_blocks = new float[data_blocks.size()];
        for (int i = 0; i < data_blocks.size(); i++) dataArray_blocks[i] = data_blocks.get(i);
        this.setMesh_blocks(dataArray_blocks);

        float[] dataArray_water = new float[data_water.size()];
        for (int i = 0; i < data_water.size(); i++) dataArray_water[i] = data_water.get(i);
        this.setMesh_water(dataArray_water);
    }

    public void generateVBO_blocks() {
        VBO_blocks = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO_blocks);
        glBufferData(GL_ARRAY_BUFFER, mesh_blocks, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        isVBOGenerated = true;
    }

    public void generateVBO_water() {
        VBO_water = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO_water);
        glBufferData(GL_ARRAY_BUFFER, mesh_water, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        isVBOGenerated = true;
    }

    public int getVBO_blocks() {
        return VBO_blocks;
    }

    public int getVBO_water() {
        return VBO_water;
    }

    public void destroy() {
        if(VBO_blocks != -1) glDeleteBuffers(VBO_blocks);
        if(VBO_water != -1) glDeleteBuffers(VBO_water);
    }

    public boolean isVBOGenerated() {
        return isVBOGenerated;
    }

    public boolean isTerrainGenerated(){
        return isTerrainGenerated;
    }
}
