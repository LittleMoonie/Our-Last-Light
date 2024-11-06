package src.game.world;

import src.game.constants.Config;
import src.game.constants.WorldConfig;
import src.game.bin.LRUCache;

import java.util.*;
import java.util.concurrent.*;

public class TileProvider {
    private static final int TILE_WIDTH = Config.TILE_WIDTH;
    private static final int MAX_CACHE_SIZE = 10000;

    private Map<Long, Tile> tileMap;
    private SimplexNoise noiseGenerator;
    private ExecutorService executor;
    private Set<Long> tilesBeingGenerated;

    public TileProvider(long seed) {
        tileMap = Collections.synchronizedMap(new LRUCache<>(MAX_CACHE_SIZE));
        noiseGenerator = new SimplexNoise(seed);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        tilesBeingGenerated = ConcurrentHashMap.newKeySet();
    }

    public Tile getTileAt(int x, int y, boolean immediate) {
        int tileX = x / Config.TILE_WIDTH;
        int tileY = y / Config.TILE_HEIGHT;
        long key = generateKey(tileX, tileY);

        Tile tile = tileMap.get(key);
        if (tile != null) {
            return tile;
        } else {
            tile = generateTile(tileX, tileY);
            tileMap.put(key, tile);
            return tile;
        }
    }

    private Tile generateTile(int x, int y) {
        double noiseValue = noiseGenerator.noise(x * WorldConfig.SIMPLEX_NOISE_SCALE_BASE, y * WorldConfig.SIMPLEX_NOISE_SCALE_BASE);

        if (noiseValue > WorldConfig.NOISE_THRESHOLD_FOREST) {
            return new Tile("forest");
        } else if (noiseValue > WorldConfig.NOISE_THRESHOLD_GRASS) {
            return new Tile("grass");
        } else if (noiseValue > WorldConfig.NOISE_THRESHOLD_SAND) {
            return new Tile("sand");
        } else {
            return new Tile("ocean");
        }
    }

    private long generateKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    private Tile generateLandTile(int x, int y) {
        // Generate noise value for the tile's position
        double noiseValue = noiseGenerator.noise(x * WorldConfig.SIMPLEX_NOISE_SCALE_BASE, y * WorldConfig.SIMPLEX_NOISE_SCALE_BASE);

        // Select tile type based on noise value and thresholds
        if (noiseValue > WorldConfig.NOISE_THRESHOLD_FOREST) {
            return new Tile("forest");
        } else if (noiseValue > WorldConfig.NOISE_THRESHOLD_GRASS) {
            return new Tile("grass");
        } else if (noiseValue > WorldConfig.NOISE_THRESHOLD_SAND) {
            return new Tile("sand");
        } else {
            return new Tile("ocean"); // If outside the main island area but still within the ocean radius
        }
    }
}