package src.game.world;

import src.game.constants.Config;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class World {
    private TileProvider tileProvider;
    private long worldSeed;

    public World(long seed) {
        this.worldSeed = seed;
        tileProvider = new TileProvider(seed);
    }

    public Tile getTileAt(int x, int y, boolean immediate) {
        return tileProvider.getTileAt(x, y, immediate);
    }

    public boolean isLoadingTiles() {
        return tileProvider.isLoadingTiles();
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public Set<Long> findLargestLandmass(int centerX, int centerY, int radius) {
        Set<Long> visited = new HashSet<>();
        Set<Long> largestLandmass = new HashSet<>();

        int startX = centerX - radius;
        int endX = centerX + radius;
        int startY = centerY - radius;
        int endY = centerY + radius;

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

            Tile tile = getTileAt(cx * Config.TILE_SIZE, cy * Config.TILE_SIZE, true);

            if (tile != null && !tile.type.equals("water") && !tile.isObstacle) {
                landmass.add(key);

                queue.offer(new int[]{cx + 1, cy});
                queue.offer(new int[]{cx - 1, cy});
                queue.offer(new int[]{cx, cy + 1});
                queue.offer(new int[]{cx, cy - 1});
            }
        }
    }

    public void render(Graphics2D g2, int cameraX, int cameraY, int screenWidth, int screenHeight) {
        int tilesAcross = screenWidth / Config.TILE_SIZE + 2;
        int tilesDown = screenHeight / Config.TILE_SIZE + 2;

        int startTileX = cameraX / Config.TILE_SIZE;
        int startTileY = cameraY / Config.TILE_SIZE;

        for (int x = startTileX; x < startTileX + tilesAcross; x++) {
            for (int y = startTileY; y < startTileY + tilesDown; y++) {
                Tile tile = getTileAt(x * Config.TILE_SIZE, y * Config.TILE_SIZE, false);
                if (tile != null) {
                    int screenX = (x * Config.TILE_SIZE) - cameraX;
                    int screenY = (y * Config.TILE_SIZE) - cameraY;
                    g2.drawImage(tile.getImage(), screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }
            }
        }
    }

    private long generateKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }
}
