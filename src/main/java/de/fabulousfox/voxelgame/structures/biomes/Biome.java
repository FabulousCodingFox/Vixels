package de.fabulousfox.voxelgame.structures.biomes;

import de.fabulousfox.voxelgame.structures.BlockState;

public interface Biome {
    int getHeight(int seed, int x, int z);
    BlockState getBlock(int height, int y);
    int getWaterLevel();
}
