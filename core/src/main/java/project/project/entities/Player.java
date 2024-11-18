//// Player.java
package project.project.entities;

import com.badlogic.gdx.math.Vector2;
import project.project.components.*;

public class Player extends Character {
    private PositionComponent position;

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
    }

    // Met à jour la position dans le monde
    public void setWorldPosition(float x, float y) {
        position.setWorldPosition(x, y);
    }
}

