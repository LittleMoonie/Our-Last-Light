package project.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player implements Entity {
    private Texture img;
    private Vector2 tilePos; // The player's position in tile coordinates
    private Vector2 worldPos; // The player's position in world coordinates
    private float time;

    public static final float TILE_WIDTH = 64;
    public static final float TILE_HEIGHT = 32;

    // New constructor
    public Player(Vector2 startingTilePos) {
        img = new Texture("player1.png");
        tilePos = new Vector2(startingTilePos);
        worldPos = isoToWorld(tilePos.x, tilePos.y);
        time = 2;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(img, worldPos.x - img.getWidth() / 2f, worldPos.y - img.getHeight() / 2f);
    }

    @Override
    public void update(float delta) {
        time += delta;
        if (time > 0.2f) {
            move();
        }
    }

    private void move() {
        boolean moved = false;
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            tilePos.y += 1;
            moved = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            tilePos.y -= 1;
            moved = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            tilePos.x -= 1;
            moved = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            tilePos.x += 1;
            moved = true;
        }

        if (moved) {
            worldPos = isoToWorld(tilePos.x, tilePos.y);
            time = 0;
        }
    }

    public Vector2 getWorldPosition() {
        return worldPos;
    }

    public Vector2 getTilePosition() {
        return tilePos;
    }

    public void dispose() {
        img.dispose();
    }

    // Helper method to convert isometric tile coordinates to world coordinates
    private Vector2 isoToWorld(float tileX, float tileY) {
        float worldX = (tileX - tileY) * (TILE_WIDTH / 2f);
        float worldY = (tileX + tileY) * (TILE_HEIGHT / 2f);
        return new Vector2(worldX, worldY);
    }
}
