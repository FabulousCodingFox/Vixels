package de.fabulousfox.voxelgame.structures;

import de.fabulousfox.voxelgame.engine.terrain.TerrainGenerator;
import de.fabulousfox.voxelgame.structures.biomes.Biome;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class Chunk {
    public static final int CHUNK_SIZE = 20;
    public static final int CHUNK_HEIGHT = 255;
    public static final int CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_HEIGHT;

    private final BlockState[][][] blocks;
    private float[] mesh;
    private final int x;
    private final int y;
    private int VBO;
    private boolean ready;
    private Chunk[] neighbors;
    private Biome biome;

    public Chunk(int x, int y) {
        this.blocks = new BlockState[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
        this.x = x;
        this.y = y;
        this.ready = false;
        this.neighbors = new Chunk[4];
    }
    public Biome getBiome() {
        return biome;
    }
    public void setBiome(Biome biome) {
        this.biome = biome;
    }
    public BlockState getBlock(int x, int y, int z) {
        //if(x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE) return null;
        return blocks[x][y][z];
    }
    public void setBlock(int x, int y, int z, BlockState block) {
        if(x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE) return;
        blocks[x][y][z] = block;
    }
    public float[] getMesh() {
        return mesh;
    }
    public void setMesh(float[] mesh) {this.mesh = mesh;}
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void generateTerrain() {
        TerrainGenerator.generateChunk(this);
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
    private static float getAOVal(boolean side1, boolean side2, boolean corner) {
        return switch ((side1?1:0) + (side2?1:0)  + (corner?1:0)) {
            case 3 -> 0.25f;
            case 2 -> 0.5f;
            case 1 -> 0.75f;
            default -> 1f;
        };
    }

    public void generateMesh(Chunk c0, Chunk c1, Chunk c2, Chunk c3, Chunk c4, Chunk c5, Chunk c6, Chunk c7) {
        final float atlasWidth = 2f;
        final float atlasHeight = 2f;

        final float texWidth = 1f / atlasWidth;
        final float texHeight = 1f / atlasHeight;

        ArrayList<Float> data = new ArrayList<>();
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                    BlockState block = this.getBlock(x, y, z);
                    if (block.getBlockType() != BlockState.AIR) {
                        int xp = x + this.getX() * Chunk.CHUNK_SIZE;
                        int zp = z + this.getY() * Chunk.CHUNK_SIZE;

                        float AO_MMM = (getAOVal(
                                !getBlockAt(x, y, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));
                        float AO_MMP = (getAOVal(
                                !getBlockAt(x, y, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));
                        float AO_MPM = (getAOVal(
                                !getBlockAt(x, y+1, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y+1, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y+1, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));
                        float AO_MPP = (getAOVal(
                                !getBlockAt(x, y+1, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y+1, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x-1, y+1, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));
                        float AO_PMM = (getAOVal(
                                !getBlockAt(x, y, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));
                        float AO_PMP = (getAOVal(
                                !getBlockAt(x, y, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));
                        float AO_PPM = (getAOVal(
                                !getBlockAt(x, y+1, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y+1, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y+1, z-1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));
                        float AO_PPP = (getAOVal(
                                !getBlockAt(x, y+1, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y+1, z, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent(),
                                !getBlockAt(x+1, y+1, z+1, this,c0,c1,c2,c3,c4,c5,c6,c7).getTransparent()));


                        BlockState up = getBlockAt(x, y + 1, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (up.getTransparent() && up.getBlockType() != block.getBlockType()) {
                            float texX = block.getTexPositions(BlockSide.UP)[0] / atlasWidth;
                            float texY = block.getTexPositions(BlockSide.UP)[1] / atlasHeight;
                            float texX2 = texX + texWidth;
                            float texY2 = texY + texHeight;
                            if(AO_MPM + AO_PPP < AO_PPM + AO_MPP) {
                                data.addAll(List.of(
                                        -0.5f + xp, 0.5f + y,  0.5f + zp, texX,  texY2, AO_MPP,
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, texX,  texY,  AO_MPM,
                                         0.5f + xp, 0.5f + y, -0.5f + zp, texX2, texY,  AO_PPM,
                                         0.5f + xp, 0.5f + y, -0.5f + zp, texX2, texY,  AO_PPM,
                                         0.5f + xp, 0.5f + y,  0.5f + zp, texX2, texY2, AO_PPP,
                                        -0.5f + xp, 0.5f + y,  0.5f + zp, texX,  texY2, AO_MPP));
                            } else {
                                data.addAll(List.of(
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, texX,  texY2, AO_MPM,
                                         0.5f + xp, 0.5f + y, -0.5f + zp, texX,  texY,  AO_PPM,
                                         0.5f + xp, 0.5f + y,  0.5f + zp, texX2, texY,  AO_PPP,
                                         0.5f + xp, 0.5f + y,  0.5f + zp, texX2, texY,  AO_PPP,
                                        -0.5f + xp, 0.5f + y,  0.5f + zp, texX2, texY2, AO_MPP,
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, texX,  texY2, AO_MPM));
                            }
                        }

                        /*BlockState down = getBlockAt(x, y - 1, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (down.getTransparent() && down.getBlockType() != block.getBlockType()) {
                            if(AO_MMM + AO_PMP < AO_PMM + AO_MMP) {
                                data.addAll(List.of(
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_MMP, colorG * AO_MMP, colorB * AO_MMP,
                                        0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_PMM, colorG * AO_PMM, colorB * AO_PMM,
                                        -0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_MMM, colorG * AO_MMM, colorB * AO_MMM,
                                        0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_PMM, colorG * AO_PMM, colorB * AO_PMM,
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_MMP, colorG * AO_MMP, colorB * AO_MMP,
                                        0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_PMP, colorG * AO_PMP, colorB * AO_PMP));
                            } else {
                                data.addAll(List.of(
                                        -0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_MMM, colorG * AO_MMM, colorB * AO_MMM,
                                        0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_PMP, colorG * AO_PMP, colorB * AO_PMP,
                                        0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_PMM, colorG * AO_PMM, colorB * AO_PMM,
                                        0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_PMP, colorG * AO_PMP, colorB * AO_PMP,
                                        -0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_MMM, colorG * AO_MMM, colorB * AO_MMM,
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_MMP, colorG * AO_MMP, colorB * AO_MMP));
                            }
                        }

                        BlockState left = getBlockAt(x - 1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (left.getTransparent() && left.getBlockType() != block.getBlockType()) {
                            if(AO_MPP + AO_MPM > AO_MMM + AO_MMP) {
                                data.addAll(List.of(
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_MMP, colorG * AO_MMP, colorB * AO_MMP,
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, colorR * AO_MPM, colorG * AO_MPM, colorB * AO_MPM,
                                        -0.5f + xp, 0.5f + y, 0.5f + zp, colorR * AO_MPP, colorG * AO_MPP, colorB * AO_MPP,
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, colorR * AO_MPM, colorG * AO_MPM, colorB * AO_MPM,
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_MMP, colorG * AO_MMP, colorB * AO_MMP,
                                        -0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_MMM, colorG * AO_MMM, colorB * AO_MMM));
                            } else {
                                data.addAll(List.of(
                                        -0.5f + xp, 0.5f + y, 0.5f + zp, colorR * AO_MPP, colorG * AO_MPP, colorB * AO_MPP,
                                        -0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_MMM, colorG * AO_MMM, colorB * AO_MMM,
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, colorR * AO_MPM, colorG * AO_MPM, colorB * AO_MPM,
                                        -0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_MMM, colorG * AO_MMM, colorB * AO_MMM,
                                        -0.5f + xp, 0.5f + y, 0.5f + zp, colorR * AO_MPP, colorG * AO_MPP, colorB * AO_MPP,
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_MMP, colorG * AO_MMP, colorB * AO_MMP));
                            }
                        }

                        BlockState right = getBlockAt(x + 1, y, z, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (right.getTransparent() && right.getBlockType() != block.getBlockType()) {
                            if(false) {
                                data.addAll(List.of(
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_PMP, colorG * AO_PMP, colorB * AO_PMP,
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, colorR * AO_PPM, colorG * AO_PPM, colorB * AO_PPM,
                                        -0.5f + xp, 0.5f + y, 0.5f + zp, colorR * AO_PPP, colorG * AO_PPP, colorB * AO_PPP,
                                        -0.5f + xp, 0.5f + y, -0.5f + zp, colorR * AO_PPM, colorG * AO_PPM, colorB * AO_PPM,
                                        -0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_PMP, colorG * AO_PMP, colorB * AO_PMP,
                                        -0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_PMM, colorG * AO_PMM, colorB * AO_PMM));
                            } else {
                                data.addAll(List.of(
                                        0.5f + xp, 0.5f + y, 0.5f + zp, colorR * AO_PPP, colorG * AO_PPP, colorB * AO_PPP,
                                        0.5f + xp, 0.5f + y, -0.5f + zp, colorR * AO_PPM, colorG * AO_PPM, colorB * AO_PPM,
                                        0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_PMM, colorG * AO_PMM, colorB * AO_PMM,
                                        0.5f + xp, -0.5f + y, -0.5f + zp, colorR * AO_PMM, colorG * AO_PMM, colorB * AO_PMM,
                                        0.5f + xp, -0.5f + y, 0.5f + zp, colorR * AO_PMP, colorG * AO_PMP, colorB * AO_PMP,
                                        0.5f + xp, 0.5f + y, 0.5f + zp, colorR * AO_PPP, colorG * AO_PPP, colorB * AO_PPP));
                            }
                        }

                        // TODO: Add proper support for AO_MMM, AO_MMP, AO_MPM, AO_MPP
                        BlockState front = getBlockAt(x, y, z + 1, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (front.getTransparent() && front.getBlockType() != block.getBlockType()) {
                            data.addAll(List.of(
                                    -0.5f + xp, -0.5f + y, 0.5f + zp, colorR*AO_MMP, colorG*AO_MMP, colorB*AO_MMP,
                                    0.5f + xp, 0.5f + y, 0.5f + zp, colorR*AO_PPP, colorG*AO_PPP, colorB*AO_PPP,
                                    0.5f + xp, -0.5f + y, 0.5f + zp, colorR*AO_PMP, colorG*AO_PMP, colorB*AO_PMP,

                                    0.5f + xp, 0.5f + y, 0.5f + zp, colorR*AO_PPP, colorG*AO_PPP, colorB*AO_PPP,
                                    -0.5f + xp, -0.5f + y, 0.5f + zp, colorR*AO_MMP, colorG*AO_MMP, colorB*AO_MMP,
                                    -0.5f + xp, 0.5f + y, 0.5f + zp, colorR*AO_MPP, colorG*AO_MPP, colorB*AO_MPP));
                        }

                        BlockState back = getBlockAt(x, y, z - 1, this,c0,c1,c2,c3,c4,c5,c6,c7);
                        if (back.getTransparent() && back.getBlockType() != block.getBlockType()) {
                            data.addAll(List.of(
                                    -0.5f + xp, -0.5f + y, -0.5f + zp, colorR*AO_MMM, colorG*AO_MMM, colorB*AO_MMM,
                                    0.5f + xp, -0.5f + y, -0.5f + zp, colorR*AO_PMM, colorG*AO_PMM, colorB*AO_PMM,
                                    0.5f + xp, 0.5f + y, -0.5f + zp, colorR*AO_PPM, colorG*AO_PPM, colorB*AO_PPM,
                                    0.5f + xp, 0.5f + y, -0.5f + zp, colorR*AO_PPM, colorG*AO_PPM, colorB*AO_PPM,
                                    -0.5f + xp, 0.5f + y, -0.5f + zp, colorR*AO_MPM, colorG*AO_MPM, colorB*AO_MPM,
                                    -0.5f + xp, -0.5f + y, -0.5f + zp, colorR*AO_MMM, colorG*AO_MMM, colorB*AO_MMM));
                        }*/
                    }
                }
            }
        }
        float[] dataArray = new float[data.size()];
        for (int i = 0; i < data.size(); i++) dataArray[i] = data.get(i);
        this.setMesh(dataArray);
    }

    public void generateVBO() {
        VBO = glGenBuffers();
        //System.out.println("Generated VBO: " + VBO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, mesh, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        ready = true;
    }
    public int getVBO() {
        return VBO;
    }
    public void destroy() {
        if(VBO!=0) glDeleteBuffers(VBO);
        neighbors = null;
    }
    public boolean isReady() {
        return ready;
    }
    public void setNeighbors(Chunk[] neighbors) {
        this.neighbors = neighbors;
    }
    public Chunk[] getNeighbors() {
        return neighbors;
    }
}
