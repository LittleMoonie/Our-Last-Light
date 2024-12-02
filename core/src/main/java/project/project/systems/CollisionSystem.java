//package project.project.systems;
//
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector2;
//import project.project.components.HitboxComponent;
//import project.project.components.PositionComponent;
//import project.project.entities.Character;
//
//import java.util.List;
//
//public class CollisionSystem {
//
//    /**
//     * Vérifie les collisions entre le joueur et une liste d'entités.
//     *
//     * @param player   Le joueur (avec une hitbox).
//     * @param entities La liste des entités dans le jeu.
//     * @return La première entité avec laquelle le joueur est en collision, ou null si aucune collision.
//     */
//    public Character checkCollision(Character player, List<Character> entities) {
//        HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
//        PositionComponent playerPos = player.getComponent(PositionComponent.class);
//
//        if (playerHitbox == null || playerPos == null) {
//            return null; // Le joueur n'a pas de hitbox ou de position.
//        }
//
//        // Calculer la hitbox actuelle du joueur.
//        Rectangle playerBounds = new Rectangle(
//            playerPos.worldPos.x,
//            playerPos.worldPos.y,
//            playerHitbox.width,
//            playerHitbox.height
//        );
//
//        // Vérifier les collisions avec les entités.
//        for (Character entity : entities) {
//            HitboxComponent entityHitbox = entity.getComponent(HitboxComponent.class);
//            PositionComponent entityPos = entity.getComponent(PositionComponent.class);
//
//            if (entityHitbox != null && entityPos != null) {
//                Rectangle entityBounds = new Rectangle(
//                    entityPos.worldPos.x,
//                    entityPos.worldPos.y,
//                    entityHitbox.width,
//                    entityHitbox.height
//                );
//
//                if (playerBounds.overlaps(entityBounds)) {
//                    return entity; // Collision détectée.
//                }
//            }
//        }
//
//        return null; // Aucune collision.
//    }
//
//    /**
//     * Empêche le joueur d'avancer en cas de collision.
//     *
//     * @param player         Le joueur.
//     * @param movementVector Le vecteur de mouvement initial.
//     * @param collidingEntity L'entité en collision avec le joueur.
//     */
//    public void resolveCollision(Character player, Vector2 movementVector, Character collidingEntity) {
//        if (collidingEntity == null) {
//            return; // Pas de collision, pas besoin d'agir.
//        }
//
//        PositionComponent playerPos = player.getComponent(PositionComponent.class);
//
//        // Réduire le mouvement du joueur en cas de collision.
//        playerPos.worldPos.x -= movementVector.x;
//        playerPos.worldPos.y -= movementVector.y;
//    }
//}


package project.project.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import project.project.components.HitboxComponent;
import project.project.components.PositionComponent;
import project.project.entities.Character;

import java.util.List;

public class CollisionSystem {

    /**
     * Vérifie les collisions entre le joueur et une liste d'entités.
     *
     * @param player   Le joueur (avec une hitbox).
     * @param entities La liste des entités dans le jeu.
     * @return La première entité avec laquelle le joueur est en collision, ou null si aucune collision.
     */
    public Character checkCollision(Character player, List<Character> entities) {
        HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
        PositionComponent playerPos = player.getComponent(PositionComponent.class);

        if (playerHitbox == null || playerPos == null) {
            return null; // Le joueur n'a pas de hitbox ou de position.
        }

        // Calculer la hitbox actuelle du joueur.
        Rectangle playerBounds = new Rectangle(
            playerPos.worldPos.x,
            playerPos.worldPos.y,
            playerHitbox.width,
            playerHitbox.height
        );

        // Vérifier les collisions avec les entités.
        for (Character entity : entities) {
            HitboxComponent entityHitbox = entity.getComponent(HitboxComponent.class);
            PositionComponent entityPos = entity.getComponent(PositionComponent.class);

            if (entityHitbox != null && entityPos != null) {
                Rectangle entityBounds = new Rectangle(
                    entityPos.worldPos.x,
                    entityPos.worldPos.y,
                    entityHitbox.width,
                    entityHitbox.height
                );

                if (playerBounds.overlaps(entityBounds)) {
                    return entity; // Collision détectée.
                }
            }
        }

        return null; // Aucune collision.
    }

    /**
     * Empêche le joueur d'avancer en cas de collision.
     *
     * @param player         Le joueur.
     * @param movementVector Le vecteur de mouvement initial.
     * @param collidingEntity L'entité en collision avec le joueur.
     */
    public void resolveCollision(Character player, Vector2 movementVector, Character collidingEntity) {
        if (collidingEntity == null) {
            return; // Pas de collision, pas besoin d'agir.
        }

        PositionComponent playerPos = player.getComponent(PositionComponent.class);

        // Annule le mouvement en cas de collision.
        if (playerPos != null) {
            playerPos.worldPos.x -= movementVector.x;
            playerPos.worldPos.y -= movementVector.y;
        }
    }
}
