package src.game.systems.stats;

import src.game.entities.Entity;
import src.game.components.stats.SanityComponent;
import src.game.systems.System;

public class SanitySystem extends System {

    @Override
    public void update(Entity entity, float deltaTime) {
        SanityComponent sanity = entity.getComponent(SanityComponent.class);
        if (sanity != null) {
            sanity.decreaseSanity(1); // Example: decrease sanity by 1 per update cycle
        }
    }
}
