package de.fabulousfox.voxelgame.entity;

import de.fabulousfox.voxelgame.libs.Location;

enum EntityType {
    PLAYER,
    PIG,
    ZOMBIE
}

public class Entity {
    public static final EntityType PLAYER = EntityType.PLAYER;
    public static final EntityType PIG = EntityType.PIG;
    public static final EntityType ZOMBIE = EntityType.ZOMBIE;

    private Location location;

    public Entity(EntityType type, Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return new Location(location);
    }

    public void teleport(Location location) {
        this.location = new Location(location);
    }
}
