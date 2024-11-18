//// Player.java
//package project.project.entities;
//
//import com.badlogic.gdx.math.Vector2;
//import project.project.components.*;
//
//public class Player extends Character {
//
//    private Vector2 tilePos;
//    private Vector2 worldPos;
//    private PositionComponent position;
//
//    public Player(Vector2 startingTilePos) {
//        super("Player");
//        this.tilePos = startingTilePos;
//        this.worldPos = startingTilePos;
//        this.position = new PositionComponent(startingTilePos);
//        addComponent(this.position);
//        addComponent(new TextureComponent("player1.png"));
//        addComponent(new MovementComponent(2));
//    }
//
//
//    public Vector2 getWorldPosition() {
//        return worldPos;
//    }
//
//    public void setWorldPosition(float x, float y) {
//        this.worldPos.set(x, y);
//    }
//}
//package project.project.entities;
//
//import com.badlogic.gdx.math.Vector2;
//import project.project.components.*;
//
//public class Player extends Character {
//
//    private Vector2 tilePos;
//    private Vector2 worldPos;
//    private PositionComponent position;
//
//    public Player(Vector2 startingTilePos) {
//        super("Player");
//        this.tilePos = startingTilePos;
//        this.worldPos = startingTilePos;
//        this.position = new PositionComponent(startingTilePos);
//        addComponent(this.position);
//        addComponent(new TextureComponent("player1.png"));
//        addComponent(new MovementComponent(2));
//    }
//
//    public Vector2 getWorldPosition() {
//        return worldPos;
//    }
//
//    public void setWorldPosition(float x, float y) {
//        this.worldPos.set(x, y); // Met à jour la position monde
//        this.position.setWorldPosition(x, y); // Met à jour PositionComponent
//    }
//}


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
        addComponent(new TextureComponent("player1.png"));
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

