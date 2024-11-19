// core/src/main/java/project/project/Constants.java
package project.project;

public class Constants {

    // Map dimensions
    public static final int MAP_WIDTH = 700; // Reduced for testing
    public static final int MAP_HEIGHT = 700; // Reduced for testing

    // Biome generation settings
    public static final double BIOME_NOISE_SCALE = 0.005; // Adjust to control biome size

    // Biome size constraints (in number of tiles)
    public static final int MIN_BIOME_SIZE = 5 * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE;  // Minimum biome size (5 chunks)
    public static final int MAX_BIOME_SIZE = 10 * Constants.CHUNK_SIZE * Constants.CHUNK_SIZE; // Maximum biome size (10 chunks)

    // Map scale for elevation noise
    public static final double MAP_SCALE = 100.0; // Adjusted for better elevation variation

    // Chunk settings
    public static final int CHUNK_SIZE = 16;
    public static final int RENDER_DISTANCE = 5; // Reduced for testing

    // Tile dimensions
    public static final int TILE_WIDTH = 256; // Original width of each tile
    public static final int TILE_HEIGHT = 192; // Original height of each tile

    // Camera settings
    public static final float INITIAL_ZOOM = 1f; // Adjusted zoom for better visibility
    public static final float MIN_ZOOM = 1f;
    public static final float MAX_ZOOM = 200.0f;
    public static final float ZOOM_SPEED = 1f;

    // Player settings
    public static final float PLAYER_MOVE_INTERVAL = 0.2f; // Time between moves
    public static final String PLAYER_TEXTURE = "player1.png";

    // MapGenerator settings
//    public static final double MAP_SCALE = 5.0; // Scaling factor for noise

    // Miscellaneous
    public static final String DEBUG_MODE = "true"; // Set to "false" to disable debug mode
}
