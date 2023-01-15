package de.fabulousfox.voxelgame.client;

import de.fabulousfox.voxelgame.engine.Key;
import de.fabulousfox.voxelgame.engine.OpenGlRenderer;
import de.fabulousfox.voxelgame.entity.Player;
import de.fabulousfox.voxelgame.libs.Location;
import de.fabulousfox.voxelgame.world.World;

public class Client {
    private Player player;
    private Location prevLocation;
    private OpenGlRenderer engine;

    private int renderDistance;

    private World world;

    public Client() {
        this.player = new Player(new Location(0, 100, 0));
        prevLocation = new Location(player.getLocation());

        this.renderDistance = 8;

        world = new World();

        this.engine = new OpenGlRenderer(
                this,
                "VoxelGame",
                800,
                600
        );

        world.restartChunkThread(
                player.getLocation().getChunkPosition()[0],
                player.getLocation().getChunkPosition()[1],
                renderDistance
        );

        boolean running = true;
        while(running) {
            running = engine.render(world.getRenderableAndUpdateChunks(), player.getLocation().getChunkPosition()[0], player.getLocation().getChunkPosition()[1]);
        }
    }

    public void onKeyPress(Key key, float deltaTime) {
        if (key == Key.WALK_FORWARD) engine.getCamera().processKeyboard(Key.WALK_FORWARD, deltaTime);
        if (key == Key.WALK_BACKWARD) engine.getCamera().processKeyboard(Key.WALK_BACKWARD, deltaTime);
        if (key == Key.WALK_LEFT) engine.getCamera().processKeyboard(Key.WALK_LEFT, deltaTime);
        if (key == Key.WALK_RIGHT) engine.getCamera().processKeyboard(Key.WALK_RIGHT, deltaTime);
        if (key == Key.JUMP) engine.getCamera().processKeyboard(Key.JUMP, deltaTime);
        if (key == Key.CROUCH) engine.getCamera().processKeyboard(Key.CROUCH, deltaTime);

        player.teleport(new Location(engine.getCamera().getPosition().x, engine.getCamera().getPosition().y, engine.getCamera().getPosition().z, engine.getCamera().getYaw(), engine.getCamera().getPitch()));

        if(player.getLocation().getChunkPosition()[0] != prevLocation.getChunkPosition()[0] || player.getLocation().getChunkPosition()[1] != prevLocation.getChunkPosition()[1]) {
            world.restartChunkThread(
                    player.getLocation().getChunkPosition()[0],
                    player.getLocation().getChunkPosition()[1],
                    renderDistance
            );
        }

        prevLocation.setLocation(player.getLocation());
    }
    public void onMouseMove(double xpos, double ypos) {
        engine.getCamera().processMouseMovement((float) xpos, (float) ypos, true);
    }
}
