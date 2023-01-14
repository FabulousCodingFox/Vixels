package de.fabulousfox.voxelgame.world.biomes;

import de.fabulousfox.voxelgame.libs.FastNoiseLite;
import de.fabulousfox.voxelgame.libs.SplineInterpolator;
import de.fabulousfox.voxelgame.world.BlockState;

import java.util.List;

public class OceanBiome implements Biome {
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
                    List.of(71f,70f,60f,72f)
            );
        }
        heightNoise.SetSeed(seed);
        return (int) heightSampler.interpolate(heightNoise.GetNoise(x,z));
    }

    @Override
    public BlockState getBlock(int height, int y) {
        if(y > height && y<=getWaterLevel()) return new BlockState(BlockState.WATER);
        if(y <= height)return new BlockState(BlockState.SAND);
        return new BlockState(BlockState.AIR);
    }

    @Override
    public int getWaterLevel() {
        return 100;
    }
}
