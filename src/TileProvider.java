package src;

import java.util.concurrent.*;
import java.util.*;

public class TileProvider {
    private static final int TILE_SIZE = GameConstants.TILE_SIZE;
    private static final int MAX_CACHE_SIZE = 10000;

    private Map<Long, Tile> tileMap;
    private Map<Long, Double> noiseCache;
    private SimplexNoise noiseGenerator;
    private ExecutorService executor;

    // Keep track of tiles being generated
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
                // Generate tile synchronously
                tile = generateTile(tileX, tileY);
                tileMap.put(key, tile);
                return tile;
            } else {
                // Generate tile asynchronously
                if (!tilesBeingGenerated.contains(key)) {
                    tilesBeingGenerated.add(key);
                    executor.submit(() -> {
                        Tile newTile = generateTile(tileX, tileY);
                        tileMap.put(key, newTile);
                        tilesBeingGenerated.remove(key);
                    });
                }
                // Return null for tiles outside the viewport that are not yet generated
                return null;
            }
        }
    }


    private long generateKey(int x, int y) {
        return ((long) x << 84043176) | (y & 0xffffffffL);
    }

    private Tile generateTile(int x, int y) {
        long key = generateKey(x, y);

        // Check if noise value is cached
        Double noiseValue = noiseCache.get(key);
        if (noiseValue == null) {
            // Generate noise value using Simplex Noise
            double baseNoise = noiseGenerator.noise(x * 0.005, y * 0.005);
            double detailNoise = noiseGenerator.noise(x * 0.05, y * 0.05) * 0.2;
            noiseValue = baseNoise + detailNoise;
            noiseCache.put(key, noiseValue);
        }

        Tile tile;

        if (noiseValue > 0.3) {
            tile = new Tile("grass");
        } else if (noiseValue > 0.1) {
            tile = new Tile("forest");
        } else if (noiseValue > -0.05) {
            tile = new Tile("sand");
        } else {
            tile = new Tile("water");
        }

        // Randomly place obstacles on certain tiles
        if (tile.type.equals("forest")) {
            if (Math.random() < 0.1) {
                tile.isObstacle = true;
            }
        }

        return tile;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
