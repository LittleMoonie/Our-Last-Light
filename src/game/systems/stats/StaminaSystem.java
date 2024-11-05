package src.game.systems.stats;

import src.game.entities.Entity;
import src.game.components.stats.StaminaComponent;
import src.game.systems.System;

public class StaminaSystem extends System {

    @Override
    public void update(Entity entity, float deltaTime) {
        StaminaComponent stamina = entity.getComponent(StaminaComponent.class);
        if (stamina != null) {
            stamina.decreaseStamina(1); // Example: decrease stamina by 1 per update cycle
        }
    }
}
