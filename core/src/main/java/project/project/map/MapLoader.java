// MapLoader.java
package project.project.map;

import project.project.Constants;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MapLoader {
    private final MapGenerator mapGenerator;
    private final List<Chunk> loadedChunks;
    private final ConcurrentHashMap<String, Chunk> chunkCache;

    public MapLoader(MapGenerator mapGenerator) {
        this.mapGenerator = mapGenerator;
        this.loadedChunks = new CopyOnWriteArrayList<>();
        this.chunkCache = new ConcurrentHashMap<>();
    }

    public void update(float cameraX, float cameraY) {
        int chunkSize = Constants.CHUNK_SIZE;
        float renderDistance = Constants.RENDER_DISTANCE;

        int startX = (int) (cameraX / chunkSize) - (int) renderDistance;
        int endX = (int) (cameraX / chunkSize) + (int) renderDistance;
        int startY = (int) (cameraY / chunkSize) -(int) renderDistance;
        int endY = (int) (cameraY / chunkSize) + (int) renderDistance;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (!isChunkLoaded(x, y)) {
                    Chunk.loadAndAddChunk(loadedChunks, x, y, chunkSize, mapGenerator.getBiomeMap());
                }
            }
        }

        unloadInvisibleChunks(startX, endX, startY, endY);
    }

    private void unloadInvisibleChunks(int startX, int endX, int startY, int endY) {
        List<Chunk> chunksToUnload = new CopyOnWriteArrayList<>();
        for (Chunk chunk : loadedChunks) {
            int chunkX = chunk.getChunkX();
            int chunkY = chunk.getChunkY();
            if (chunkX < startX || chunkX > endX || chunkY < startY || chunkY > endY) {
                System.out.println("Unloading chunk at: (" + chunkX + ", " + chunkY + ")");
                chunksToUnload.add(chunk);
            }
        }
        loadedChunks.removeAll(chunksToUnload);
    }

    public boolean isChunkLoaded(int chunkX, int chunkY) {
        return loadedChunks.stream().anyMatch(chunk -> chunk.getChunkX() == chunkX && chunk.getChunkY() == chunkY);
    }

    public int getTotalChunks() {
        int mapWidthInChunks = (int) Math.ceil((double) Constants.MAP_WIDTH / Constants.CHUNK_SIZE);
        int mapHeightInChunks = (int) Math.ceil((double) Constants.MAP_HEIGHT / Constants.CHUNK_SIZE);
        return mapWidthInChunks * mapHeightInChunks;
    }

    public List<Chunk> getLoadedChunks() {
        return loadedChunks;
    }

    public void dispose() {
        // Dispose resources if needed
    }
}
