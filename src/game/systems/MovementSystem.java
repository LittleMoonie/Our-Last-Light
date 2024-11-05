package src.game.systems;

import src.game.components.PositionComponent;
import src.game.components.stats.SpeedComponent;
import src.game.components.InputComponent;
import src.game.entities.Entity;
import src.game.world.World;

public class MovementSystem extends System {
    private World world;

    public MovementSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        PositionComponent position = entity.getComponent(PositionComponent.class);
        SpeedComponent speed = entity.getComponent(SpeedComponent.class);
        InputComponent input = entity.getComponent(InputComponent.class);

        if (position == null || speed == null || input == null) return;

        int currentSpeed = input.sprint ? speed.sprintSpeed : speed.normalSpeed;
        int dx = 0, dy = 0;

        if (input.up) dy -= currentSpeed;
        if (input.down) dy += currentSpeed;
        if (input.left) dx -= currentSpeed;
        if (input.right) dx += currentSpeed;

        // Normalize diagonal movement
        if (dx != 0 && dy != 0) {
            dx = (int) (dx / Math.sqrt(2));
            dy = (int) (dy / Math.sqrt(2));
        }

        position.x += dx * deltaTime;
        position.y += dy * deltaTime;
    }
}
