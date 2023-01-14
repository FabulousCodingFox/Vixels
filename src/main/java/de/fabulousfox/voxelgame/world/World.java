package de.fabulousfox.voxelgame.world;

import de.fabulousfox.voxelgame.world.biomes.Biome;
import de.fabulousfox.voxelgame.world.biomes.OceanBiome;
import de.fabulousfox.voxelgame.world.biomes.PlainsBiome;
import de.fabulousfox.voxelgame.world.biomes.SwampBiome;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class World {
    private final ArrayList<Chunk> chunks;
    private final ArrayList<Chunk> chunksToRender;
    private final ArrayList<Chunk> chunksToDestroy;

    private final int seed;

    private Thread chunkThread;

    public World(){
        this.seed = new Random().nextInt(Integer.MAX_VALUE);
        this.chunks = new ArrayList<>();
        this.chunksToRender = new ArrayList<>();
        this.chunksToDestroy = new ArrayList<>();

        ArrayList<Biome> biomes = new ArrayList<>();
        biomes.add(new SwampBiome());
        biomes.add(new OceanBiome());
        biomes.add(new PlainsBiome());
        TerrainGenerator.init(biomes);
    }

    public void restartChunkThread(int x, int z, int renderDistance){
        if(chunkThread != null && chunkThread.isAlive()) chunkThread.interrupt();
        chunkThread = new Thread(() -> updateChunks(x, z, renderDistance));
        chunkThread.start();
    }

    public ArrayList<Chunk> getRenderableAndUpdateChunks(){
        ArrayList<Chunk> chunkToRender = new ArrayList<>(chunksToRender);
        for(Chunk chunk : chunkToRender) {
            chunk.generateVBO_blocks();
            chunk.generateVBO_water();
        }
        chunksToRender.removeIf(chunkToRender::contains);
        ArrayList<Chunk> chunkToDestroy = new ArrayList<>(chunksToDestroy);
        for(Chunk chunk : chunkToDestroy) {chunk.destroy();}
        chunksToDestroy.removeIf(chunkToDestroy::contains);

        return new ArrayList<>(chunks).stream().filter(Chunk::isVBOGenerated).collect(Collectors.toCollection(ArrayList::new));
    }

    public Chunk getChunkAt(int x, int z){
        for(Chunk chunk:chunks){
            if(x == chunk.getX() && z == chunk.getZ()) return chunk;
        }
        return null;
    }

    public void updateChunks(int x, int z, int renderDistance) {
        // Remove all chunks outside the render distance
        chunks.forEach(chunk -> {if(chunk.getX() > x + renderDistance + 1 || chunk.getX() < x - renderDistance - 1 || chunk.getZ() > z + renderDistance + 1 || chunk.getZ() < z - renderDistance - 1) chunksToDestroy.add(chunk);});
        chunks.removeIf(chunk -> chunk.getX() > x + renderDistance + 1 || chunk.getX() < x - renderDistance - 1 || chunk.getZ() > z + renderDistance + 1 || chunk.getZ() < z - renderDistance - 1);

        // Adding chunks to the list that are within the render distance
        ArrayList<Chunk> newChunks = new ArrayList<>();
        for(int i = x - renderDistance; i <= x + renderDistance; i++) {
            for(int j = z - renderDistance; j <= z + renderDistance; j++) {
                int finalI = i;
                int finalJ = j;
                if(chunks.stream().noneMatch(chunk -> chunk.getX() == finalI && chunk.getZ() == finalJ)) {
                    newChunks.add(new Chunk(i, j));
                }
            }
        }
        chunks.addAll(newChunks);

        ArrayList<Chunk> chunksToGenerate = new ArrayList<>();

        for(Chunk chunk : chunks) {
            if(chunk.getMesh_blocks() != null) continue;

            int sides = 0;
            for(Chunk search : chunks) {
                if(search.getX()==chunk.getX()-1 && search.getZ()==chunk.getZ()-1){ sides++; }
                if(search.getX()==chunk.getX() && search.getZ()==chunk.getZ()-1){ sides++; }
                if(search.getX()==chunk.getX()+1 && search.getZ()==chunk.getZ()-1){ sides++; }

                if(search.getX()==chunk.getX()-1 && search.getZ()==chunk.getZ()){ sides++; }
                if(search.getX()==chunk.getX()+1 && search.getZ()==chunk.getZ()){ sides++; }

                if(search.getX()==chunk.getX()-1 && search.getZ()==chunk.getZ()+1){ sides++; }
                if(search.getX()==chunk.getX() && search.getZ()==chunk.getZ()+1){ sides++; }
                if(search.getX()==chunk.getX()+1 && search.getZ()==chunk.getZ()+1){ sides++; }
            }
            if(sides == 8) {
                chunksToGenerate.add(chunk);
            }
        }

        //Sort chunks by distance
        chunksToGenerate.sort((a, b) -> {
            double valueA = Math.sqrt(Math.pow(a.getX() - x, 2) + Math.pow(a.getZ() - z, 2));
            double valueB = Math.sqrt(Math.pow(b.getX() - x, 2) + Math.pow(b.getZ() - z, 2));
            int value = Double.compare(valueA, valueB);
            return Integer.compare(value, 0);
        });

        //Generate Chunks
        for(Chunk chunk : chunksToGenerate) {
            if(!chunk.isTerrainGenerated()) chunk.generateTerrain(seed);
            Chunk c0 = getChunkAt(chunk.getX()  - 1, chunk.getZ() - 1);
            Chunk c1 = getChunkAt(chunk.getX(), chunk.getZ() - 1);
            Chunk c2 = getChunkAt(chunk.getX() + 1, chunk.getZ() - 1);
            Chunk c3 = getChunkAt(chunk.getX() - 1, chunk.getZ());
            Chunk c4 = getChunkAt(chunk.getX() + 1, chunk.getZ());
            Chunk c5 = getChunkAt(chunk.getX() - 1, chunk.getZ() + 1);
            Chunk c6 = getChunkAt(chunk.getX(), chunk.getZ() + 1);
            Chunk c7 = getChunkAt(chunk.getX() + 1, chunk.getZ() + 1);
            if(!c0.isTerrainGenerated()){ c0.generateTerrain(seed); }
            if(!c1.isTerrainGenerated()){ c1.generateTerrain(seed); }
            if(!c2.isTerrainGenerated()){ c2.generateTerrain(seed); }
            if(!c3.isTerrainGenerated()){ c3.generateTerrain(seed); }
            if(!c4.isTerrainGenerated()){ c4.generateTerrain(seed); }
            if(!c5.isTerrainGenerated()){ c5.generateTerrain(seed); }
            if(!c6.isTerrainGenerated()){ c6.generateTerrain(seed); }
            if(!c7.isTerrainGenerated()){ c7.generateTerrain(seed); }
            chunk.generateMesh(c0, c1, c2, c3, c4, c5, c6, c7);
            chunksToRender.add(chunk);
        }
    }
}
