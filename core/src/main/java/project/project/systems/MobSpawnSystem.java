package project.project.systems;

import com.badlogic.gdx.math.Vector2;
import project.project.entities.enemies.Mob;
import project.project.Constants;

import java.util.ArrayList;
import java.util.List;

public class MobSpawnSystem {
    private final List<Mob> mobs = new ArrayList<>();
    private static final float MIN_STOP_DISTANCE = Constants.TILE_WIDTH; // Distance minimale autour du joueur
    private static final float MAX_STOP_DISTANCE = Constants.TILE_WIDTH * 5; // Distance maximale autour du joueur
    private float SPEED;

    /**
     * Spawns multiple mobs at random positions within the range of the player.
     *
     * @param playerPosition Position du joueur en pixels.
     * @param numMobs        Nombre de mobs à générer.
     */
    public void spawnMobs(Vector2 playerPosition, int numMobs) {
        int spawnedMobs = 0;

        while (spawnedMobs < numMobs) {
            // Générer une distance aléatoire (entre le min et le max) et un angle
            float stopDistance = MIN_STOP_DISTANCE + (float) Math.random() * (MAX_STOP_DISTANCE - MIN_STOP_DISTANCE);
            float angle = (float) (Math.random() * 2 * Math.PI);

            // Calculer une position cible autour du joueur
            float targetX = playerPosition.x + (float) Math.cos(angle) * stopDistance;
            float targetY = playerPosition.y + (float) Math.sin(angle) * stopDistance;

            Vector2 targetPosition = new Vector2(targetX, targetY);

            // Vérifier si la position est valide (pas trop proche d'autres mobs)
            if (isPositionValid(targetPosition)) {
                Mob mob = new Mob(playerPosition.cpy()); // Spawn initial à la position du joueur
                this.SPEED = mob.getSpeed(mob);
                mob.setRandomTargetAroundPlayer(playerPosition, 5); // Assigner la position cible
                mobs.add(mob);
                spawnedMobs++;

                System.out.println("Mob spawned at: " + mob.getPosition() + " with target: " + targetPosition);
            }
        }
    }

    /**
     * Update les positions des mobs pour qu'ils se déplacent vers leurs cibles.
     *
     * @param deltaTime Temps écoulé depuis le dernier frame.
     */
    public void updateMobs(float deltaTime) {
        for (Mob mob : mobs) {
            Vector2 currentPosition = mob.getPosition();
            Vector2 targetPosition = mob.getTargetPosition();

            if (currentPosition.epsilonEquals(targetPosition, 5f)) {
                // Mob a atteint sa cible
                continue;
            }

            // Calculer la direction vers la cible
            Vector2 direction = new Vector2(targetPosition).sub(currentPosition).nor();

            // Calculer la nouvelle position
            Vector2 newPosition = new Vector2(currentPosition).add(direction.scl(SPEED * deltaTime));
            mob.setPosition(newPosition);
        }
    }

    /**
     * Vérifie si une position cible est valide (pas trop proche d'autres mobs).
     *
     * @param position Position à vérifier.
     * @return True si valide, False sinon.
     */
    private boolean isPositionValid(Vector2 position) {
        for (Mob mob : mobs) {
            if (mob.getPosition().dst(position) < Constants.TILE_WIDTH / 2f) {
                return false; // Trop proche d'un autre mob
            }
        }
        return true;
    }

    /**
     * Retourne la liste actuelle des mobs.
     *
     * @return Liste des mobs.
     */
    public List<Mob> getMobs() {
        return mobs;
    }

    /**
     * Supprime les mobs morts de la liste.
     */
    public void removeDeadMobs() {
        mobs.removeIf(mob -> !mob.isAlive());
    }
}
