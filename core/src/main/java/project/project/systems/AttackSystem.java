package project.project.systems;

import com.badlogic.gdx.math.Rectangle;
import project.project.components.HitboxComponent;
import project.project.entities.Entity;
import project.project.entities.Character;
import project.project.components.PositionComponent;
import project.project.components.AttackComponent;
import project.project.components.HealthComponent;

import java.util.List;

public class AttackSystem {

    /**
     * Gère l'attaque d'un joueur contre une liste de mobs.
     *
     * @param attacker Le personnage qui attaque (peut être le joueur ou un mob).
     * @param targets  La liste des cibles potentielles.
     */
    public void handleAttack(Character attacker, List<Entity> targets) {
        AttackComponent attackComponent = attacker.getComponent(AttackComponent.class);
        PositionComponent attackerPosition = attacker.getComponent(PositionComponent.class);
        HitboxComponent attackerHitbox = attacker.getComponent(HitboxComponent.class);

        if (attackComponent == null || attackerPosition == null || attackerHitbox == null) {
            return; // Pas d'attaque possible.
        }

        // Définir la portée de l'attaque (une hitbox agrandie autour de l'attaquant).
        Rectangle attackRange = new Rectangle(
            attackerPosition.worldPos.x - attackComponent.getAttackRange() / 2,
            attackerPosition.worldPos.y - attackComponent.getAttackRange() / 2,
            attackerHitbox.width + attackComponent.getAttackRange(),
            attackerHitbox.height + attackComponent.getAttackRange()
        );

        // Parcourir les cibles pour détecter les collisions avec la portée de l'attaque.
        for (Entity entity : targets) {
            if (entity instanceof Character) {
                Character target = (Character) entity;
                PositionComponent targetPosition = target.getComponent(PositionComponent.class);
                HitboxComponent targetHitbox = target.getComponent(HitboxComponent.class);
                HealthComponent targetHealth = target.getComponent(HealthComponent.class);

                if (targetPosition != null && targetHitbox != null && targetHealth != null) {
                    Rectangle targetBounds = new Rectangle(
                        targetPosition.worldPos.x,
                        targetPosition.worldPos.y,
                        targetHitbox.width,
                        targetHitbox.height
                    );

                    if (attackRange.overlaps(targetBounds)) {
                        // Infliger des dégâts à la cible.
                        targetHealth.currentHealth -= attackComponent.getDamage();

                        // Vérifier si la cible est morte.
                        if (targetHealth.currentHealth <= 0) {
                            handleTargetDeath(target);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gère la mort d'une cible (par exemple, retirer le mob de la scène).
     *
     * @param target Le mob ou personnage mort.
     */
    private void handleTargetDeath(Character target) {
        System.out.println(target.getName() + " est mort !");
        // Logique supplémentaire pour retirer la cible de la scène ou donner des récompenses.
    }
}
