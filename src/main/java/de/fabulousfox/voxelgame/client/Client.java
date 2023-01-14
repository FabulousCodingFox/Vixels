package de.fabulousfox.voxelgame.client;

import de.fabulousfox.voxelgame.engine.opengl.stable.OpenGlRenderer;
import de.fabulousfox.voxelgame.engine.terrain.TerrainGenerator;
import de.fabulousfox.voxelgame.entity.Player;
import de.fabulousfox.voxelgame.libs.Location;
import de.fabulousfox.voxelgame.structures.Chunk;
import de.fabulousfox.voxelgame.structures.biomes.Biome;
import de.fabulousfox.voxelgame.structures.biomes.OceanBiome;
import de.fabulousfox.voxelgame.structures.biomes.SwampBiome;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Client {
    private Player player;
    private Location prevLocation;
    private OpenGlRenderer engine;
    private ArrayList<Chunk> chunks;
    private ArrayList<Chunk> chunksToRender;
    private ArrayList<Chunk> chunksToDestroy;

    private int renderDistance;
    private int seed;

    private Thread chunkThread;

    public Client() {
        this.player = new Player(new Location(0, 100, 0));
        prevLocation = new Location(player.getLocation());

        this.chunks = new ArrayList<>();
        this.chunksToRender = new ArrayList<>();
        this.chunksToDestroy = new ArrayList<>();

        this.renderDistance = 2;
        this.seed = new Random().nextInt(Integer.MAX_VALUE);

        this.engine = new OpenGlRenderer(
                this,
                "VoxelGame",
                800,
                600
        );

        ArrayList<Biome> biomes = new ArrayList<>();
        biomes.add(new SwampBiome());
        biomes.add(new OceanBiome());
        TerrainGenerator.init(biomes, seed);

        // Load in the first chunks
        chunkThread = new Thread(() -> updateChunks(seed));
        chunkThread.start();

        boolean running = true;
        while(running) {
            // Update Chunks
            ArrayList<Chunk> chunkToRender = new ArrayList<>(chunksToRender);
            for(Chunk chunk : chunkToRender) {chunk.generateVBO();}
            chunksToRender.removeIf(chunkToRender::contains);
            ArrayList<Chunk> chunkToDestroy = new ArrayList<>(chunksToDestroy);
            for(Chunk chunk : chunkToDestroy) {chunk.destroy();}
            chunksToDestroy.removeIf(chunkToDestroy::contains);

            running = engine.render(new ArrayList<>(new ArrayList<>(chunks).stream().filter(Chunk::isReady).collect(Collectors.toList())));
        }
        chunkThread.interrupt();
    }
    public void updateChunks(int seed) {
        // Get the players position
        int x = player.getLocation().getChunkPosition()[0];
        int z = player.getLocation().getChunkPosition()[1];

        // Remove all chunks outside the render distance
        chunks.forEach(chunk -> {if(chunk.getX() > x + renderDistance + 1 || chunk.getX() < x - renderDistance - 1 || chunk.getY() > z + renderDistance + 1 || chunk.getY() < z - renderDistance - 1) chunksToDestroy.add(chunk);});
        chunks.removeIf(chunk -> chunk.getX() > x + renderDistance + 1 || chunk.getX() < x - renderDistance - 1 || chunk.getY() > z + renderDistance + 1 || chunk.getY() < z - renderDistance - 1);

        // Adding chunks to the list that are within the render distance
        ArrayList<Chunk> newChunks = new ArrayList<>();
        for(int i = x - renderDistance; i <= x + renderDistance; i++) {
            for(int j = z - renderDistance; j <= z + renderDistance; j++) {
                int finalI = i;
                int finalJ = j;
                if(chunks.stream().noneMatch(chunk -> chunk.getX() == finalI && chunk.getY() == finalJ)) {
                    newChunks.add(new Chunk(i, j));
                }
            }
        }
        chunks.addAll(newChunks);

        ArrayList<Chunk> chunksToGenerate = new ArrayList<>();

        for(Chunk chunk : chunks) {
            if(chunk.getMesh() != null) continue;

            int sides = 0;
            Chunk[] neighbors = new Chunk[8];
            for(Chunk search : chunks) {
                if(search.getX()==chunk.getX()-1 && search.getY()==chunk.getY()-1){ sides++;neighbors[0] = search; }
                if(search.getX()==chunk.getX() && search.getY()==chunk.getY()-1){ sides++;neighbors[1] = search; }
                if(search.getX()==chunk.getX()+1 && search.getY()==chunk.getY()-1){ sides++;neighbors[2] = search; }

                if(search.getX()==chunk.getX()-1 && search.getY()==chunk.getY()){ sides++;neighbors[3] = search; }
                if(search.getX()==chunk.getX()+1 && search.getY()==chunk.getY()){ sides++;neighbors[4] = search; }

                if(search.getX()==chunk.getX()-1 && search.getY()==chunk.getY()+1){ sides++;neighbors[5] = search; }
                if(search.getX()==chunk.getX() && search.getY()==chunk.getY()+1){ sides++;neighbors[6] = search; }
                if(search.getX()==chunk.getX()+1 && search.getY()==chunk.getY()+1){ sides++;neighbors[7] = search; }
            }
            chunk.setNeighbors(neighbors);
            if(sides == 8) {
                chunksToGenerate.add(chunk);
            }
        }

        //Sort chunks by distance
        chunksToGenerate.sort((a, b) -> {
            double valueA = Math.sqrt(Math.pow(a.getX() - x, 2) + Math.pow(a.getY() - z, 2));
            double valueB = Math.sqrt(Math.pow(b.getX() - x, 2) + Math.pow(b.getY() - z, 2));
            int value = Double.compare(valueA, valueB);
            return Integer.compare(value, 0);
        });

        //Generate Chunks
        for(Chunk chunk : chunksToGenerate) {
            if(chunk.getBlock(0,0,0)==null) chunk.generateTerrain();
            if(chunk.getNeighbors()[0].getBlock(0,0,0)==null){ chunk.getNeighbors()[0].generateTerrain();}
            if(chunk.getNeighbors()[1].getBlock(0,0,0)==null){ chunk.getNeighbors()[1].generateTerrain();}
            if(chunk.getNeighbors()[2].getBlock(0,0,0)==null){ chunk.getNeighbors()[2].generateTerrain();}
            if(chunk.getNeighbors()[3].getBlock(0,0,0)==null){ chunk.getNeighbors()[3].generateTerrain();}
            if(chunk.getNeighbors()[4].getBlock(0,0,0)==null){ chunk.getNeighbors()[4].generateTerrain();}
            if(chunk.getNeighbors()[5].getBlock(0,0,0)==null){ chunk.getNeighbors()[5].generateTerrain();}
            if(chunk.getNeighbors()[6].getBlock(0,0,0)==null){ chunk.getNeighbors()[6].generateTerrain();}
            if(chunk.getNeighbors()[7].getBlock(0,0,0)==null){ chunk.getNeighbors()[7].generateTerrain();}
            chunk.generateMesh(
                    chunk.getNeighbors()[0],
                    chunk.getNeighbors()[1],
                    chunk.getNeighbors()[2],
                    chunk.getNeighbors()[3],
                    chunk.getNeighbors()[4],
                    chunk.getNeighbors()[5],
                    chunk.getNeighbors()[6],
                    chunk.getNeighbors()[7]
            );
            chunksToRender.add(chunk);
        }
    }
    public void onKeyPress(Key key, float deltaTime) {
        if (key == Key.WALK_FORWARD) engine.getCamera().processKeyboard(Key.WALK_FORWARD, deltaTime);
        if (key == Key.WALK_BACKWARD) engine.getCamera().processKeyboard(Key.WALK_BACKWARD, deltaTime);
        if (key == Key.WALK_LEFT) engine.getCamera().processKeyboard(Key.WALK_LEFT, deltaTime);
        if (key == Key.WALK_RIGHT) engine.getCamera().processKeyboard(Key.WALK_RIGHT, deltaTime);
        if (key == Key.JUMP) engine.getCamera().processKeyboard(Key.JUMP, deltaTime);
        if (key == Key.CROUCH) engine.getCamera().processKeyboard(Key.CROUCH, deltaTime);

        player.teleport(new Location(engine.getCamera().getPosition().x, engine.getCamera().getPosition().y, engine.getCamera().getPosition().z, engine.getCamera().getYaw(), engine.getCamera().getPitch()));

        if(player.getLocation().getChunkPosition()[0] != prevLocation.getChunkPosition()[0] || player.getLocation().getChunkPosition()[1] != prevLocation.getChunkPosition()[1]) {
            if(chunkThread.isAlive()) chunkThread.interrupt();
            chunkThread = new Thread(() -> updateChunks(seed));
            chunkThread.start();
        }

        prevLocation.setLocation(player.getLocation());
    }
    public void onMouseMove(double xpos, double ypos) {
        engine.getCamera().processMouseMovement((float) xpos, (float) ypos, true);
    }
    public static void main(String[] args) {
        new Client();
    }
}
