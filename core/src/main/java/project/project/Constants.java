// core/src/main/java/project/project/Constants.java
package project.project;

public class Constants {
    // Map dimensions
    public static final int MAP_WIDTH = 500; // Reduced for testing
    public static final int MAP_HEIGHT = 500; // Reduced for testing

    // Tile dimensions
    public static final int TILE_WIDTH = 256; // Original width of each tile
    public static final int TILE_HEIGHT = 192; // Original height of each tile

    // Chunk settings
    public static final int CHUNK_SIZE = 16;
    public static final int RENDER_DISTANCE = 5; // Reduced for testing

    // Camera settings
    public static final float INITIAL_ZOOM = 0.5f; // Adjusted zoom for better visibility
    public static final float MIN_ZOOM = 0.1f;
    public static final float MAX_ZOOM = 2.0f;
    public static final float ZOOM_SPEED = 0.005f;

    // Player settings
    public static final float PLAYER_MOVE_INTERVAL = 0.2f; // Time between moves
    public static final String PLAYER_TEXTURE = "player1.png";

    // MapGenerator settings
    public static final double MAP_SCALE = 5.0; // Scaling factor for noise

    // Miscellaneous
    public static final String DEBUG_MODE = "true"; // Set to "false" to disable debug mode
}
