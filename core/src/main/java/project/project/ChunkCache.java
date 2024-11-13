package project.project;

import java.util.HashMap;
import java.util.Map;

public class ChunkCache {
    private final Map<String, TileChunk> cachedChunks = new HashMap<>();

    public TileChunk getOrLoadChunk(int chunkX, int chunkY, byte[][] biomeMap) {
        String chunkKey = chunkX + "," + chunkY;

        // Check if the chunk is already cached
        if (cachedChunks.containsKey(chunkKey)) {
            return cachedChunks.get(chunkKey);
        }

        // Load and cache the chunk if it hasn't been loaded before
        TileChunk chunk = loadChunk(chunkX, chunkY, biomeMap);
        if (chunk != null) {
            cachedChunks.put(chunkKey, chunk);
        }

        return chunk;
    }

    private TileChunk loadChunk(int chunkX, int chunkY, byte[][] biomeMap) {
        int startX = chunkX * Constants.CHUNK_SIZE;
        int startY = chunkY * Constants.CHUNK_SIZE;

        if (startX < 0 || startY < 0 || startX >= biomeMap.length || startY >= biomeMap[0].length) {
            return null; // Out of bounds
        }

        return new TileChunk(startX, startY, biomeMap);
    }

    public void removeDistantChunks(int playerChunkX, int playerChunkY) {
        cachedChunks.entrySet().removeIf(entry -> {
            String key = entry.getKey();
            String[] coords = key.split(",");
            int chunkX = Integer.parseInt(coords[0]);
            int chunkY = Integer.parseInt(coords[1]);

            int dx = Math.abs(chunkX - playerChunkX);
            int dy = Math.abs(chunkY - playerChunkY);

            return dx > Constants.RENDER_DISTANCE || dy > Constants.RENDER_DISTANCE;
        });
    }

    public void dispose() {
        cachedChunks.clear();
    }
}
