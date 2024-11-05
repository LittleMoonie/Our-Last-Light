package src.game.systems.stats;

import src.game.entities.Entity;
import src.game.components.stats.HealthComponent;
import src.game.systems.System;

public class HealthSystem extends System {

    @Override
    public void update(Entity entity, float deltaTime) {
        HealthComponent health = entity.getComponent(HealthComponent.class);
        if (health != null) {
            // Example: decrease health by 1 per update cycle if some condition is met
            if (shouldTakeDamage(entity)) {
                health.decreaseHealth(1);
            }

            // Handle death
            if (!health.isAlive()) {
                handleDeath(entity);
            }
        }
    }

    private boolean shouldTakeDamage(Entity entity) {
        // Placeholder for actual game logic to determine if the entity should take damage
        return false;
    }

    private void handleDeath(Entity entity) {
        // Logic for what happens when an entity dies, such as removing it from the world
        java.lang.System.out.println("Entity " + entity + " has died.");
    }
}
