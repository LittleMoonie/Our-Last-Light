package project.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IsometricRenderer {
    public static final int TILE_WIDTH = 64;
    public static final int TILE_HEIGHT = 32; // Adjusted for isometric tiles

    public Texture grass;
    public Texture water;
    public Texture sand;
    public Texture forest;
    public Texture prairie;
    public Texture desert;
    public Texture mountain;

    private MapGenerator mapGenerator;
    private byte[][] biomeMap;

    // Chunk management
    private Map<String, TileChunk> loadedChunks = new HashMap<>();
    private int renderDistance = 5; // Number of chunks to render around the player

    // For tracking player's chunk position to manage loaded chunks
    private Vector2 lastPlayerChunkPos = new Vector2(-1, -1);

    public IsometricRenderer(int width, int height) {
        // Load textures
        grass = new Texture(Gdx.files.internal("grass.png"));
        water = new Texture(Gdx.files.internal("water.png"));
        sand = new Texture(Gdx.files.internal("sand.png"));
        forest = new Texture(Gdx.files.internal("forest.png"));
        prairie = new Texture(Gdx.files.internal("prairie.png"));
        desert = new Texture(Gdx.files.internal("desert.png"));
        mountain = new Texture(Gdx.files.internal("mountain.png"));

        // Generate the map
        mapGenerator = new MapGenerator(width, height);
        biomeMap = mapGenerator.getBiomeMap();
    }

    /**
     * Draws the ground tiles around the player's position.
     *
     * @param batch    The SpriteBatch used for drawing.
     * @param playerX  The player's x-coordinate in world space.
     * @param playerY  The player's y-coordinate in world space.
     */
    public void drawGround(SpriteBatch batch, float playerX, float playerY) {
        // Convert player world position to tile coordinates
        Vector2 playerTilePos = worldToIso(playerX, playerY);

        int playerTileX = (int) playerTilePos.x;
        int playerTileY = (int) playerTilePos.y;

        // Convert tile coordinates to chunk coordinates
        int playerChunkX = playerTileX / TileChunk.CHUNK_SIZE;
        int playerChunkY = playerTileY / TileChunk.CHUNK_SIZE;

        // Remove distant chunks if the player has moved to a new chunk
        if (lastPlayerChunkPos.x != playerChunkX || lastPlayerChunkPos.y != playerChunkY) {
            lastPlayerChunkPos.set(playerChunkX, playerChunkY);
            removeDistantChunks(playerChunkX, playerChunkY);
        }

        // Loop through chunks around the player
        for (int dx = -renderDistance; dx <= renderDistance; dx++) {
            for (int dy = -renderDistance; dy <= renderDistance; dy++) {
                int chunkX = playerChunkX + dx;
                int chunkY = playerChunkY + dy;

                String chunkKey = chunkX + "," + chunkY;
                TileChunk chunk = loadedChunks.get(chunkKey);

                if (chunk == null) {
                    chunk = loadChunk(chunkX, chunkY);
                    if (chunk != null) {
                        loadedChunks.put(chunkKey, chunk);
                    }
                }

                if (chunk != null) {
                    drawChunk(batch, chunk);
                }
            }
        }
    }

    /**
     * Loads a chunk at the specified chunk coordinates.
     *
     * @param chunkX The x-coordinate of the chunk.
     * @param chunkY The y-coordinate of the chunk.
     * @return The loaded TileChunk, or null if out of bounds.
     */
    private TileChunk loadChunk(int chunkX, int chunkY) {
        int startX = chunkX * TileChunk.CHUNK_SIZE;
        int startY = chunkY * TileChunk.CHUNK_SIZE;

        if (startX < 0 || startY < 0 || startX >= biomeMap.length || startY >= biomeMap[0].length) {
            return null; // Out of bounds
        }

        return new TileChunk(startX, startY, biomeMap);
    }

    /**
     * Draws a chunk of tiles.
     *
     * @param batch The SpriteBatch used for drawing.
     * @param chunk The TileChunk to draw.
     */
    private void drawChunk(SpriteBatch batch, TileChunk chunk) {
        for (int x = 0; x < chunk.tiles.length; x++) {
            for (int y = 0; y < chunk.tiles[0].length; y++) {
                int globalX = chunk.startX + x;
                int globalY = chunk.startY + y;

                float drawX = (globalX - globalY) * (TILE_WIDTH / 2f);
                float drawY = (globalX + globalY) * (TILE_HEIGHT / 2f) / 2;

                Texture tileTexture = getTextureForBiome(chunk.tiles[x][y]);

                batch.draw(tileTexture, drawX, drawY, TILE_WIDTH, TILE_HEIGHT);
            }
        }
    }

    /**
     * Removes chunks from loadedChunks that are beyond the render distance.
     *
     * @param playerChunkX The player's current chunk x-coordinate.
     * @param playerChunkY The player's current chunk y-coordinate.
     */
    private void removeDistantChunks(int playerChunkX, int playerChunkY) {
        Iterator<Map.Entry<String, TileChunk>> iterator = loadedChunks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TileChunk> entry = iterator.next();
            String key = entry.getKey();
            String[] coords = key.split(",");
            int chunkX = Integer.parseInt(coords[0]);
            int chunkY = Integer.parseInt(coords[1]);

            int dx = Math.abs(chunkX - playerChunkX);
            int dy = Math.abs(chunkY - playerChunkY);

            if (dx > renderDistance || dy > renderDistance) {
                iterator.remove(); // Remove chunk from loadedChunks
            }
        }
    }

    /**
     * Returns the texture associated with a biome.
     *
     * @param biome The biome identifier.
     * @return The corresponding Texture.
     */
    private Texture getTextureForBiome(int biome) {
        switch (biome) {
            case 0: // OCEAN
                return water;
            case 1: // BEACH
                return sand;
            case 2: // GRASS
                return grass;
            case 3: // FOREST
                return forest;
            case 4: // DESERT
                return desert;
            case 5: // MOUNTAIN
                return mountain;
            case 6: // PRAIRIE
                return prairie;
            default:
                return grass;
        }
    }

    /**
     * Helper method to convert world coordinates to isometric tile coordinates.
     *
     * @param worldX The x-coordinate in world space.
     * @param worldY The y-coordinate in world space.
     * @return A Vector2 representing the tile coordinates.
     */
    private Vector2 worldToIso(float worldX, float worldY) {
        float tileX = (worldY / TILE_HEIGHT + worldX / TILE_WIDTH);
        float tileY = (worldY / TILE_HEIGHT - worldX / TILE_WIDTH);
        return new Vector2(tileX, tileY);
    }

    public byte[][] getBiomeMap() {
        return biomeMap;
    }

    /**
     * Disposes of textures to free up memory.
     */
    public void dispose() {
        grass.dispose();
        water.dispose();
        sand.dispose();
        forest.dispose();
        prairie.dispose();
        desert.dispose();
        mountain.dispose();
    }
}
