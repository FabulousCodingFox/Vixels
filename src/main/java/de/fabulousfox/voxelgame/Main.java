package de.fabulousfox.voxelgame;

import de.fabulousfox.voxelgame.client.Client;

public class Main {
    public static void main(String[] args) {
        try {
            new Client();
        } catch (Exception ignore) { }
    }
}
