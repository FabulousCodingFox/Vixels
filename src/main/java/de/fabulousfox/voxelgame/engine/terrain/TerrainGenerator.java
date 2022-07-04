package de.fabulousfox.voxelgame.engine.terrain;

import de.fabulousfox.voxelgame.libs.FastNoiseLite;
import de.fabulousfox.voxelgame.structures.Chunk;
import de.fabulousfox.voxelgame.structures.biomes.Biome;

import java.util.*;

public class TerrainGenerator {
    private static ArrayList<Biome> biomes;

    private static FastNoiseLite a;
    private static FastNoiseLite b;
    private static FastNoiseLite c;

    private static int seed;

    public static void init(ArrayList<Biome> biomes, int seed) {
        TerrainGenerator.biomes = biomes;
        TerrainGenerator.seed = seed;
        a = new FastNoiseLite();
        b = new FastNoiseLite();
        c = new FastNoiseLite();
        a.SetSeed(seed);
        b.SetSeed(seed);
        c.SetSeed(seed);

        a.SetFrequency(0.01f);
        b.SetFrequency(0.005f);
        c.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
    }

    public static Biome getBiomeAtXY(int x, int y) {
        return biomes.get(
                Math.round(((a.GetNoise(x, y) + 1) / 2) * (biomes.size()-1))
        );
    }

    public static void generateChunk(Chunk chunk) {
        //float value =((a.GetNoise(chunk.getX(), chunk.getY()) + 1) / 2) * biomes.size();
        //chunk.setBiome(biomes.get((int) value));

        chunk.setBiome(getBiomeAtXY(chunk.getX(), chunk.getY()));
        Biome front = getBiomeAtXY(chunk.getX(), chunk.getY() + 1);
        Biome back = getBiomeAtXY(chunk.getX(), chunk.getY() - 1);
        Biome left = getBiomeAtXY(chunk.getX() - 1, chunk.getY());
        Biome right = getBiomeAtXY(chunk.getX() + 1, chunk.getY());

        // Normal generation
        if (true) { // chunk.getBiome() == front && chunk.getBiome() == back && chunk.getBiome() == left && chunk.getBiome() == right
            for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                        chunk.setBlock(x, y, z, chunk.getBiome().getBlock(chunk.getBiome().getHeight(seed, x + chunk.getX() * Chunk.CHUNK_SIZE, z + chunk.getY() * Chunk.CHUNK_SIZE), y));
                    }
                }
            }
            return;
        }


        float[][] heightmap = new float[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];

        float[][] hmFront = new float[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
        float[][] hmLeft = new float[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];








        /*// Front and Back
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                float diff = Math.abs(
                        front.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) -
                        back.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)
                );

                // Calculate the terrain hight
                hmFront[x][z] =Math.min(
                        front.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE),
                        back.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)
                ) + diff/2 + (diff/2) * (z / (float) Chunk.CHUNK_SIZE);

                // If in the smaller chunk, subtract diff/2
                if(
                        front.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) > chunk.getBiome().getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) ||
                                back.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) > chunk.getBiome().getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)
                ) {hmFront[x][z] -= diff/2;}
            }
        }

        // Left and Right
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                float diff = Math.abs(
                        left.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) -
                        right.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)
                );

                // Calculate the terrain hight
                hmLeft[x][z] =Math.min(
                        left.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE),
                        right.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)
                ) + diff/2 + (diff/2) * (1f-(z / (float) Chunk.CHUNK_SIZE));

                // If in the smaller chunk, subtract diff/2
                if(
                        left.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) > chunk.getBiome().getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) ||
                                right.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE) > chunk.getBiome().getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)
                ) {hmLeft[x][z] -= diff/2;}
            }
        }


        // Combine
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                heightmap[x][z] = hmFront[x][z];

                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                    chunk.setBlock(x, y, z, chunk.getBiome().getBlock((int) heightmap[x][z], y));
                }
            }
        }*/









        /*// Every surrounding chunk, including this one, influences this terrain height by 1/5
        float[][] heightmap = new float[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];

        int totalBiomes = ((front == chunk.getBiome())?0:1) + ((back == chunk.getBiome())?0:1) + ((left == chunk.getBiome())?0:1) + ((right == chunk.getBiome())?0:1) + 1;

        // This chunk
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                heightmap[x][z] = (chunk.getBiome().getHeight(seed, x + chunk.getX() * Chunk.CHUNK_SIZE, z + chunk.getY() * Chunk.CHUNK_SIZE)) / totalBiomes;
            }
        }

        // Surrounding chunks
        // Front
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                heightmap[x][z] += (front.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)) * (1f-(z/(float)Chunk.CHUNK_SIZE));
            }
        }

        // Back
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                heightmap[x][z] += (back.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)) * (z/(float)Chunk.CHUNK_SIZE);
            }
        }

        // Left
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                heightmap[x][z] += (left.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)) * (1f-(x/(float)Chunk.CHUNK_SIZE));
            }
        }

        // Right
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                heightmap[x][z] += (right.getHeight(seed, x+chunk.getX()*Chunk.CHUNK_SIZE, z+chunk.getY()*Chunk.CHUNK_SIZE)) * (x/(float)Chunk.CHUNK_SIZE);
            }
        }

        // Generate the actual terrain
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                    chunk.setBlock(x, y, z, chunk.getBiome().getBlock((int)(heightmap[x][z]/totalBiomes), y));
                }
            }
        }*/
    }
}
