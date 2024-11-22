package project.project.map;

import java.util.List;

public class Chunk {
    private final byte[][] chunkData;
    private final int chunkX;
    private final int chunkY;
    private final int chunkSize;

    public Chunk(int chunkX, int chunkY, int chunkSize) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkSize = chunkSize;
        this.chunkData = new byte[chunkSize][chunkSize];
    }

    public byte[][] getChunkData() {
        return chunkData;
    }

    public void loadChunk(int startX, int startY, byte[][] biomeMap) {
        for (int x = 0; x < chunkSize; x++) {
            for (int y = 0; y < chunkSize; y++) {
                if (startX + x < biomeMap.length && startY + y < biomeMap[0].length) {
                    chunkData[x][y] = biomeMap[startX + x][startY + y];
                }
            }
        }
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public static void loadAndAddChunk(List<Chunk> loadedChunks, int chunkX, int chunkY, int chunkSize, byte[][] biomeMap) {
        Chunk newChunk = new Chunk(chunkX, chunkY, chunkSize);
        newChunk.loadChunk(chunkX * chunkSize, chunkY * chunkSize, biomeMap);
        loadedChunks.add(newChunk);
//        System.out.println("Chunk added at: (" + chunkX + ", " + chunkY + ")");
    }

    public static void unloadInvisibleChunks(List<Chunk> loadedChunks, int startX, int endX, int startY, int endY) {
        loadedChunks.removeIf(chunk -> chunk.getChunkX() < startX || chunk.getChunkX() > endX || chunk.getChunkY() < startY || chunk.getChunkY() > endY);
    }
}
