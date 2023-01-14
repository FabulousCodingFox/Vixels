package de.fabulousfox.voxelgame.structures;

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

    public int[] getTexPositions(BlockSide side){
        int[] pos = new int[]{0, 0};

        if(block == BlockType.GRASS){
            if(side == BlockSide.UP) pos = new int[]{0, 0};
            else if(side == BlockSide.DOWN) pos = new int[]{0, 2};
            else pos = new int[]{0, 1};
        }

        if(block == BlockType.SAND){
            pos = new int[]{1, 1};
        }

        if(block == BlockType.WATER){
            pos = new int[]{3, 0};
        }

        if(block == BlockType.DIRT){
            pos = new int[]{0, 2};
        }

        if(block == BlockType.COBBLESTONE){
            pos = new int[]{2, 0};
        }

        if(block == BlockType.STONE){
            pos = new int[]{1, 0};
        }

        return pos;
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
