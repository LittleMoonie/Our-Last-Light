package project.project;

/**
 * Represents a chunk of tiles in the map.
 */
public class TileChunk {
    public static final int CHUNK_SIZE = 16;
    public int startX, startY;
    public int[][] tiles;

    /**
     * Constructs a TileChunk.
     *
     * @param startX    The starting x-coordinate in the biome map.
     * @param startY    The starting y-coordinate in the biome map.
     * @param biomeMap  The biome map to extract tiles from.
     */
    public TileChunk(int startX, int startY, byte[][] biomeMap) {
        this.startX = startX;
        this.startY = startY;
        int sizeX = Math.min(CHUNK_SIZE, biomeMap.length - startX);
        int sizeY = Math.min(CHUNK_SIZE, biomeMap[0].length - startY);
        tiles = new int[sizeX][sizeY];

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                tiles[x][y] = biomeMap[startX + x][startY + y];
            }
        }
    }
}
