package project.project.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import project.project.components.*;
import project.project.Constants;

import static project.project.utils.CoordinateUtils.tileToWorld;
import static project.project.utils.CoordinateUtils.worldToTile;
public class Player extends Character {
    private PositionComponent position;

public class Player implements Entity {
    private Texture img;
    private Vector2 tilePos; // The player's position in tile coordinates
    private Vector2 worldPos; // The player's position in world coordinates
    private float moveTimer;

    public static final float TILE_WIDTH = Constants.TILE_WIDTH * 2f; // Adjusted for proper scaling
    public static final float TILE_HEIGHT = Constants.TILE_HEIGHT * 2f;

    public Player(Vector2 startingWorldPos) {
        img = new Texture(Constants.PLAYER_TEXTURE);
        worldPos = new Vector2(startingWorldPos);
        tilePos = worldToTile(worldPos.x, worldPos.y);
        moveTimer = 0;
    public Player(Vector2 startingTilePos) {
        super("Player");
        this.position = new PositionComponent(startingTilePos);
        this.position.setTilePosition(startingTilePos.x, startingTilePos.y); // Conversion initiale
        addComponent(this.position);
        addComponent(new TextureComponent("player1.png" , 50, 70));
        addComponent(new HealthComponent(100));
        addComponent(new MovementComponent(2));
    }

    // Obtient la position dans le monde
    public Vector2 getWorldPosition() {
        return position.worldPos;
    @Override
    public void render(SpriteBatch batch) {
        // Ensure the player is centered on the tile
        batch.draw(img, worldPos.x - img.getWidth() / 2f, worldPos.y - img.getHeight() / 2f);
    }

    @Override
    public void update(float delta) {
        moveTimer += delta;
        if (moveTimer > Constants.PLAYER_MOVE_INTERVAL) {
            move(delta);
        }
    }

    private void move(float delta) {
        boolean moved = false;
        Vector2 direction = new Vector2(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            direction.y += 1;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            direction.y -= 1;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction.x -= 1;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction.x += 1;
            moved = true;
        }

        if (moved) {
            // Normalize direction to prevent faster diagonal movement
            if (direction.len() > 0) {
                direction.nor();
            }

            tilePos.x += direction.x;
            tilePos.y += direction.y;
            worldPos = tileToWorld(tilePos.x, tilePos.y);
            moveTimer = 0;
        }
    }

    public Vector2 getWorldPosition() {
        return worldPos;
    // Met à jour la position dans le monde
    public void setWorldPosition(float x, float y) {
        position.setWorldPosition(x, y);
    }

    public Vector2 getTilePosition() {
        return tilePos;
    }

    public void dispose() {
        img.dispose();
    }
}
