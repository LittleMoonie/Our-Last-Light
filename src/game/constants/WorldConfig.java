package src.game.constants;

public class WorldConfig {
    public static final int WORLD_WIDTH = 5000;    // Total world width
    public static final int WORLD_HEIGHT = 5000;   // Total world height
    public static final int CHUNK_SIZE = 16;
    public static final int ISLAND_RADIUS = 1500;  // Half of 3000 (diameter of the island area)
    public static final int OCEAN_RADIUS = 2500;   // Half of 5000 (diameter of the entire circular map)
    public static final double SIMPLEX_NOISE_SCALE_BASE = 0.01;

    // Noise thresholds for different biomes
    public static final double NOISE_THRESHOLD_FOREST = 0.7;
    public static final double NOISE_THRESHOLD_GRASS = 0.5;
    public static final double NOISE_THRESHOLD_SAND = 0.3;
}
