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
//
//    public void update(float delta, Character character) {
//        // Récupérer les composants nécessaires
//        PositionComponent position = character.getComponent(PositionComponent.class);
//        MovementComponent movement = character.getComponent(MovementComponent.class);
//
////        if (position != null && movement != null) {
////            // Calculer le déplacement
////            Vector2 movementVector = handleInput();
////            if (!movementVector.isZero()) {
////                // Normaliser pour éviter un mouvement plus rapide en diagonale
////                movementVector.nor().scl(movement.speed * delta);
////
////                // Detect backward movement (negative y-axis after rotation)
////                if (character instanceof Player) {
////                    Player player = (Player) character;
////                    // Walking Backwards
////                    if (movementVector.y > 0 && previousMovementVector.y <= 0) {
////                        player.setTexture("back_walk.gif");
////                    }
////                    // Walking forward
////                    else if (movementVector.y < 0 && previousMovementVector.y >= 0) {
////                        player.setTexture("forward_walk.gif");
////                    }
////                    // Left and top left and bottom left walk
////                    else if (movementVector.x < 0 && previousMovementVector.x >= 0) {
////                        player.setTexture("left_walk.gif");
////                    }
////                    // Right and top right and bottom right walk
////                    else if (movementVector.x > 0 && previousMovementVector.x <= 0) {
////                        player.setTexture("right_walk.gif");
////                    }
////                }
////
////                // Apply movement
////                move(position, movementVector);
////                // Synchroniser la position du joueur
////                if (character instanceof Player) {
////                    syncWorldPosition(position, (Player) character);
////                }
////            }
////
////            // Update the previous movement vector
////            previousMovementVector.set(movementVector);
////        }
//    }
//
//    /**
//     * Gère les entrées clavier pour générer un vecteur directionnel.
//     */
//    private Vector2 handleInput() {
//        Vector2 movementVector = new Vector2();
//
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            movementVector.y += 1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            movementVector.y -= 1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//            movementVector.x -= 1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            movementVector.x += 1;
//        }
//        // Rotate the movement vector by 45 degrees
//        if (!movementVector.isZero()) {
//            movementVector.rotateDeg(-45);
//        }
//
//        return movementVector;
//    }
//
//    /**
//     * Met à jour la position sur les tuiles et synchronise avec les coordonnées mondiales.
//     */
//    private void move(PositionComponent position, Vector2 movement) {
//        // Mettre à jour la position des tuiles
//        position.setTilePosition(
//            position.tilePos.x + movement.x,
//            position.tilePos.y + movement.y
//        );
//        // Optionnel : recalculer `worldPos` si nécessaire ici
//
//    }
//
//    /**
//     * Synchronise la position mondiale avec la position des tuiles.
//     */
//    private void syncWorldPosition(PositionComponent position, Player player) {
//        player.setWorldPosition(position.worldPos.x, position.worldPos.y);
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

    public void update(float delta, Character character) {
        PositionComponent position = character.getComponent(PositionComponent.class);
        MovementComponent movement = character.getComponent(MovementComponent.class);

        if (position != null && movement != null) {
            Vector2 movementVector = handleInput();
            if (!movementVector.isZero()) {
                movementVector.nor().scl(movement.speed * delta);
                move(position, movementVector);
                if (character instanceof Player) {
                    syncWorldPosition(position, (Player) character);
                }
            }
        }
    }

    private Vector2 handleInput() {
        Vector2 movementVector = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movementVector.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movementVector.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movementVector.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movementVector.x += 1;
        }
        if (!movementVector.isZero()) {
            movementVector.rotateDeg(-45);
        }

        return movementVector;
    }

    private void move(PositionComponent position, Vector2 movement) {
        position.setTilePosition(
            position.tilePos.x + movement.x,
            position.tilePos.y + movement.y
        );
    }

    private void syncWorldPosition(PositionComponent position, Player player) {
        player.setWorldPosition(position.worldPos.x, position.worldPos.y);
    }
}
