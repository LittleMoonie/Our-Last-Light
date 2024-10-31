package src;

import java.util.*;

public class World {
    private TileProvider tileProvider;

    public World() {
        tileProvider = new TileProvider();
    }

    public Tile getTileAt(int x, int y) {
        return tileProvider.getTileAt(x, y, false);
    }

    public boolean isLoadingTiles() {
        return tileProvider.isLoadingTiles();
    }

    // Implement the findLargestLandmass method
    public Set<Long> findLargestLandmass(int centerX, int centerY, int radius) {
        Set<Long> visited = new HashSet<>();
        Set<Long> largestLandmass = new HashSet<>();

        int startX = centerX - radius;
        int endX = centerX + radius;
        int startY = centerY - radius;
        int endY = centerY + radius;

        // Iterate over the area to find all landmasses
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                long key = generateKey(x, y);

                if (!visited.contains(key)) {
                    Set<Long> currentLandmass = new HashSet<>();
                    exploreLandmass(x, y, visited, currentLandmass);

                    if (currentLandmass.size() > largestLandmass.size()) {
                        largestLandmass = currentLandmass;
                    }
                }
            }
        }

        return largestLandmass;
    }

    private void exploreLandmass(int x, int y, Set<Long> visited, Set<Long> landmass) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{x, y});

        while (!queue.isEmpty()) {
            int[] coords = queue.poll();
            int cx = coords[0];
            int cy = coords[1];
            long key = generateKey(cx, cy);

            if (visited.contains(key)) {
                continue;
            }
            visited.add(key);

            Tile tile = getTileAt(cx * GameConstants.TILE_SIZE, cy * GameConstants.TILE_SIZE);

            if (tile != null && !tile.type.equals("water") && !tile.isObstacle) {
                landmass.add(key);

                // Add neighboring tiles to the queue
                queue.offer(new int[]{cx + 1, cy});
                queue.offer(new int[]{cx - 1, cy});
                queue.offer(new int[]{cx, cy + 1});
                queue.offer(new int[]{cx, cy - 1});
            }
        }
    }

    private long generateKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }
}
