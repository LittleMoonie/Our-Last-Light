// src/game/entities/PlayerEntity.java
package src.game.entities;

import src.game.components.*;
import src.game.components.stats.*;

public class PlayerEntity extends Entity {
    public PlayerEntity(String playerName, int startX, int startY) {
        addComponent(new PlayerComponent(playerName));             // Mark this entity as the player with a name
        addComponent(new PositionComponent(startX, startY));       // Initial position
        addComponent(new InputComponent());                        // Input handling component
        addComponent(new SpeedComponent(4, 8));                    // Normal and sprint speeds
        addComponent(new InventoryComponent());                    // Inventory component
        addComponent(new MinimapComponent());                      // Minimap visibility
        addComponent(new HealthComponent(100, 100));                    // Max health of 100
        addComponent(new HungerComponent(100, 100));                    // Max hunger of 100
        addComponent(new ThirstComponent(100, 100));                    // Max thirst of 100
        addComponent(new StaminaComponent(100, 100));                   // Max stamina of 100
        addComponent(new SanityComponent(100, 100));                    // Max sanity of 100
    }
}
