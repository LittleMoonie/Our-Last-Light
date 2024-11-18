package project.project.components;

import com.badlogic.gdx.graphics.Texture;

public class TextureComponent implements Component {
    public Texture texture;

    public TextureComponent(String texturePath) {
        this.texture = new Texture(texturePath);
    }

    public void dispose() {
        texture.dispose();
    }
}
