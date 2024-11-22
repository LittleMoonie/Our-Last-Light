//package project.project.systems;
//
//import project.project.entities.enemies.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class MobSpawnSystem {
//    private final List<Mob> mobs = new ArrayList<>();
//    private final Random random = new Random();
//
//    public List<Mob> spawnMobs(int count, int health, int minDamage, int maxDamage) {
//        for (int i = 0; i < count; i++) {
//            int damage = random.nextInt(maxDamage - minDamage + 1) + minDamage;
//            mobs.add(new Mob(health, damage));
//        }
//        return mobs;
//    }
//
//    public List<Mob> getMobs() {
//        return mobs;
//    }
//
//    public void removeDeadMobs() {
//        mobs.removeIf(mob -> !mob.isAlive());
//    }
//}
//
//package project.project.systems;
//
//import project.project.entities.enemies.Mob;
//import project.project.utils.RandomUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MobSpawnSystem {
//    private final List<Mob> mobs = new ArrayList<>();
//    private float spawnTimer = 0; // Timer for spawning mobs
//    private float spawnInterval = 2; // Time (in seconds) between spawns
//
//    public void update(float delta) {
//        // Update spawn timer
//        spawnTimer += delta;
//
//        // Spawn a new mob if timer exceeds the interval
//        if (spawnTimer >= spawnInterval) {
//            spawnTimer -= spawnInterval; // Reset timer
//            spawnMob(); // Spawn a new mob
//        }
//
//        // Update existing mobs (e.g., movement, AI, etc.)
//        for (Mob mob : mobs) {
//            mob.update(delta);
//        }
//    }
//
//    protected void spawnMob() {
//        // Logic to spawn a new mob
//        Object RandomUtils;
//        float health = RandomUtils.randomFloat(50, 150); // Random health
//        float damage = RandomUtils.randomFloat(5, 15); // Random damage
//        Mob mob = new Mob(health, damage);
//
//        // Add the mob to the list
//        mobs.add(mob);
//
//        System.out.println("Mob spawned! Total mobs: " + mobs.size());
//    }
//
//    public List<Mob> getMobs() {
//        return mobs;
//    }
//
//    public void clearMobs() {
//        mobs.clear(); // Clear mobs when a wave ends
//    }
//}

//
//// MobSpawnSystem.java
//package project.project.systems;
//
//import project.project.entities.Player;
//import project.project.entities.enemies.Mob;
//import project.project.utils.RandomUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MobSpawnSystem {
//    private final List<Mob> mobs = new ArrayList<>();
//    private float spawnTimer = 0; // Timer for spawning mobs
//    private float spawnInterval = 2; // Time (in seconds) between spawns
//
//    public void update(float delta, Player player) {
//        // Update spawn timer
//        spawnTimer += delta;
//
//        // Spawn a new mob if timer exceeds the interval
//        if (spawnTimer >= spawnInterval) {
//            spawnTimer -= spawnInterval; // Reset timer
//            spawnMob(); // Spawn a new mob
//        }
//
//        // Update existing mobs (e.g., movement, AI, etc.)
//        for (Mob mob : mobs) {
//            mob.update(delta, player);
//        }
//    }
//
//    protected void spawnMob() {
//        float health = RandomUtils.randomFloat(50, 150); // Random health
//        float damage = RandomUtils.randomFloat(5, 15); // Random damage
//        Mob mob = new Mob((int) health, (int) damage);
//
//        // Add the mob to the list
//        mobs.add(mob);
//
//        System.out.println("Mob spawned! Total mobs: " + mobs.size());
//    }
//
//    public List<Mob> getMobs() {
//        return mobs;
//    }
//
//    public void clearMobs() {
//        mobs.clear(); // Clear mobs when a wave ends
//    }
//}

//
//package project.project.systems;
//
//import project.project.entities.Player;
//import project.project.entities.enemies.Mob;
//import project.project.utils.RandomUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MobSpawnSystem {
//    private final List<Mob> mobs = new ArrayList<>();
//    private float spawnTimer = 0; // Timer for spawning mobs
//    private float spawnInterval = 2; // Time (in seconds) between spawns
//
//    private final int maxMobs = 20; // Maximum number of mobs allowed at the same time
//
//    public void update(float delta, Player player) {
//        // Update spawn timer
//        spawnTimer += delta;
//
//        // Spawn a new mob if timer exceeds the interval and we haven't reached the max mob limit
//        if (spawnTimer >= spawnInterval && mobs.size() < maxMobs) {
//            spawnTimer -= spawnInterval; // Reset timer
//            spawnMob(); // Spawn a new mob
//        }
//
//        // Update existing mobs (e.g., movement, AI, etc.)
//        for (Mob mob : mobs) {
//            mob.update(delta, player);
//        }
//    }
//
//    protected void spawnMob() {
//        float health = RandomUtils.randomFloat(50, 150); // Random health
//        float damage = RandomUtils.randomFloat(5, 15); // Random damage
//        Mob mob = new Mob((int) health, (int) damage);
//
//        // Add the mob to the list if we haven't reached the max number of mobs
//        if (mobs.size() < maxMobs) {
//            mobs.add(mob);
//            System.out.println("Mob spawned! Total mobs: " + mobs.size());
//        } else {
//            System.out.println("Max mobs limit reached. No more mobs can be spawned.");
//        }
//    }
//
//    public List<Mob> getMobs() {
//        return mobs;
//    }
//
//    public void clearMobs() {
//        mobs.clear(); // Clear mobs when a wave ends
//    }
//}
//
//
//// MobSpawnSystem.java
//package project.project.systems;
//
//import com.badlogic.gdx.math.Vector2;
//import project.project.entities.Player;
//import project.project.entities.enemies.Mob;
//import project.project.utils.RandomUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MobSpawnSystem {
//    private final List<Mob> mobs = new ArrayList<>();
//    private float spawnTimer = 0; // Timer for spawning mobs
//    private float spawnInterval = 2; // Time (in seconds) between spawns
//
//    private final int maxMobs = 20; // Maximum number of mobs allowed at the same time
//
//    public void update(float delta, Player player) {
//        // Update spawn timer
//        spawnTimer += delta;
//
//        // Spawn a new mob if timer exceeds the interval and we haven't reached the max mob limit
//        if (spawnTimer >= spawnInterval && mobs.size() < maxMobs) {
//            spawnTimer -= spawnInterval; // Reset timer
//            spawnMob(player); // Spawn a new mob around the player
//        }
//
//        // Update existing mobs (e.g., movement, AI, etc.)
//        for (Mob mob : mobs) {
//            mob.update(delta, player);
//        }
//    }
//
//    public void spawnMob(Player player) {
//        float health = RandomUtils.randomFloat(50, 150); // Random health
//        float damage = RandomUtils.randomFloat(5, 15); // Random damage
//        Mob mob = new Mob((int) health, (int) damage);
//
//        // Position the mob 5 tiles away from the player
//        Vector2 playerPosition = player.getWorldPosition();
//        Vector2 spawnPosition = playerPosition.cpy().add(5, 0); // Adjust the direction as needed
//        mob.setWorldPosition(spawnPosition.x, spawnPosition.y);
//
//        // Add the mob to the list if we haven't reached the max number of mobs
//        if (mobs.size() < maxMobs) {
//            mobs.add(mob);
//            System.out.println("Mob spawned! Total mobs: " + mobs.size());
//        } else {
//            System.out.println("Max mobs limit reached. No more mobs can be spawned.");
//        }
//    }
//
//    public List<Mob> getMobs() {
//        return mobs;
//    }
//
//    public void clearMobs() {
//        mobs.clear(); // Clear mobs when a wave ends
//    }
//}

//
//
//// MobSpawnSystem.java
//package project.project.systems;
//
//import com.badlogic.gdx.math.Vector2;
//import project.project.entities.Player;
//import project.project.entities.enemies.Mob;
//import project.project.utils.RandomUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MobSpawnSystem {
//    private final List<Mob> mobs = new ArrayList<>();
//    private float spawnTimer = 0; // Timer for spawning mobs
//    private float spawnInterval = 2; // Time (in seconds) between spawns
//
//    private final int maxMobs = 20; // Maximum number of mobs allowed at the same time
//
//    public void update(float delta, Player player) {
//        // Update spawn timer
//        spawnTimer += delta;
//
//        // Spawn a new mob if timer exceeds the interval and we haven't reached the max mob limit
//        if (spawnTimer >= spawnInterval && mobs.size() < maxMobs) {
//            spawnTimer -= spawnInterval; // Reset timer
//            spawnMob(player); // Spawn a new mob around the player
//        }
//
//        // Update existing mobs (e.g., movement, AI, etc.)
//        for (Mob mob : mobs) {
//            mob.update(delta, player);
//        }
//    }
//
////    public void spawnMob(Player player) {
////        float health = RandomUtils.randomFloat(50, 150); // Random health
////        float damage = RandomUtils.randomFloat(5, 15); // Random damage
////        Mob mob = new Mob((int) health, (int) damage);
////
////        // Position the mob 5 tiles away from the player
////        Vector2 playerPosition = player.getWorldPosition();
////        Vector2 spawnPosition = playerPosition.cpy().add(5, 0); // Adjust the direction as needed
////        mob.setWorldPosition(spawnPosition.x, spawnPosition.y);
////
////        // Add the mob to the list if we haven't reached the max number of mobs
////        if (mobs.size() < maxMobs) {
////            mobs.add(mob);
////            System.out.println("Mob spawned! Total mobs: " + mobs.size());
////        } else {
////            System.out.println("Max mobs limit reached. No more mobs can be spawned.");
////        }
////    }
//
////    public void spawnMob(Player player) {
////        float health = RandomUtils.randomFloat(50, 150); // Random health
////        float damage = RandomUtils.randomFloat(5, 15); // Random damage
////        Mob mob = new Mob((int) health, (int) damage);
////
////        // Position the mob 5 tiles away from the player
////        Vector2 playerPosition = player.getWorldPosition();
////        Vector2 spawnPosition = playerPosition.cpy().add(5, 0); // Adjust the direction as needed
////        mob.setWorldPosition(spawnPosition.x, spawnPosition.y);
////
////        // Add the mob to the list if we haven't reached the max number of mobs
////        if (mobs.size() < maxMobs) {
////            mobs.add(mob);
////            System.out.println("Mob spawned! Total mobs: " + mobs.size());
////        } else {
////            System.out.println("Max mobs limit reached. No more mobs can be spawned.");
////        }
////    }
//
//    public void spawnMob(Player player) {
//        float health = RandomUtils.randomFloat(50, 150); // Random health
//        float damage = RandomUtils.randomFloat(5, 15); // Random damage
//        Mob mob = new Mob((int) health, (int) damage);
//
//        // Position the mob 5 tiles away from the player
//        Vector2 playerPosition = player.getWorldPosition();
//        Vector2 spawnPosition = playerPosition.cpy().add(5, 0); // Adjust the direction as needed
//        mob.setWorldPosition(spawnPosition.x, spawnPosition.y);
//
//        // Add the mob to the list
//        mobs.add(mob);
//        System.out.println("Mob spawned! Total mobs: " + mobs.size());
//    }
//
//    public List<Mob> getMobs() {
//        return mobs;
//    }
//
//    public void clearMobs() {
//        mobs.clear(); // Clear mobs when a wave ends
//    }
//}



// MobSpawnSystem.java
package project.project.systems;

import com.badlogic.gdx.math.Vector2;
import project.project.entities.Player;
import project.project.entities.enemies.Mob;
import project.project.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
public class MobSpawnSystem {
    private List<Mob> mobs = new ArrayList<>();

    public void spawnMob(Vector2 playerPosition) {
        mobs.add(new Mob(playerPosition));
    }

    public List<Mob> getMobs() {
        return mobs;
    }

    public void update(float delta, Vector2 playerPosition) {
        // Tu peux ajouter un intervalle pour faire apparaître des mobs à intervalle régulier
        spawnMob(playerPosition);
    }
}
