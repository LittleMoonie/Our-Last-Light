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
