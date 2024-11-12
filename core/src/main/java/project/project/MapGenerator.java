package project.project;

import com.badlogic.gdx.math.MathUtils;

public class MapGenerator {
    public enum Biome {
        OCEAN,
        BEACH,
        GRASS,
        FOREST,
        DESERT,
        MOUNTAIN,
        PRAIRIE
    }

    private int width;
    private int height;
    private byte[][] biomeMap;

    public MapGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        biomeMap = new byte[width][height];
        generateMap();
    }

    private void generateMap() {
        // Parameters for island shape
        float centerX = width / 2f;
        float centerY = height / 2f;
        float maxDistance = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float distance = (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                float normalizedDistance = distance / maxDistance;

                // Determine biome based on distance
                if (normalizedDistance < 0.3f) {
                    biomeMap[x][y] = (byte) Biome.GRASS.ordinal();
                } else if (normalizedDistance < 0.4f) {
                    biomeMap[x][y] = (byte) Biome.BEACH.ordinal();
                } else if (normalizedDistance < 0.6f) {
                    biomeMap[x][y] = (byte) Biome.FOREST.ordinal();
                } else if (normalizedDistance < 0.7f) {
                    biomeMap[x][y] = (byte) Biome.DESERT.ordinal();
                } else if (normalizedDistance < 0.85f) {
                    biomeMap[x][y] = (byte) Biome.MOUNTAIN.ordinal();
                } else if (normalizedDistance < 0.95f) {
                    biomeMap[x][y] = (byte) Biome.PRAIRIE.ordinal();
                } else {
                    biomeMap[x][y] = (byte) Biome.OCEAN.ordinal();
                }
            }
        }

        // Optionally, add some random variation to make the island more natural
        addRandomNoise();
    }

    private void addRandomNoise() {
        // Simple noise addition to make the island more varied
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float noise = MathUtils.random();
                if (noise < 0.05f) { // 5% chance to change biome
                    biomeMap[x][y] = (byte) MathUtils.random(0, Biome.values().length - 1);
                }
            }
        }
    }

    public byte[][] getBiomeMap() {
        return biomeMap;
    }
}
