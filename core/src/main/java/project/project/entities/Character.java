package project.project.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import project.project.components.Component;

public class Character {
    private final UUID id;
    private final Map<Class<? extends Component>, Component> components;
    public String name;

    public Character(String name) {
        this.id = UUID.randomUUID();
        this.components = new HashMap<>();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }



}
