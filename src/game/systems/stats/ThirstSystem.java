package src.game.systems.stats;

import src.game.entities.Entity;
import src.game.components.stats.ThirstComponent;
import src.game.systems.System;

public class ThirstSystem extends System {

    @Override
    public void update(Entity entity, float deltaTime) {
        ThirstComponent thirst = entity.getComponent(ThirstComponent.class);
        if (thirst != null) {
            thirst.decreaseThirst(1); // Example: decrease thirst by 1 per update cycle
        }
    }
}
