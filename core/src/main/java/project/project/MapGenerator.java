package project.project;

import java.util.Random;

public class MapGenerator {
    public enum Biome {
        PRAIRIE, WATER, MOUNTAIN, FOREST, SNOWY, SPOOKY
    }

    private int width;
    private int height;
    private byte[][] biomeMap;
    private int[][] tileVariants;
    private SimplexNoise simplexNoise;
    private Random random;

    public MapGenerator(int width, int height) {
        random = new Random();
        this.width = width;
        this.height = height;
        this.biomeMap = new byte[width][height];
        this.tileVariants = new int[width][height];

        // Generate a new seed each time if one is not provided
        int seed = SeedGenerator.generateComplexSeed();
        this.simplexNoise = new SimplexNoise(1024, 0.5, seed);

        generateIslandMap();
        enforceMinimumBiomeSize();
    }


    private void generateIslandMap() {
        double maxDistance = Math.sqrt((width / 2.0) * (width / 2.0) + (height / 2.0) * (height / 2.0));

        for (int x = -width / 2; x < width / 2; x++) {
            for (int y = -height / 2; y < height / 2; y++) {
                int arrayX = x + width / 2;
                int arrayY = y + height / 2;

                double distance = Math.sqrt(x * x + y * y) / maxDistance;
                double noise = simplexNoise.getNoise(x / Constants.MAP_SCALE, y / Constants.MAP_SCALE);

                Biome biome;
                if (distance > 0.75) {
                    biome = Biome.WATER;
                } else if (noise < -0.006) {
                    biome = Biome.PRAIRIE;
                } else if (noise < -0.002) {
                    biome = Biome.FOREST;
                } else if (noise < 0.002) {
                    biome = Biome.SNOWY;
                } else if (noise < 0.005) {
                    biome = Biome.SPOOKY;
                } else {
                    biome = Biome.MOUNTAIN;
                }

                biomeMap[arrayX][arrayY] = (byte) biome.ordinal();

                // Assign a random tile variant for this biome at this location
                int variantCount = getVariantCountForBiome(biome);
                tileVariants[arrayX][arrayY] = random.nextInt(variantCount);
            }
        }
    }

    private void enforceMinimumBiomeSize() {
        final int MIN_BIOME_SIZE = 500; // Adjust as needed
        boolean[][] visited = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!visited[x][y]) {
                    int biome = biomeMap[x][y];
                    int biomeSize = floodFillSize(x, y, biome, visited);

                    if (biomeSize < MIN_BIOME_SIZE) {
                        expandBiome(x, y, biome);
                    }
                }
            }
        }
    }

    /**
     * Calculates the size of a biome region using flood fill.
     */
    private int floodFillSize(int startX, int startY, int biome, boolean[][] visited) {
        int size = 0;
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.add(new int[]{startX, startY});

        while (!queue.isEmpty()) {
            int[] tile = queue.poll();
            int x = tile[0], y = tile[1];
            if (x < 0 || y < 0 || x >= width || y >= height || visited[x][y] || biomeMap[x][y] != biome) {
                continue;
            }
            visited[x][y] = true;
            size++;
            queue.add(new int[]{x + 1, y});
            queue.add(new int[]{x - 1, y});
            queue.add(new int[]{x, y + 1});
            queue.add(new int[]{x, y - 1});
        }
        return size;
    }

    /**
     * Expands a smaller biome region by converting surrounding tiles to the same biome.
     */
    private void expandBiome(int startX, int startY, int biome) {
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.add(new int[]{startX, startY});

        int expanded = 0;
        while (!queue.isEmpty() && expanded < 1280) {
            int[] tile = queue.poll();
            int x = tile[0], y = tile[1];
            if (x < 0 || y < 0 || x >= width || y >= height || biomeMap[x][y] == biome) {
                continue;
            }

            biomeMap[x][y] = (byte) biome;
            expanded++;

            queue.add(new int[]{x + 1, y});
            queue.add(new int[]{x - 1, y});
            queue.add(new int[]{x, y + 1});
            queue.add(new int[]{x, y - 1});
        }
    }

    private int getVariantCountForBiome(Biome biome) {
        switch (biome) {
            case PRAIRIE: return 4;
            case WATER: return 4;
            case MOUNTAIN: return 4;
            case FOREST: return 2;
            case SNOWY: return 5;
            case SPOOKY: return 4;
            default: return 1;
        }
    }

    public byte[][] getBiomeMap() {
        return biomeMap;
    }

    public int[][] getTileVariants() {
        return tileVariants;
    }
}
