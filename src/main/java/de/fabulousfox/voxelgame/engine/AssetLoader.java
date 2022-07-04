package de.fabulousfox.voxelgame.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetLoader {

    // TODO: DIRTY HACK TO GET IT TO WORK

    public static String getCoreShaderPath(String path) {
        return "assets/shaders/core/" + path;
    }

    public static String readTextFile(String path) throws IOException {
        InputStream inputStream = AssetLoader.class.getClassLoader().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return readFromBufferedReader(reader);
    }

    private static String readFromBufferedReader(BufferedReader br) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {resultStringBuilder.append(line).append("\n");}
        return resultStringBuilder.toString();
    }
}
