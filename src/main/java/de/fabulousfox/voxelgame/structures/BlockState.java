package de.fabulousfox.voxelgame.structures;

import java.util.Random;

enum BlockType {
    AIR,
    STONE,
    GRASS,
    DIRT,
    COBBLESTONE,
    SAND,
    WATER
}

public class BlockState {
    public static final BlockType AIR = BlockType.AIR;
    public static final BlockType STONE = BlockType.STONE;
    public static final BlockType GRASS = BlockType.GRASS;
    public static final BlockType DIRT = BlockType.DIRT;
    public static final BlockType COBBLESTONE = BlockType.COBBLESTONE;
    public static final BlockType SAND = BlockType.SAND;
    public static final BlockType WATER = BlockType.WATER;

    public static BlockType blockTypeFromString(String s) {
        return switch (s.toUpperCase()) {
            case "STONE" -> BlockType.STONE;
            case "GRASS" -> BlockType.GRASS;
            case "DIRT" -> BlockType.DIRT;
            case "COBBLESTONE" -> BlockType.COBBLESTONE;
            case "SAND" -> BlockType.SAND;
            case "WATER" -> BlockType.WATER;
            default -> BlockType.AIR;
        };
    }

    public static float[] getBlockColor(BlockType blockType) {
        return switch (blockType) {
            case STONE, COBBLESTONE -> new float[]{0.53f, 0.54f, 0.55f};
            case GRASS -> new float[]{0.33f, 0.49f, 0.27f};
            case DIRT -> new float[]{0.6f, 0.46f, 0.32f};
            case SAND -> new float[]{0.77f, 0.65f, 0.39f};
            case WATER -> new float[]{0.45f, 0.71f, 0.98f};
            default -> new float[]{0, 0, 0};
        };
    }

    private final BlockType block;

    public BlockState(BlockType block) {
        this.block = block;
    }

    public BlockType getBlockType() {
        return block;
    }

    public boolean getTransparent() {
        return (block == BlockType.AIR || block == BlockType.WATER);
    }
}
