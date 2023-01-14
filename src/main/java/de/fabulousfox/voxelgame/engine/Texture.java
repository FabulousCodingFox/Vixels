package de.fabulousfox.voxelgame.engine;

import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    int texture;

    public Texture(String path, int texChannel, boolean alpha) {
        glActiveTexture(texChannel);
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        // set the texture wrapping parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // load image, create texture and generate mipmaps
        String absolutePath = Objects.requireNonNull(Texture.class.getClassLoader().getResource(path)).getPath().substring(1);
        if(!System.getProperty("os.name").contains("Windows")){
            absolutePath = File.separator + absolutePath;
        }
        stbi_set_flip_vertically_on_load(false);
        IntBuffer x = BufferUtils.createIntBuffer(1);
        IntBuffer y = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(absolutePath, x, y, channels, STBI_rgb_alpha);
        if (image == null) {
            throw new IllegalStateException("Could not decode image file ["+ absolutePath +"]: ["+ stbi_failure_reason() +"]");
        }
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, x.get(), y.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        stbi_image_free(image);
    }

    public int get() {
        return texture;
    }
}
