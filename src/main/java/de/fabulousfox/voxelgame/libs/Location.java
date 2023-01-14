package de.fabulousfox.voxelgame.libs;

import de.fabulousfox.voxelgame.world.Chunk;

public class Location {
    public double x;
    public double y;
    public double z;

    public double pitch;
    public double yaw;

    public Location(double x, double y, double z, double pitch, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public Location(Location location) {
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.pitch = location.pitch;
        this.yaw = location.yaw;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public void setLocation(double x, double y, double z, double pitch, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setLocation(Location location) {
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.pitch = location.pitch;
        this.yaw = location.yaw;
    }

    public Location copy() {
        return new Location(this);
    }

    public int[] getChunkPosition() {
        int[] chunkPosition = new int[2];
        chunkPosition[0] = (int) (x / Chunk.CHUNK_SIZE);
        chunkPosition[1] = (int) (z / Chunk.CHUNK_SIZE);
        return chunkPosition;
    }

}
