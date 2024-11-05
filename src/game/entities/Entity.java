package src.game.entities;

import src.game.components.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The Entity class represents a game object that holds a collection of components.
 * It provides methods to add, remove, and retrieve components.
 * Entities contain no behavior or logic themselves; they are managed by systems.
 */
public class Entity {
    private final UUID id;  // Unique identifier for each entity
    private final Map<Class<? extends Component>, Component> components;

    public Entity() {
        this.id = UUID.randomUUID();
        this.components = new HashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> componentType) {
        return componentType.cast(components.get(componentType));
    }

    public <T extends Component> void removeComponent(Class<T> componentType) {
        components.remove(componentType);
    }

    public boolean hasComponent(Class<? extends Component> componentType) {
        return components.containsKey(componentType);
    }
}
