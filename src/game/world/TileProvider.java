package src.game.world;

import src.game.constants.Config;
import src.game.bin.LRUCache;

import java.util.concurrent.*;
import java.util.*;

public class TileProvider {
    private static final int TILE_SIZE = Config.TILE_SIZE;
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
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;
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

        Double noiseValue = noiseCache.get(key);
        if (noiseValue == null) {
            double baseNoise = noiseGenerator.noise(x * Config.SIMPLEX_NOISE_SCALE_BASE, y * Config.SIMPLEX_NOISE_SCALE_BASE);
            double detailNoise = noiseGenerator.noise(x * Config.SIMPLEX_NOISE_SCALE_DETAIL, y * Config.SIMPLEX_NOISE_SCALE_DETAIL) * 0.2;
            noiseValue = baseNoise + detailNoise;
            noiseCache.put(key, noiseValue);
        }

        Tile tile;
        if (noiseValue > Config.NOISE_THRESHOLD_GRASS) {
            tile = new Tile("grass");
        } else if (noiseValue > Config.NOISE_THRESHOLD_FOREST) {
            tile = new Tile("forest");
        } else if (noiseValue > Config.NOISE_THRESHOLD_SAND) {
            tile = new Tile("sand");
        } else {
            tile = new Tile("water");
        }

        if (tile.type.equals("forest") && Math.random() < Config.OBSTACLE_PROBABILITY_FOREST) {
            tile.isObstacle = true;
        }

        return tile;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
