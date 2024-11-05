package src.game.systems.stats;

import src.game.entities.Entity;
import src.game.components.stats.HungerComponent;
import src.game.systems.System;

public class HungerSystem extends System {

    @Override
    public void update(Entity entity, float deltaTime) {
        HungerComponent hunger = entity.getComponent(HungerComponent.class);
        if (hunger != null) {
            hunger.decreaseHunger(1); // Example: decrease hunger by 1 per update cycle
        }
    }
}
