package de.fabulousfox.voxelgame.structures.biomes;

import de.fabulousfox.voxelgame.libs.FastNoiseLite;
import de.fabulousfox.voxelgame.libs.SplineInterpolator;
import de.fabulousfox.voxelgame.structures.BlockState;

import java.util.List;

public class SwampBiome implements Biome {
    private FastNoiseLite heightNoise;
    private SplineInterpolator heightSampler;

    @Override
    public int getHeight(int seed, int x, int z) {
        if(heightNoise == null) {
            heightNoise = new FastNoiseLite();
        }
        if(heightSampler == null) {
            heightSampler = SplineInterpolator.createMonotoneCubicSpline(
                    List.of(-1f,-0.5f,0f,1f),
                    List.of(115f,110f,98f,102f)
            );
        }
        heightNoise.SetSeed(seed);
        return (int) heightSampler.interpolate(heightNoise.GetNoise(x,z));
    }

    @Override
    public BlockState getBlock(int height, int y) {
        if(y > height && y<=getWaterLevel()) return new BlockState(BlockState.WATER);
        if(y == height) return new BlockState(BlockState.GRASS);
        if(y == height - 1) return new BlockState(BlockState.DIRT);
        if(y <= height)return new BlockState(BlockState.STONE);
        return new BlockState(BlockState.AIR);
    }

    @Override
    public int getWaterLevel() {
        return 100;
    }
}
