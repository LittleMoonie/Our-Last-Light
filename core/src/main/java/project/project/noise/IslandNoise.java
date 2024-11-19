package project.project.noise;

import project.project.Constants;
import project.project.map.Biome;
import project.project.utils.Point;

import java.util.*;

public class IslandNoise {
    private final SimplexNoise elevationNoise;
    private final SimplexNoise detailNoise;
    private final SimplexNoise biomeNoise;
    private final SimplexNoise tileVariantNoise;
    private final SimplexNoise warpNoiseX;
    private final SimplexNoise warpNoiseY;
    private final int width;
    private final int height;
    private final int seed;

    public IslandNoise(int width, int height, int seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;

        // Create multiple layers of noise for island shape and biome distribution
        elevationNoise = new SimplexNoise(256, 0.6, seed);
        detailNoise = new SimplexNoise(1024, 0.8, seed + 1);
        biomeNoise = new SimplexNoise(64, 1.0, seed + 2);
        tileVariantNoise = new SimplexNoise(64, 0.5, seed + 5);

        // Warp noises to add turbulence
        warpNoiseX = new SimplexNoise(64, 0.5, seed + 3);
        warpNoiseY = new SimplexNoise(64, 0.5, seed + 4);
    }

    public Biome[][] generateBiomeMap() {
        Biome[][] biomeMap = new Biome[width][height];
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        // Step 1: Initialize the map with DEEP_OCEAN
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                biomeMap[x][y] = Biome.DEEP_OCEAN;
            }
        }

        // Step 2: Add OCEAN Layer
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double centerDistance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                double distanceFactor = Math.max(0, (centerDistance - width / 3) / (width / 3));

                double elevationValue = elevationNoise.getNoise(x * 0.01, y * 0.01) - distanceFactor;
                if (elevationValue > -0.6 && elevationValue <= -0.4) {
                    biomeMap[x][y] = Biome.OCEAN;
                }
            }
        }

        // Step 3: Add Island Layer
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double centerDistance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                double distanceFactor = Math.max(0, (centerDistance - width / 3) / (width / 3)); // Controls Island size

                double elevationValue = elevationNoise.getNoise(x * 0.005, y * 0.005) * 1.8
                    + detailNoise.getNoise(x * 0.02, y * 0.02) * 0.7
                    - distanceFactor * 2;

                if (elevationValue > -0.6 && (biomeMap[x][y] == Biome.OCEAN || biomeMap[x][y] == Biome.DEEP_OCEAN)) {
                    biomeMap[x][y] = Biome.LUSH_FOREST;
                }
            }
        }

        // Step 4: Add Biome Layer with Normalization
        double minBiomeValue = Double.MAX_VALUE;
        double maxBiomeValue = Double.MIN_VALUE;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (biomeMap[x][y] == Biome.LUSH_FOREST) {
                    // Warp the noise
                    double warpX = warpNoiseX.getNoise(x * 0.05, y * 0.05) * 20; // Increased warping strength
                    double warpY = warpNoiseY.getNoise(x * 0.05, y * 0.05) * 20;
                    double rawBiomeValue = biomeNoise.getNoise((x + warpX) * 0.01, (y + warpY) * 0.01);

                    // Track min and max biomeValue
                    if (rawBiomeValue < minBiomeValue) minBiomeValue = rawBiomeValue;
                    if (rawBiomeValue > maxBiomeValue) maxBiomeValue = rawBiomeValue;
                }
            }
        }

        System.out.println("Biome Value Range: " + minBiomeValue + " to " + maxBiomeValue);

        // Normalize biome values and assign biomes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (biomeMap[x][y] == Biome.LUSH_FOREST) {
                    double warpX = warpNoiseX.getNoise(x * 0.05, y * 0.05) * 20;
                    double warpY = warpNoiseY.getNoise(x * 0.05, y * 0.05) * 20;
                    double rawBiomeValue = biomeNoise.getNoise((x + warpX) * 0.01, (y + warpY) * 0.01)
                        + 0.3 * biomeNoise.getNoise(x * 0.03, y * 0.03)
                        - 0.2 * biomeNoise.getNoise(x * 0.07, y * 0.07);

                    // Normalize and assign biome
                    double biomeValue = (rawBiomeValue - minBiomeValue) / (maxBiomeValue - minBiomeValue);
                    biomeMap[x][y] = assignBiomeByGradient(biomeValue, biomeMap, x, y);
                }
            }
        }

        System.out.println("Normalized Biome Value Range: 0.0 to 1.0");

        // Step 5: Ensure all biomes appear
        ensureAllBiomesAppear(biomeMap);

        // Step 6: Enforce biome sizes
        enforceBiomeSizes(biomeMap);
        return biomeMap;
    }

    private void ensureAllBiomesAppear(Biome[][] biomeMap) {
        Set<Biome> existingBiomes = new HashSet<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                existingBiomes.add(biomeMap[x][y]);
            }
        }

        List<Biome> missingBiomes = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            if (!existingBiomes.contains(biome)) {
                missingBiomes.add(biome);
            }
        }

        Random random = new Random();
        for (Biome missingBiome : missingBiomes) {
            int x, y;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (biomeMap[x][y] != Biome.LUSH_FOREST);

            biomeMap[x][y] = missingBiome;
            expandBiomeRegion(biomeMap, x, y, missingBiome, 10 + random.nextInt(20));
        }
    }

    private void expandBiomeRegion(Biome[][] biomeMap, int startX, int startY, Biome biome, int size) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(startX, startY));
        int count = 0;

        while (!queue.isEmpty() && count < size) {
            Point p = queue.poll();
            int x = p.x;
            int y = p.y;

            if (x < 0 || y < 0 || x >= width || y >= height || biomeMap[x][y] != Biome.LUSH_FOREST) {
                continue;
            }

            biomeMap[x][y] = biome;
            count++;

            queue.add(new Point(x + 1, y));
            queue.add(new Point(x - 1, y));
            queue.add(new Point(x, y + 1));
            queue.add(new Point(x, y - 1));
        }
    }

    private Biome assignBiomeByGradient(double biomeValue, Biome[][] biomeMap, int x, int y) {
        List<Biome> neighborBiomes = getNeighborBiomes(biomeMap, x, y);

        // If neighbors exist, prioritize biomes close to the neighbors
        if (!neighborBiomes.isEmpty() && Math.random() < 0.3) {
            return neighborBiomes.get(new Random().nextInt(neighborBiomes.size()));
        }

        // Otherwise, use the gradient rules
        if (biomeValue < 0.05) return Biome.DENSE_JUNGLE;
        if (biomeValue < 0.1) return Biome.SWAMPY_WETLANDS;
        if (biomeValue < 0.2) return Biome.LUSH_FOREST;
        if (biomeValue < 0.3) return Biome.DARK_WOODS;
        if (biomeValue < 0.4) return Biome.SUNNY_PLAINS;
        if (biomeValue < 0.5) return Biome.AUTUMN_GROVE;
        if (biomeValue < 0.6) return Biome.FLOWERING_MEADOW;
        if (biomeValue < 0.65) return Biome.ROCKY_FLATS;
        if (biomeValue < 0.7) return Biome.SAVANNA;
        if (biomeValue < 0.75) return Biome.ENCHANTED_GROVE;
        if (biomeValue < 0.8) return Biome.SPOOKY_VALLEY;
        if (biomeValue < 0.85) return Biome.SNOWY_TUNDRA;
        if (biomeValue < 0.9) return Biome.HAUNTED_WOODS;
        if (biomeValue < 0.95) return Biome.DESERT;
        return Biome.DESERT_OASIS;
    }

    private List<Biome> getNeighborBiomes(Biome[][] biomeMap, int x, int y) {
        List<Biome> neighbors = new ArrayList<>();
        int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] offset : offsets) {
            int nx = x + offset[0];
            int ny = y + offset[1];
            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                neighbors.add(biomeMap[nx][ny]);
            }
        }
        return neighbors;
    }

    private void enforceBiomeSizes(Biome[][] biomeMap) {
        boolean[][] visited = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!visited[x][y] && biomeSettingsMap.containsKey(biomeMap[x][y])) {
                    Biome biome = biomeMap[x][y];
                    List<Point> region = new ArrayList<>();
                    floodFill(biomeMap, x, y, biome, visited, region);

                    int regionSize = region.size();
                    BiomeSettings settings = biomeSettingsMap.get(biome);

                    if (regionSize < settings.minSize) {
                        expandBiomeRegion(biomeMap, region, settings.minSize - regionSize);
                    }
                }
            }
        }
    }

    private void floodFill(Biome[][] biomeMap, int startX, int startY, Biome biome, boolean[][] visited, List<Point> region) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(startX, startY));
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            region.add(p);

            for (int[] offset : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int nx = p.x + offset[0];
                int ny = p.y + offset[1];

                if (nx >= 0 && ny >= 0 && nx < width && ny < height
                    && !visited[nx][ny] && biomeMap[nx][ny] == biome) {
                    visited[nx][ny] = true;
                    queue.add(new Point(nx, ny));
                }
            }
        }
    }

    private void expandBiomeRegion(Biome[][] biomeMap, List<Point> region, int tilesNeeded) {
        Random random = new Random();
        List<Point> candidates = new ArrayList<>(region);

        while (tilesNeeded > 0 && !candidates.isEmpty()) {
            Point p = candidates.remove(random.nextInt(candidates.size()));

            for (int[] offset : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int nx = p.x + offset[0];
                int ny = p.y + offset[1];
                if (nx >= 0 && ny >= 0 && nx < width && ny < height && biomeMap[nx][ny] == Biome.OCEAN) {
                    biomeMap[nx][ny] = biomeMap[p.x][p.y];
                    region.add(new Point(nx, ny));
                    candidates.add(new Point(nx, ny));
                    tilesNeeded--;
                    if (tilesNeeded <= 0) break;
                }
            }
        }
    }

    private final Map<Biome, BiomeSettings> biomeSettingsMap = new HashMap<>() {{
        put(Biome.LUSH_FOREST, new BiomeSettings(0.15, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.SUNNY_PLAINS, new BiomeSettings(0.2, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.DENSE_JUNGLE, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.AUTUMN_GROVE, new BiomeSettings(0.12, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.FLOWERING_MEADOW, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.ROCKY_FLATS, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.SAVANNA, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.ENCHANTED_GROVE, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.SPOOKY_VALLEY, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.SNOWY_TUNDRA, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.HAUNTED_WOODS, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.DESERT, new BiomeSettings(0.1, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
        put(Biome.DESERT_OASIS, new BiomeSettings(0.05, Constants.MIN_BIOME_SIZE, Constants.MAX_BIOME_SIZE));
    }};

    public static class BiomeSettings {
        public double appearanceChance;
        public int minSize;
        public int maxSize;

        public BiomeSettings(double appearanceChance, int minSize, int maxSize) {
            this.appearanceChance = appearanceChance;
            this.minSize = minSize;
            this.maxSize = maxSize;
        }
    }

}
