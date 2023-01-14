package de.fabulousfox.voxelgame.world.biomes;

import de.fabulousfox.voxelgame.world.BlockState;

public interface Biome {
    int getHeight(int seed, int x, int z);
    BlockState getBlock(int height, int y);
    int getWaterLevel();
}
