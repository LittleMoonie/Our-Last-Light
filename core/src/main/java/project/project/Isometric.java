package project.project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import project.project.screens.GameScreen;

public class Isometric extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(batch)); // Set GameScreen as the active screen
    }

    @Override
    public void render() {
        super.render(); // Calls render() on the active screen (GameScreen)
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
