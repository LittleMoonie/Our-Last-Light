package project.project.map;

import project.project.Constants;

import java.util.*;

public class BiomeMapProcessor {
    private byte[][] biomeMap;
    private int width;
    private int height;

    private static final int[] DX = { -1, 1, 0, 0 };
    private static final int[] DY = { 0, 0, -1, 1 };

    private Map<Integer, List<Point>> biomeRegions;

    public BiomeMapProcessor(byte[][] biomeMap) {
        this.biomeMap = biomeMap;
        this.width = biomeMap.length;
        this.height = biomeMap[0].length;
    }

    public void processBiomeMap() {
        biomeRegions = new HashMap<>();
        int[][] labels = new int[width][height];
        int labelCounter = 1;

        // Label connected regions
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (labels[x][y] == 0) {
                    int biomeType = biomeMap[x][y];
                    List<Point> region = new ArrayList<>();
                    floodFill(x, y, biomeType, labels, labelCounter, region);
                    biomeRegions.put(labelCounter, region);
                    labelCounter++;
                }
            }
        }

        // Merge small biomes
        mergeSmallBiomes(labels);

        // Split large biomes
        splitLargeBiomes(labels);

        // Update biomeMap with processed data
        for (List<Point> region : biomeRegions.values()) {
            byte biomeType = biomeMap[region.get(0).x][region.get(0).y];
            for (Point p : region) {
                biomeMap[p.x][p.y] = biomeType;
            }
        }
    }

    private void floodFill(int x, int y, int biomeType, int[][] labels, int label, List<Point> region) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));
        labels[x][y] = label;
        region.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            for (int i = 0; i < 4; i++) {
                int nx = p.x + DX[i];
                int ny = p.y + DY[i];
                if (nx >= 0 && ny >= 0 && nx < width && ny < height &&
                    labels[nx][ny] == 0 && biomeMap[nx][ny] == biomeType) {
                    labels[nx][ny] = label;
                    queue.add(new Point(nx, ny));
                    region.add(new Point(nx, ny));
                }
            }
        }
    }

    private void mergeSmallBiomes(int[][] labels) {
        boolean merged;
        do {
            merged = false;
            Iterator<Map.Entry<Integer, List<Point>>> iterator = biomeRegions.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, List<Point>> entry = iterator.next();
                int label = entry.getKey();
                List<Point> region = entry.getValue();
                if (region.size() < Constants.MIN_BIOME_SIZE) {
                    Point p = region.get(0);
                    byte biomeType = biomeMap[p.x][p.y];
                    int neighborLabel = findNeighborWithSameBiome(region, labels, biomeType);
                    if (neighborLabel != -1) {
                        List<Point> neighborRegion = biomeRegions.get(neighborLabel);
                        neighborRegion.addAll(region);
                        for (Point point : region) {
                            labels[point.x][point.y] = neighborLabel;
                        }
                        iterator.remove();
                        merged = true;
                        break;
                    }
                }
            }
        } while (merged);
    }

    private int findNeighborWithSameBiome(List<Point> region, int[][] labels, byte biomeType) {
        Set<Integer> neighborLabels = new HashSet<>();
        for (Point p : region) {
            for (int i = 0; i < 4; i++) {
                int nx = p.x + DX[i];
                int ny = p.y + DY[i];
                if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                    int neighborLabel = labels[nx][ny];
                    if (neighborLabel != labels[p.x][p.y]) {
                        byte neighborBiome = biomeMap[nx][ny];
                        if (neighborBiome == biomeType) {
                            neighborLabels.add(neighborLabel);
                        }
                    }
                }
            }
        }

        int maxSize = -1;
        int maxLabel = -1;
        for (int label : neighborLabels) {
            List<Point> neighborRegion = biomeRegions.get(label);
            if (neighborRegion.size() > maxSize) {
                maxSize = neighborRegion.size();
                maxLabel = label;
            }
        }

        return maxLabel;
    }

    private void splitLargeBiomes(int[][] labels) {
        List<Integer> labelsToSplit = new ArrayList<>();
        for (Map.Entry<Integer, List<Point>> entry : biomeRegions.entrySet()) {
            if (entry.getValue().size() > Constants.MAX_BIOME_SIZE) {
                labelsToSplit.add(entry.getKey());
            }
        }

        for (int label : labelsToSplit) {
            List<Point> region = biomeRegions.get(label);
            biomeRegions.remove(label);
            splitRegion(region, labels, label);
        }
    }

    private void splitRegion(List<Point> region, int[][] labels, int originalLabel) {
        int numSplits = (int) Math.ceil((double) region.size() / Constants.MAX_BIOME_SIZE);
        Random rand = new Random();
        List<Point> seeds = new ArrayList<>();

        // Select random seeds for new regions
        for (int i = 0; i < numSplits; i++) {
            Point seed = region.get(rand.nextInt(region.size()));
            seeds.add(seed);
        }

        Map<Point, List<Point>> newRegions = new HashMap<>();
        for (Point seed : seeds) {
            newRegions.put(seed, new ArrayList<>());
        }

        // Assign each point to the nearest seed
        for (Point p : region) {
            Point nearestSeed = null;
            double minDistance = Double.MAX_VALUE;
            for (Point seed : seeds) {
                double distance = Math.hypot(p.x - seed.x, p.y - seed.y);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestSeed = seed;
                }
            }
            newRegions.get(nearestSeed).add(p);
        }

        // Update labels and biomeRegions
        for (List<Point> newRegion : newRegions.values()) {
            biomeRegions.put(originalLabel, newRegion);
            for (Point p : newRegion) {
                labels[p.x][p.y] = originalLabel;
            }
            originalLabel++; // Increment label for next region
        }
    }

    public byte[][] getBiomeMap() {
        return biomeMap;
    }

    // Helper class to represent points
    private static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
