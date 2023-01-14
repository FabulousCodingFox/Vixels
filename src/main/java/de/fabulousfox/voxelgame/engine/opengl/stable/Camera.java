package de.fabulousfox.voxelgame.engine.opengl.stable;

import de.fabulousfox.voxelgame.client.Key;
import de.fabulousfox.voxelgame.world.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public static final float YAW = -90.0f;
    public static final float PITCH = 0.0f;
    public static final float SPEED = 20.0f;
    public static final float SENSITIVITY = 0.1f;
    public static final float ZOOM = 70.0f;

    private final Vector3f Position;
    private Vector3f Front;
    private Vector3f Up;
    private Vector3f Right;
    private final Vector3f WorldUp;

    float Yaw;
    float Pitch;

    float MovementSpeed = SPEED;
    float MouseSensitivity = SENSITIVITY;
    float Zoom = ZOOM;

    float lastX;
    float lastY;

    public Camera(long window, Vector3f position, Vector3f up, float yaw, float pitch) {
        this.Position = position;
        this.WorldUp = up;
        this.Yaw = yaw;
        this.Pitch = pitch;

        double[] x = new double[1];
        double[] y = new double[1];
        org.lwjgl.glfw.GLFW.glfwGetCursorPos(window, x, y);
        lastX = (float) x[0];
        lastY = (float) y[0];
        x = null;
        y = null;
        updateCameraVectors();
    }

    public Camera(long window, float positionX, float positionY, float positionZ, float upX, float upY, float upZ, float yaw, float pitch) {
        this.Position = new Vector3f(positionX, positionY, positionZ);
        this.WorldUp = new Vector3f(upX, upY, upZ);
        this.Yaw = yaw;
        this.Pitch = pitch;
        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(Position, new Vector3f(Position).add(Front), Up);
    }

    public Matrix4f getProjectionMatrix(float width, float height) {
        return new Matrix4f().perspective((float) Math.toRadians(Zoom), width/height, 0.1f, Chunk.CHUNK_SIZE * 48);
    }

    public void processKeyboard(Key direction, float deltaTime) {
        float velocity = MovementSpeed * deltaTime;
        if (direction == Key.WALK_FORWARD) {
            Position.add(new Vector3f(Front).set(Front.x,0.0f, Front.z).normalize().mul(velocity));
        }
        if (direction == Key.WALK_BACKWARD) {
            Position.sub(new Vector3f(Front).set(Front.x,0.0f, Front.z).normalize().mul(velocity));
        }
        if (direction == Key.WALK_LEFT) {
            Position.sub(new Vector3f(Right).mul(velocity));
        }
        if (direction == Key.WALK_RIGHT) {
            Position.add(new Vector3f(Right).mul(velocity));
        }
        if (direction == Key.JUMP) {
            Position.add(new Vector3f(WorldUp).mul(velocity));
        }
        if (direction == Key.CROUCH) {
            Position.sub(new Vector3f(WorldUp).mul(velocity));
        }
    }

    public void processMouseMovement(float xp, float yp, boolean constrainPitch) {
        float xoffset = xp - lastX;
        float yoffset = lastY - yp;
        lastX = xp;
        lastY = yp;


        xoffset *= MouseSensitivity;
        yoffset *= MouseSensitivity;

        Yaw += xoffset;
        Pitch += yoffset;

        if (constrainPitch) {
            if (Pitch > 89.0f) {
                Pitch = 89.0f;
            }
            if (Pitch < -89.0f) {
                Pitch = -89.0f;
            }
        }

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        Vector3f front = new Vector3f();
        front.x = (float) Math.cos(Math.toRadians(Yaw)) * (float) Math.cos(Math.toRadians(Pitch));
        front.y = (float) Math.sin(Math.toRadians(Pitch));
        front.z = (float) Math.sin(Math.toRadians(Yaw)) * (float) Math.cos(Math.toRadians(Pitch));
        Front = front.normalize();

        Right = new Vector3f(Front).cross(WorldUp).normalize();
        Up = new Vector3f(Right).cross(Front).normalize();
    }

    public Vector3f getPosition() {
        return Position;
    }

    public float getYaw() {
        return Yaw;
    }

    public float getPitch() {
        return Pitch;
    }
}
