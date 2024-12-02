package project.project.ui.components;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

// Modified Methods
public abstract class BaseUI {
    protected Stage stage;
    protected Skin skin;

    public BaseUI(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
        initialize(); // Calls initialize in subclasses
    }

    protected abstract void initialize(); // Each subclass must implement this

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
