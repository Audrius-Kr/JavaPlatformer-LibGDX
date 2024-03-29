package jsonParse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class ParseJson {
    private ObjectMapper objectMapper;
    private SpriteFrame spriteFrame;
    FileHandle fileHandle;

    public ParseJson(String path) {
        objectMapper = new ObjectMapper();
        try {
            FileHandle fileHandle = Gdx.files.internal(path);
            spriteFrame = objectMapper.readValue(fileHandle.read(), SpriteFrame.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SpriteFrame getSpriteFrame() {
        return this.spriteFrame;
    }
}
