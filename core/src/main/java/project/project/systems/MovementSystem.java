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
        float deltaX = 0, deltaY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            deltaY += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            deltaY -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            deltaX -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            deltaX += 1;
        }

        if (deltaX != 0 || deltaY != 0) {
            Vector2 movement = new Vector2(deltaX, deltaY).nor().scl(speed);

            // Update tile position
            position.setTilePosition(
                position.tilePos.x + movement.x,
                position.tilePos.y + movement.y
            );

            // Synchronize the world position
            player.setWorldPosition(position.worldPos.x, position.worldPos.y);
        }
    }
}
