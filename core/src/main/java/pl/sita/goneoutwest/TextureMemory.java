package pl.sita.goneoutwest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureMemory {

    private static final Map<String, Texture> memory = new HashMap<>();

    public static Texture getTexture(String name) {
        if (memory.containsKey(name)) {
            return memory.get(name);
        } else {
            memory.put(name, new Texture(Gdx.files.internal(name)));
            return memory.get(name);
        }
    }

}
