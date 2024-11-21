package project.project.components;

import com.badlogic.gdx.graphics.Texture;

public class TextureComponent implements Component {
    public Texture texture;
    public float width;
    public float height;
    public String filePath;

    public TextureComponent(String texturePath, float width, float height) {
        this.texture = new Texture(texturePath);
        this.filePath = texturePath;
        this.width = width;
        this.height = height;
    }
    public Texture getTexture() {
        return texture;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void dispose() {
        texture.dispose();
    }
}
