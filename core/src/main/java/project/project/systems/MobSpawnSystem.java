package project.project.systems;

import com.badlogic.gdx.math.Vector2;
import project.project.entities.enemies.Mob;

import java.util.ArrayList;
import java.util.List;

public class MobSpawnSystem {
    private final List<Mob> mobs = new ArrayList<>();
    private static final float SPAWN_DISTANCE = 2; // 2 pixels away from the player

    public void spawnMob(Vector2 playerPosition) {
        // Calculate random direction
        float angle = (float) (Math.random() * 2 * Math.PI); // Random angle in radians
        float offsetX = (float) Math.cos(angle) * SPAWN_DISTANCE;
        float offsetY = (float) Math.sin(angle) * SPAWN_DISTANCE;

        // Spawn position 2px from the player
        Vector2 mobPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y + offsetY);

        // Create and store the new mob
        Mob mob = new Mob(mobPosition);
        mobs.add(mob);

        System.out.println("Mob spawned at coordinates: " + mobPosition);
        System.out.println("Total mobs: " + mobs.size());
    }

    public List<Mob> getMobs() {
        System.out.println("Returning mobs list: " + mobs);
        return mobs;
    }

    public void removeDeadMobs() {
        mobs.removeIf(mob -> !mob.isAlive());
    }
}
