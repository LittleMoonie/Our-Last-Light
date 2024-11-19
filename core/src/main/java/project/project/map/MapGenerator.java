package project.project.map;

import project.project.noise.IslandNoise;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    private int width;
    private int height;
    private byte[][] biomeMap;
    private int[][] tileVariants;
    private List<ResourcePlacement> resources;

    private IslandNoise islandNoise;
    private Random random;

    public MapGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.biomeMap = new byte[width][height];
        this.tileVariants = new int[width][height];
        this.resources = new ArrayList<>();

        random = new Random();
        int seed = random.nextInt();
        this.islandNoise = new IslandNoise(width, height, seed);

        generateMap();
        placeResources();
    }

    private void generateMap() {
        Biome[][] generatedBiomeMap = islandNoise.generateBiomeMap();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Biome biome = Biome.valueOf(generatedBiomeMap[x][y].name());
                biomeMap[x][y] = (byte) biome.ordinal();
                tileVariants[x][y] = random.nextInt(3);
            }
        }
    }

    private void placeResources() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Biome biome = Biome.values()[biomeMap[x][y]];

                if (biome == Biome.LUSH_FOREST && Math.random() < 0.15) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.TREE, "Oak Tree"));
                } else if (biome == Biome.SUNNY_PLAINS && Math.random() < 0.1) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.TREE, "Soft Wood"));
                } else if (biome == Biome.DARK_WOODS && Math.random() < 0.2) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.TREE, "Dark Tree"));
                } else if (biome == Biome.SNOWY_TUNDRA && Math.random() < 0.1) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.TREE, "Pine Tree"));
                } else if (biome == Biome.ROCKY_FLATS && Math.random() < 0.1) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.ROCK, "Stone"));
                } else if (biome == Biome.DESERT && Math.random() < 0.05) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.BUSH, "Cactus"));
                } else if (biome == Biome.SWAMPY_WETLANDS && Math.random() < 0.15) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.BUSH, "Reeds"));
                } else if (biome == Biome.FLOWERING_MEADOW && Math.random() < 0.1) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.BUSH, "Wildflowers"));
                } else if (biome == Biome.HAUNTED_WOODS && Math.random() < 0.1) {
                    resources.add(new ResourcePlacement(x, y, ResourceType.TREE, "Dead Wood"));
                }
            }
        }
    }

    public byte[][] getBiomeMap() {
        return biomeMap;
    }

    public int[][] getTileVariants() {
        return tileVariants;
    }

    public List<ResourcePlacement> getResources() {
        return resources;
    }

    public static class ResourcePlacement {
        public int x;
        public int y;
        public ResourceType type;
        String resourceName;

        public ResourcePlacement(int x, int y, ResourceType type, String resourceName) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.resourceName = resourceName;
        }
    }

    public enum ResourceType {
        TREE, ROCK, BUSH
    }
}
