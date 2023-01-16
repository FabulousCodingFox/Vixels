package de.fabulousfox.voxelgame.engine;

import de.fabulousfox.voxelgame.client.Client;
import de.fabulousfox.voxelgame.libs.Log;
import de.fabulousfox.voxelgame.world.Chunk;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGlRenderer {
    private long window;
    private int windowWidth, windowHeight;

    private int SKYBOX_VAO;

    private Shader SHADER_DEFAULT_BLOCK;
    private Shader SHADER_SKYBOX;
    private Shader SHADER_DEFAULT_WATER;

    private Skybox SKYBOX;

    private Matrix4f modelMatrix, viewMatrix, projectionMatrix;

    private Camera camera;

    private final Client client;

    private float deltaTime;
    private float lastFrame;

    private Texture TEXTURE_ATLAS;

    public static final boolean DEBUG = false;

    public OpenGlRenderer(
            Client client,
            String windowTitle,
            int windowWidth,
            int windowHeight
    ) {
        //////////////////////////////////////////////////////////////////////////////////////
        this.client = client;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        //////////////////////////////////////////////////////////////////////////////////////
        Log.init();
        Log.info("Initializing Logging...");
        System.out.println("LWJGL Version: " + Version.getVersion());
        System.out.println("GLFW Version: " + org.lwjgl.glfw.GLFW.glfwGetVersionString());
        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Initializing GLFW...");
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit()) throw new IllegalStateException("GLFW initialization failed!");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        if(DEBUG) glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Setting GLFW window callbacks...");
        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            this.windowWidth = width;
            this.windowHeight = height;
            projectionMatrix = camera.getProjectionMatrix(windowWidth, windowHeight);});
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) glfwSetWindowShouldClose(window, true);
        });
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            client.onMouseMove(xpos, ypos);});
        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Initialize GLFW framebuffer...");
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidmode != null;
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Initializing GLFW OpenGL Context...");
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        if(DEBUG) GLUtil.setupDebugMessageCallback();
        if(DEBUG) glEnable(GL_DEBUG_OUTPUT);
        glClearColor(0.0f,0.0f,0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Initializing Shaders...");
        SHADER_DEFAULT_BLOCK = new Shader(
                AssetLoader.getCoreShaderPath("block.vert"),
                AssetLoader.getCoreShaderPath("block.frag")
        );
        SHADER_SKYBOX = new Shader(
                AssetLoader.getCoreShaderPath("skybox.vert"),
                AssetLoader.getCoreShaderPath("skybox.frag")
        );
        SHADER_DEFAULT_WATER = new Shader(
                AssetLoader.getCoreShaderPath("water.vert"),
                AssetLoader.getCoreShaderPath("water.frag")
        );
        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Initializing camera...");
        camera = new Camera(window,0,100,1,0,1,0,180,0);
        modelMatrix = new Matrix4f();
        projectionMatrix = camera.getProjectionMatrix(windowWidth, windowHeight);
        viewMatrix = camera.getViewMatrix();
        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Initializing skybox...");
        SKYBOX_VAO = glGenVertexArrays();
        SKYBOX = new Skybox(SKYBOX_VAO);
        //////////////////////////////////////////////////////////////////////////////////////
        Log.info("Initializing textures...");
        TEXTURE_ATLAS = new Texture("assets/textures/atlas.png", GL_TEXTURE1, true);

        //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
    }

    private void processInput() {
        float cameraSpeed = 2.5f * deltaTime;
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) client.onKeyPress(Key.WALK_FORWARD, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) client.onKeyPress(Key.WALK_BACKWARD, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) client.onKeyPress(Key.WALK_LEFT, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) client.onKeyPress(Key.WALK_RIGHT, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) client.onKeyPress(Key.JUMP, deltaTime);
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) client.onKeyPress(Key.CROUCH, deltaTime);
        viewMatrix = camera.getViewMatrix();
    }

    public boolean render(ArrayList<Chunk> chunks, int chunkX, int chunkY) {
        if(glfwWindowShouldClose(window)) return false;

        float currentFrame = (float) glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        processInput();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Draw the Chunks
        chunks.sort((a, b) -> {
            double valueA = Math.sqrt(Math.pow(a.getX() - chunkX, 2) + Math.pow(a.getZ() - chunkY, 2));
            double valueB = Math.sqrt(Math.pow(b.getX() - chunkX, 2) + Math.pow(b.getZ() - chunkY, 2));
            int value = Double.compare(valueA, valueB);
            return Integer.compare(value, 0);
        });
        SHADER_DEFAULT_BLOCK.use();
        SHADER_DEFAULT_BLOCK.setMatrix4f("model", modelMatrix);
        SHADER_DEFAULT_BLOCK.setMatrix4f("view", viewMatrix);
        SHADER_DEFAULT_BLOCK.setMatrix4f("projection", projectionMatrix);
        SHADER_DEFAULT_BLOCK.setInt("atlas", 1);
        for(Chunk chunk : chunks) {
            if(chunk.getVAO_blocks() == -1) continue;
            glBindVertexArray(chunk.getVAO_blocks());
            glDrawArrays(GL_TRIANGLES, 0, chunk.getMeshSize_blocks());
        }

        // Draw the Water
        chunks.sort((a, b) -> {
            double valueA = Math.sqrt(Math.pow(a.getX() - chunkX, 2) + Math.pow(a.getZ() - chunkY, 2));
            double valueB = Math.sqrt(Math.pow(b.getX() - chunkX, 2) + Math.pow(b.getZ() - chunkY, 2));
            int value = Double.compare(valueA, valueB);
            return Integer.compare(0, value);
        });
        SHADER_DEFAULT_WATER.use();
        SHADER_DEFAULT_WATER.setMatrix4f("model", modelMatrix);
        SHADER_DEFAULT_WATER.setMatrix4f("view", viewMatrix);
        SHADER_DEFAULT_WATER.setMatrix4f("projection", projectionMatrix);
        SHADER_DEFAULT_WATER.setInt("atlas", 1);
        SHADER_DEFAULT_WATER.setFloat("Time", (float) glfwGetTime());
        for(Chunk chunk : chunks) {
            if(chunk.getVAO_water() == -1) continue;
            glBindVertexArray(chunk.getVAO_water());
            glDrawArrays(GL_TRIANGLES, 0, chunk.getMeshSize_water());
        }

        // Draw the Skybox
        SHADER_SKYBOX.use();
        SHADER_SKYBOX.setMatrix4f("view", new Matrix4f(new Matrix3f(viewMatrix)));
        SHADER_SKYBOX.setMatrix4f("projection", projectionMatrix);

        glBindVertexArray(SKYBOX_VAO);
        glCullFace(GL_FRONT);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glCullFace(GL_BACK);

        glfwSwapBuffers(window);
        glfwPollEvents();
        return true;
    }

    public Camera getCamera() {
        return camera;
    }
}
