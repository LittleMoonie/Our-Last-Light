//// MovementSystem.java
//package project.project.systems;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.math.Vector2;
//import project.project.components.PositionComponent;
//import project.project.components.MovementComponent;
//import project.project.entities.Character;
//import project.project.entities.Player;
//
//public class MovementSystem {
//    private static final float TILE_WIDTH = 64;
//    private static final float TILE_HEIGHT = 32;
//
//    public void update(float delta, Character player) {
//        PositionComponent position = player.getComponent(PositionComponent.class);
//        MovementComponent movement = player.getComponent(MovementComponent.class);
//        position.setTilePosition(newTileX, newTileY);
///
//
//        if (position != null && movement != null) {
//            move(position, movement.speed * delta, (Player) player);
//        }
//    }
//
////    private void move(PositionComponent position, float speed, Player player) {
////        boolean moved = false;
////
////        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
////            position.tilePos.y += speed;
////            moved = true;
////        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
////            position.tilePos.y -= speed;
////            moved = true;
////        }
////        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
////            position.tilePos.x -= speed;
////            moved = true;
////        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
////            position.tilePos.x += speed;
////            moved = true;
////        }
////
////        if (moved) {
////            position.worldPos = isoToWorld(position.tilePos.x, position.tilePos.y);
////            player.setWorldPosition(position.worldPos.x, position.worldPos.y);
////        }
////    }
//
//
//    private void move(PositionComponent position, float speed, Player player) {
//        boolean moved = false;
//
//        float deltaX = 0, deltaY = 0;
//
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            deltaY += speed;
//            moved = true;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            deltaY -= speed;
//            moved = true;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//            deltaX -= speed;
//            moved = true;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            deltaX += speed;
//            moved = true;
//        }
//
//        if (moved) {
//            // Utilisation cohérente de setTilePosition
//            position.setTilePosition(
//                position.tilePos.x + deltaX,
//                position.tilePos.y + deltaY
//            );
//
//            // Synchroniser avec le joueur
//            player.setWorldPosition(position.worldPos.x, position.worldPos.y);
//        }
//    }
//
//    private Vector2 isoToWorld(float tileX, float tileY) {
//        float worldX = (tileX - tileY) * (TILE_WIDTH / 2f);
//        float worldY = (tileX + tileY) * (TILE_HEIGHT / 2f);
//        return new Vector2(worldX, worldY);
//    }
//}


package project.project.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import project.project.components.PositionComponent;
import project.project.components.MovementComponent;
import project.project.entities.Character;
import project.project.entities.Player;

public class MovementSystem {
    public void update(float delta, Character player) {
        PositionComponent position = player.getComponent(PositionComponent.class);
        MovementComponent movement = player.getComponent(MovementComponent.class);

        if (position != null && movement != null) {
            move(position, movement.speed * delta, (Player) player);
        }
    }

//    private void move(PositionComponent position, float speed, Player player) {
//        boolean moved = false;
//        float deltaX = 0, deltaY = 0;
//
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            deltaY += speed;
//            moved = true;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            deltaY -= speed;
//            moved = true;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//            deltaX -= speed;
//            moved = true;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            deltaX += speed;
//            moved = true;
//        }
//
//        if (moved) {
//            // Utilise setTilePosition pour gérer automatiquement la conversion
//            position.setTilePosition(
//                position.tilePos.x + deltaX,
//                position.tilePos.y + deltaY
//            );
//
//            // Synchronise la position du joueur
//            player.setWorldPosition(position.worldPos.x, position.worldPos.y);
//        }
//    }


    private void move(PositionComponent position, float speed, Player player) {
        boolean moved = false;
        float deltaX = 0, deltaY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            deltaY += 1;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            deltaY -= 1;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            deltaX -= 1;
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            deltaX += 1;
            moved = true;
        }

        if (moved) {
            Vector2 movement = new Vector2(deltaX, deltaY);
            if (movement.len() > 1.5f) {
                movement.nor(); // Normalize the vector to prevent acceleration
            }
            movement.scl(speed); // Scale by speed

            position.setTilePosition(
                position.tilePos.x + movement.x,
                position.tilePos.y + movement.y
            );

            player.setWorldPosition(position.worldPos.x, position.worldPos.y);
        }
    }
}
