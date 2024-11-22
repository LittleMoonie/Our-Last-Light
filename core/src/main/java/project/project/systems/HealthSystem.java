//package project.project.systems;
//
//import project.project.entities.Character;
//import project.project.components.HealthComponent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HealthSystem {
//    private final List<Character> entities;
//
//    public HealthSystem() {
//        this.entities = new ArrayList<>();
//    }
//
//    public void addEntity(Character entity) {
//        if (entity.hasComponent(HealthComponent.class)) {
//            entities.add(entity);
//        }
//    }
//
//    public void update() {
//        for (Character entity : entities) {
//            HealthComponent health = entity.getComponent(HealthComponent.class);
//            if (health != null && health.isAlive()) {
//                health.decreaseHealth(10); // Exemple de dégâts
//            }
//        }
//    }
//}

package project.project.systems;

import project.project.components.AttackComponent;
import project.project.entities.Character;
import project.project.components.HealthComponent;
import project.project.entities.Player;
import project.project.entities.enemies.Mob;

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
//            if (health != null && health.isAlive()) {
//                // Example of periodic damage, can be removed if not needed
//                health.takeDamage(10);
//            }
        }
    }

    public void applyDamage(Character entity, int damage) {
        HealthComponent health = entity.getComponent(HealthComponent.class);
        if (health != null && health.isAlive()) {
            health.takeDamage(damage);
        }
    }

//    public void applyDamageFromPlayer(Player player, Character target) {
//        if (target instanceof Mob) {
//            Mob mobTarget = (Mob) target;
//            applyDamage(mobTarget, player.getComponent(AttackComponent.class).getDamage());
//        }
//    }
//
//    public void applyDamageFromMob(Mob mob, Player player) {
//        applyDamage(player, mob.getComponent(AttackComponent.class).getDamage());
//    }
}
