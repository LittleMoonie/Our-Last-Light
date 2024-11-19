package project.project.entities;

import com.badlogic.gdx.math.Vector2;
import project.project.components.*;

public class Player extends Character {
    private PositionComponent position;

    public Player(Vector2 startingTilePos) {
        super("Player");
        this.position = new PositionComponent(startingTilePos);
        addComponent(this.position);
        addComponent(new TextureComponent("player1.png", 50, 70));
        addComponent(new HealthComponent(100));
        addComponent(new MovementComponent(2));
    }

    public Vector2 getWorldPosition() {
        return position.worldPos;
    }

    public void setWorldPosition(float x, float y) {
        position.setWorldPosition(x, y); // Synchronizes world and tile positions
    }
}
