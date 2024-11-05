// src/game/world/World.java
package src.game.world;

import src.game.constants.Config;
import src.game.components.PositionComponent;
import src.game.constants.WorldConfig;
import src.game.entities.Entity;

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
        this.tileProvider = new TileProvider(seed);
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

    public String getWorldName() {
        return "World_" + worldSeed;
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

            Tile tile = getTileAt(cx * Config.TILE_WIDTH, cy * Config.TILE_WIDTH, true);

            if (tile != null && !tile.type.equals("water") && !tile.isObstacle) {
                landmass.add(key);

                queue.offer(new int[]{cx + 1, cy});
                queue.offer(new int[]{cx - 1, cy});
                queue.offer(new int[]{cx, cy + 1});
                queue.offer(new int[]{cx, cy - 1});
            }
        }
    }

    public Point toIsometric(int x, int y) {
        int isoX = (x - y) * Config.TILE_WIDTH / 2;
        int isoY = (x + y) * Config.TILE_WIDTH / 4;
        return new Point(isoX, isoY);
    }

    public void render(Graphics2D g2, int cameraX, int cameraY, int screenWidth, int screenHeight) {
        int tilesAcross = screenWidth / Config.TILE_WIDTH + 2; // +2 for buffer on the sides
        int tilesDown = screenHeight / Config.TILE_HEIGHT + 2; // +2 for buffer on top and bottom

        // Determine the starting tile position based on camera position
        int startTileX = cameraX / Config.TILE_WIDTH;
        int startTileY = cameraY / Config.TILE_HEIGHT;

        // Render the tiles that fit within the screen
        for (int x = startTileX; x < startTileX + tilesAcross; x++) {
            for (int y = startTileY; y < startTileY + tilesDown; y++) {
                Tile tile = getTileAt(x * Config.TILE_WIDTH, y * Config.TILE_HEIGHT, false);
                if (tile != null) {
                    // Adjust screenX and screenY for isometric rendering
                    int screenX = (x - y) * (Config.TILE_WIDTH / 2) + (screenWidth / 2) - cameraX;
                    int screenY = (x + y) * (Config.TILE_HEIGHT / 2) - cameraY;

                    // Draw the tile image
                    g2.drawImage(tile.getImage(), screenX, screenY, null);
                }
            }
        }
    }

    private long generateKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }
}
