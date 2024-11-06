package src.game.world;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import src.game.constants.Config;
import src.game.constants.WorldConfig;
import src.game.world.Tile;
import src.game.world.TileProvider;

public class World {
    private int chunksGenerated;
    private Set<Point> loadedChunks;
    private int totalChunks;
    private TileProvider tileProvider;

    public World(long seed) {
        tileProvider = new TileProvider(seed);
        loadedChunks = new HashSet<>();
        int chunksInWidth = WorldConfig.WORLD_WIDTH / (WorldConfig.CHUNK_SIZE * Config.TILE_WIDTH);
        int chunksInHeight = WorldConfig.WORLD_HEIGHT / (WorldConfig.CHUNK_SIZE * Config.TILE_HEIGHT);
        totalChunks = chunksInWidth * chunksInHeight;
        chunksGenerated = 0;
    }

    public void generateNextChunk() {
        int centerX = WorldConfig.WORLD_WIDTH / 2;
        int centerY = WorldConfig.WORLD_HEIGHT / 2;

        // Calculate next chunk coordinates in a spiral pattern around the center
        Point nextChunkCoords = getNextChunkCoords();
        if (nextChunkCoords != null) {
            int chunkX = nextChunkCoords.x;
            int chunkY = nextChunkCoords.y;

            // Generate all tiles within this chunk
            for (int x = chunkX * WorldConfig.CHUNK_SIZE; x < (chunkX + 1) * WorldConfig.CHUNK_SIZE; x++) {
                for (int y = chunkY * WorldConfig.CHUNK_SIZE; y < (chunkY + 1) * WorldConfig.CHUNK_SIZE; y++) {
                    tileProvider.getTileAt(x * Config.TILE_WIDTH, y * Config.TILE_HEIGHT, true);
                }
            }

            loadedChunks.add(nextChunkCoords);
            chunksGenerated++;
        }
    }

    private Point getNextChunkCoords() {
        int centerX = WorldConfig.WORLD_WIDTH / (2 * WorldConfig.CHUNK_SIZE);
        int centerY = WorldConfig.WORLD_HEIGHT / (2 * WorldConfig.CHUNK_SIZE);

        int layer = 0;
        int dx = 0, dy = -1;
        int x = centerX, y = centerY;

        for (int i = 0; i < totalChunks; i++) {
            if (x >= 0 && x < centerX * 2 && y >= 0 && y < centerY * 2) {
                Point nextChunk = new Point(x, y);
                if (!loadedChunks.contains(nextChunk)) {
                    return nextChunk;
                }
            }

            if (x == layer && y == -layer) {
                layer++;
                dx = 1;
                dy = 0;
            } else if (x == layer && y == layer) {
                dx = 0;
                dy = 1;
            } else if (x == -layer && y == layer) {
                dx = -1;
                dy = 0;
            } else if (x == -layer && y == -layer) {
                dx = 0;
                dy = -1;
            }

            x += dx;
            y += dy;
        }
        return null; // All chunks have been generated
    }

    public double getGenerationProgress() {
        return (double) chunksGenerated / totalChunks;
    }
    public void render(Graphics2D g2, int screenWidth, int screenHeight) {
        int tileSize = Config.TILE_WIDTH; // Assuming square tiles
        int centerX = WorldConfig.WORLD_WIDTH / 2;
        int centerY = WorldConfig.WORLD_HEIGHT / 2;

        // Calculate the visible range
        int visibleTilesX = screenWidth / tileSize;
        int visibleTilesY = screenHeight / tileSize;

        // Render only visible tiles within chunks
        for (int x = centerX - visibleTilesX / 2; x < centerX + visibleTilesX / 2; x++) {
            for (int y = centerY - visibleTilesY / 2; y < centerY + visibleTilesY / 2; y++) {
                Tile tile = tileProvider.getTileAt(x * tileSize, y * tileSize, true);
                if (tile != null) {
                    // Calculate the screen position for each tile
                    int screenX = (x - centerX) * tileSize + screenWidth / 2;
                    int screenY = (y - centerY) * tileSize + screenHeight / 2;
                    g2.drawImage(tile.getImage(), screenX, screenY, tileSize, tileSize, null);
                }
            }
        }
    }
}
