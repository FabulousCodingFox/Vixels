package de.fabulousfox.voxelgame.entity;

import de.fabulousfox.voxelgame.libs.Location;

public class Player extends Entity {
    public Player(Location location) {
        super(EntityType.PLAYER, location);
    }
}
