package src.game.constants;

public class WorldConfig {
    // Noise Scale Factors
    public static final double SIMPLEX_NOISE_SCALE_BASE = 0.005;
    public static final double SIMPLEX_NOISE_SCALE_DETAIL = 0.05;

    // Noise Thresholds for Biomes
    public static final double NOISE_THRESHOLD_GRASS = 0.3;
    public static final double NOISE_THRESHOLD_FOREST = 0.1;
    public static final double NOISE_THRESHOLD_SAND = -0.05;

    // Island Generation
    public static final double ISLAND_RADIUS = 10000.0; // Adjust for island size
    public static final double RIVER_THRESHOLD = 0.2;
    public static final double LAKE_THRESHOLD = 0.3;
}
