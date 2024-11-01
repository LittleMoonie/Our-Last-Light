package src.game.world;

import src.game.constants.Config;
import src.game.bin.LRUCache;
import java.io.*;
import java.util.concurrent.*;
import java.util.*;

public class TileProvider {
    private static final int TILE_SIZE = Config.TILE_SIZE;
    private static final int MAX_CACHE_SIZE = 10000;

    private final Map<Long, Tile> tileMap = new ConcurrentHashMap<>();
    private final Map<Long, Double> noiseCache;
    private final SimplexNoise noiseGenerator;
    private final ExecutorService executor;
    private final Set<Long> tilesBeingGenerated;

    public TileProvider(long seed) {
        noiseCache = Collections.synchronizedMap(new LRUCache<>(MAX_CACHE_SIZE));
        noiseGenerator = new SimplexNoise(seed);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        tilesBeingGenerated = ConcurrentHashMap.newKeySet();
    }

    public boolean isLoadingTiles() {
        return !tilesBeingGenerated.isEmpty();
    }

    public Tile getTileAt(int x, int y, boolean spawnable) {
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;
        long key = generateKey(tileX, tileY);

        Tile tile = tileMap.get(key);

        if (tile == null && spawnable) {
            // Only generate a new tile if 'spawnable' is true
            tile = generateTile(tileX, tileY);
            tileMap.put(key, tile);
        }

        return tile; // Returns null if tile doesn't exist and spawnable is false
    }

    private long generateKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    public void saveTileData(String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(tileMap);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadTileData(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            Map<Long, Tile> loadedMap = (Map<Long, Tile>) in.readObject();
            tileMap.clear();
            tileMap.putAll(loadedMap);
        }
    }

    private Tile generateTile(int x, int y) {
        long key = generateKey(x, y);

        Double noiseValue = noiseCache.get(key);
        if (noiseValue == null) {
            double baseNoise = noiseGenerator.noise(x * Config.SIMPLEX_NOISE_SCALE_BASE, y * Config.SIMPLEX_NOISE_SCALE_BASE);
            double detailNoise = noiseGenerator.noise(x * Config.SIMPLEX_NOISE_SCALE_DETAIL, y * Config.SIMPLEX_NOISE_SCALE_DETAIL) * 0.2;
            noiseValue = baseNoise + detailNoise;
            noiseCache.put(key, noiseValue);
        }

        String biome;
        String resource = null;

        // Determine biome based on noise value
        if (noiseValue > Config.NOISE_THRESHOLD_MOUNTAIN) {
            biome = "mountain";  // Assign mountain biome
            // Assign resources to mountains, e.g., stone
            if (Math.random() < Config.OBSTACLE_PROBABILITY_MOUNTAIN) {
                resource = "stone";
            }
        } else if (noiseValue > Config.NOISE_THRESHOLD_GRASS) {
            biome = "grass";  // Assign grass biome
            // Grass does not typically have resources, but you might add flowers or similar
        } else if (noiseValue > Config.NOISE_THRESHOLD_FOREST) {
            biome = "forest";  // Assign forest biome
            // Forests can have trees as resources
            if (Math.random() < Config.OBSTACLE_PROBABILITY_FOREST) {
                resource = "tree";
            }
        } else if (noiseValue > Config.NOISE_THRESHOLD_SAND) {
            biome = "sand";  // Assign sand biome
            // Sands can have stones or shells
            if (Math.random() < Config.OBSTACLE_PROBABILITY_SAND) {
                resource = "stone";  // For example, adding stones on sandy terrain
            }
        } else {
            biome = "water";  // Assign water biome
            // No resources in water
        }

        // Create and return the Tile
        Tile tile = new Tile(biome, resource);
        tileMap.put(key, tile);
        return tile;
    }

    public Tile getTileByKey(Long key) {
        return tileMap.get(key);
    }


    public Set<Long> getAllGeneratedTileKeys() {
        // Return a copy of the key set to avoid external modifications
        return new HashSet<>(tileMap.keySet());
    }
}
