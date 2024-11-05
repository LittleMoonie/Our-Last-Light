package src.game.systems;

import src.game.entities.Entity;

/**
 * The System class represents a processing unit that operates on entities
 * with specific components. Each subclass of System defines its own logic
 * for updating entities.
 */
public abstract class System {
    /**
     * Updates the system's logic for the given entity.
     * This method is called once per frame, with the time delta
     * to ensure smooth and consistent updates.
     *
     * @param entity The entity to update.
     * @param deltaTime The time elapsed since the last update (useful for time-based calculations).
     */
    public abstract void update(Entity entity, float deltaTime);
}
