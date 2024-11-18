package project.project.systems;

import project.project.entities.Character;
import project.project.components.HealthComponent;

import java.util.ArrayList;
import java.util.List;

public class HealthSystem {
    private final List<Character> entities;

    public HealthSystem() {
        this.entities = new ArrayList<>();
    }

    public void addEntity(Character entity) {
        if (entity.hasComponent(HealthComponent.class)) {
            entities.add(entity);
        }
    }

    public void update() {
        for (Character entity : entities) {
            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health != null && health.isAlive()) {
                health.decreaseHealth(10); // Exemple de dégâts
                System.out.println("Entity " + entity.name + " (id: " + entity.getId() + ")" + " health: " + health.getHealth());
            }
        }
    }
}
