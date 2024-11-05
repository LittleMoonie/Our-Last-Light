package src.game.world;

import src.game.constants.Config;
import src.game.constants.WorldConfig;
import src.game.bin.LRUCache;

import java.util.concurrent.*;
import java.util.*;

public class TileProvider {
    private static final int TILE_WIDTH = Config.TILE_WIDTH;
    private static final int MAX_CACHE_SIZE = 10000;

    private Map<Long, Tile> tileMap;
    private Map<Long, Double> noiseCache;
    private SimplexNoise noiseGenerator;
    private ExecutorService executor;
    private Set<Long> tilesBeingGenerated;

    public TileProvider(long seed) {
        tileMap = Collections.synchronizedMap(new LRUCache<>(MAX_CACHE_SIZE));
        noiseCache = Collections.synchronizedMap(new LRUCache<>(MAX_CACHE_SIZE));
        noiseGenerator = new SimplexNoise(seed);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        tilesBeingGenerated = ConcurrentHashMap.newKeySet();
    }

    public boolean isLoadingTiles() {
        return !tilesBeingGenerated.isEmpty();
    }

    public Tile getTileAt(int x, int y, boolean immediate) {
        int tileX = x / TILE_WIDTH;
        int tileY = y / TILE_WIDTH;
        long key = generateKey(tileX, tileY);

        Tile tile = tileMap.get(key);
        if (tile != null) {
            return tile;
        } else {
            if (immediate) {
                tile = generateTile(tileX, tileY);
                tileMap.put(key, tile);
                return tile;
            } else {
                if (!tilesBeingGenerated.contains(key)) {
                    tilesBeingGenerated.add(key);
                    executor.submit(() -> {
                        Tile newTile = generateTile(tileX, tileY);
                        tileMap.put(key, newTile);
                        tilesBeingGenerated.remove(key);
                    });
                }
                return null;
            }
        }
    }

    private long generateKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    private Tile generateTile(int x, int y) {
        long key = generateKey(x, y);

        // Calculate distance from the center to create an island shape
        double distanceFromCenter = Math.sqrt(x * x + y * y);
        double islandEffect = Math.max(0, WorldConfig.ISLAND_RADIUS - distanceFromCenter) / WorldConfig.ISLAND_RADIUS;

        // Use Simplex noise to add variety but blend it with the island effect
        double noiseValue = (noiseGenerator.noise(x * WorldConfig.SIMPLEX_NOISE_SCALE_BASE, y * WorldConfig.SIMPLEX_NOISE_SCALE_BASE) * 0.5 + 0.5) * islandEffect;

        Tile tile;
        if (noiseValue > WorldConfig.NOISE_THRESHOLD_GRASS) {
            tile = new Tile("grass");
        } else if (noiseValue > WorldConfig.NOISE_THRESHOLD_FOREST) {
            tile = new Tile("forest");
        } else if (noiseValue > WorldConfig.NOISE_THRESHOLD_SAND) {
            tile = new Tile("sand");
        } else {
            tile = new Tile("water");
        }

        if (tile.type.equals("grass") || tile.type.equals("sand")) {
            double detailNoise = noiseGenerator.noise(x * WorldConfig.SIMPLEX_NOISE_SCALE_DETAIL, y * WorldConfig.SIMPLEX_NOISE_SCALE_DETAIL);
            if (detailNoise < WorldConfig.RIVER_THRESHOLD) {
                tile = new Tile("river");
            } else if (detailNoise < WorldConfig.LAKE_THRESHOLD) {
                tile = new Tile("lake");
            }
        }

        return tile;
    }

    // Helper method to generate biome-specific tile types
    private Tile generateBiomeTile(int x, int y, String biome) {
        switch (biome) {
            case "prairie":
                return new Tile("grass");
            case "desert":
                return new Tile("sand");
            case "forest":
                return new Tile("forest");
            default:
                return new Tile("grass");
        }
    }


    public void shutdown() {
        executor.shutdown();
    }
}
