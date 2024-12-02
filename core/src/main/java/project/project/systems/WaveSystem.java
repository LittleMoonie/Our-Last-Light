//package project.project.systems;
//
//import project.project.entities.enemies.*;
//import project.project.components.WaveComponent;
//
//import java.util.List;
//
//public class WaveSystem {
//    private final WaveComponent waveComponent;
//    private final MobSpawnSystem mobSpawnSystem;
//
//    public WaveSystem(int initialWave, int mobsPerWave, MobSpawnSystem mobSpawnSystem) {
//        this.waveComponent = new WaveComponent(initialWave, mobsPerWave);
//        this.mobSpawnSystem = mobSpawnSystem;
//    }
//
//    public void startNextWave(int mobHealth, int mobMinDamage, int mobMaxDamage) {
//        waveComponent.incrementWave();
//        int mobsToSpawn = waveComponent.getMobsRemaining();
//        mobSpawnSystem.spawnMobs(mobsToSpawn, mobHealth, mobMinDamage, mobMaxDamage);
//    }
//
//    public void updateWave() {
//        mobSpawnSystem.removeDeadMobs();
//        int mobsRemaining = mobSpawnSystem.getMobs().size();
//        waveComponent.setMobsRemaining(mobsRemaining);
//
//        if (mobsRemaining == 0) {
//            System.out.println("Wave " + waveComponent.getCurrentWave() + " cleared!");
//            startNextWave(100, 5, 15); // Exemple pour la vague suivante
//        }
//    }
//}

//

//import project.project.entities.Player;
//import project.project.entities.enemies.Mob;
//
//import java.util.Collections;

//// WaveSystem.java
//package project.project.systems;
//
//import project.project.entities.Player;
//
//public class WaveSystem {
//    private int currentWave;
//    private int mobsPerWave;
//    private float timeBetweenWaves;
//    private float waveTimer = 0; // Timer for next wave
//    private final MobSpawnSystem mobSpawnSystem;
//    private Player player;
//
//    private boolean waveActive = false; // Is the wave currently active?
//
//    public WaveSystem(int initialWave, int mobsPerWave, MobSpawnSystem mobSpawnSystem, Player player) {
//        this.currentWave = initialWave;
//        this.mobsPerWave = mobsPerWave;
//        this.timeBetweenWaves = 10; // Default time between waves
//        this.mobSpawnSystem = mobSpawnSystem;
//        this.player = player;
//    }
//
//    public void update(float delta) {
//        if (waveActive) {
//            // Update the MobSpawnSystem while the wave is active
//            mobSpawnSystem.update(delta, player);
//
//            // Check if all mobs are defeated to end the wave
//            if (mobSpawnSystem.getMobs().isEmpty()) {
//                waveActive = false; // End the wave
//                waveTimer = 0; // Reset the timer for the next wave
//                System.out.println("Wave " + currentWave + " completed!");
//            }
//        } else {
//            // Increment timer to wait for the next wave
//            waveTimer += delta;
//
//            if (waveTimer >= timeBetweenWaves) {
//                startNextWave(100, 5, 15); // Default health/damage for new mobs
//            }
//        }
//    }
//
//    public void startNextWave(float health, float minDamage, float maxDamage) {
//        currentWave++;
//        waveActive = true;
//
//        // Clear previous mobs and prepare the next wave
//        mobSpawnSystem.clearMobs();
//
//        for (int i = 0; i < mobsPerWave; i++) {
//            mobSpawnSystem.spawnMob(); // Spawn mobs for the wave
//        }
//
//        System.out.println("Wave " + currentWave + " started with " + mobsPerWave + " mobs!");
//    }
//}

//
//package project.project.systems;
//
//import project.project.entities.Player;
//
//public class WaveSystem {
//    private int currentWave;
//    private int mobsPerWave;
//    private float timeBetweenWaves;
//    private float waveTimer = 0; // Timer for next wave
//    private final MobSpawnSystem mobSpawnSystem;
//    private Player player;
//
//    private boolean waveActive = false; // Is the wave currently active?
//
//    public WaveSystem(int initialWave, int mobsPerWave, MobSpawnSystem mobSpawnSystem, Player player) {
//        this.currentWave = initialWave;
//        this.mobsPerWave = mobsPerWave;
//        this.timeBetweenWaves = 10; // Default time between waves
//        this.mobSpawnSystem = mobSpawnSystem;
//        this.player = player;
//    }
//
//    public void update(float delta) {
//        if (waveActive) {
//            // Update the MobSpawnSystem while the wave is active
//            mobSpawnSystem.update(delta, player);
//
//            // Check if all mobs are defeated to end the wave
//            if (mobSpawnSystem.getMobs().isEmpty()) {
//                waveActive = false; // End the wave
//                waveTimer = 0; // Reset the timer for the next wave
//                System.out.println("Wave " + currentWave + " completed!");
//            }
//        } else {
//            // Increment timer to wait for the next wave
//            waveTimer += delta;
//
//            if (waveTimer >= timeBetweenWaves) {
//                startNextWave(100, 5, 15); // Default health/damage for new mobs
//            }
//        }
//    }
//
//    public void startNextWave(float health, float minDamage, float maxDamage) {
//        currentWave++;
//        waveActive = true;
//
//        // Clear previous mobs and prepare the next wave
//        mobSpawnSystem.clearMobs();
//
//        // Spawn mobs for the wave
//        int totalMobsToSpawn = Math.min(mobsPerWave, 20); // Ensure the mobs don't exceed the max limit
//
//        for (int i = 0; i < totalMobsToSpawn; i++) {
//            mobSpawnSystem.spawnMob(); // Spawn mobs for the wave
//        }
//
//        System.out.println("Wave " + currentWave + " started with " + totalMobsToSpawn + " mobs!");
//    }
//}

//package project.project.systems;
//
//
//import com.badlogic.gdx.math.Vector2;
//import project.project.entities.enemies.Mob;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class WaveSystem {
//
//    private List<Mob> currentWaveMobs = new ArrayList<>();
//    private int currentWave = 0;
//
//    public void spawnWave(int waveNumber) {
//        currentWaveMobs.clear();  // Vider la liste des mobs précédents
//
//        // Exemple : Création des mobs pour la vague
//        for (int i = 0; i < waveNumber * 2; i++) {  // Par exemple, plus de mobs dans les vagues suivantes
//            Mob mob = new Mob(); // Créer un mob
//            mob.setPosition(new Vector2(randomX(), randomY())); // Positionner le mob
//            currentWaveMobs.add(mob);
//        }
//    }
//
//    public void update(float delta, Player player) {
//        // Met à jour la logique de mouvement/attaque pour chaque mob dans la vague
//        for (Mob mob : currentWaveMobs) {
//            if (mob.isAlive()) {
//                mob.update(delta, player.getWorldPosition()); // Faire bouger les mobs vers le joueur
//                if (mob.isInAttackRange(player)) {
//                    player.attack(Collections.singletonList(mob));  // Attaque le joueur si dans la portée
//                }
//            }
//        }
//    }
//
//    public void nextWave() {
//        currentWave++;
//        spawnWave(currentWave);  // Passer à la vague suivante
//    }
//}

