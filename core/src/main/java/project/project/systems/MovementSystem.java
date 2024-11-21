package project.project.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import project.project.components.MovementComponent;
import project.project.components.PositionComponent;
import project.project.entities.Player;

public class MovementSystem {
    private ObjectPlacementSystem placementSystem;

    public MovementSystem(ObjectPlacementSystem placementSystem) {
        this.placementSystem = placementSystem;
    }

    public void update(float delta, Player player) {
        if (player instanceof Player) {
            PositionComponent position = player.getComponent(PositionComponent.class);
            MovementComponent movement = player.getComponent(MovementComponent.class);

            if (position != null && movement != null) {
                move(position, movement.speed * delta, player);
            }
        }
    }


    private void move(PositionComponent position, float speed, Player player) {
        float deltaX = 0, deltaY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) deltaY += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) deltaY -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) deltaX -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) deltaX += 1;

        if (deltaX != 0 || deltaY != 0) {
            Vector2 newPos = new Vector2(position.worldPos.x + deltaX * speed, position.worldPos.y + deltaY * speed);

            if (placementSystem.canMoveTo(newPos)) {
                position.setWorldPosition(newPos.x, newPos.y);
            }
        }
    }
}
