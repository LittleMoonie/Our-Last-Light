// World.java
package src.game.world;

import src.game.constants.Config;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class World {
    private TileProvider tileProvider;
    private long worldSeed;
    private String worldName;  // Add worldName attribute

    public World(long seed, String worldName) {
        this.worldSeed = seed;
        this.worldName = worldName;
        tileProvider = new TileProvider(seed);

        // Attempt to load existing world data if available
        loadWorldData(getWorldSaveFolderPath());
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public String getWorldName() {
        return worldName;
    }

    public TileProvider getTileProvider() {
        return tileProvider;
    }

    public Tile getTileAt(int x, int y, boolean spawnable) {
        return tileProvider.getTileAt(x, y, spawnable);
    }

    public void saveWorldData() {
        String folderPath = getWorldSaveFolderPath();
        new File(folderPath).mkdirs();  // Create folder if it doesn't exist
        try {
            tileProvider.saveTileData(folderPath + "/world_data.dat");
        } catch (IOException e) {
            System.err.println("Error saving world data: " + e.getMessage());
        }
    }

    private void loadWorldData(String folderPath) {
        File worldDataFile = new File(folderPath + "/world_data.dat");
        if (worldDataFile.exists()) {
            try {
                tileProvider.loadTileData(worldDataFile.getPath());
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading world data: " + e.getMessage());
            }
        }
    }

    private String getWorldSaveFolderPath() {
        return Config.SAVE_DIRECTORY + "/" + worldName;  // Each world has its own folder
    }

    public void render(Graphics2D g2, int cameraX, int cameraY, int screenWidth, int screenHeight) {
        int tilesAcross = screenWidth / Config.TILE_SIZE + 2;
        int tilesDown = screenHeight / Config.TILE_SIZE + 2;

        int startTileX = cameraX / Config.TILE_SIZE;
        int startTileY = cameraY / Config.TILE_SIZE;

        for (int x = startTileX; x < startTileX + tilesAcross; x++) {
            for (int y = startTileY; y < startTileY + tilesDown; y++) {
                Tile tile = getTileAt(x * Config.TILE_SIZE, y * Config.TILE_SIZE, true);
                if (tile != null) {
                    int screenX = (x * Config.TILE_SIZE) - cameraX;
                    int screenY = (y * Config.TILE_SIZE) - cameraY;
                    g2.drawImage(tile.getImage(), screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }
            }
        }
    }


    public void updateWorld() {
        Set<Long> allKeys = tileProvider.getAllGeneratedTileKeys();
        for (Long key : allKeys) {
            Tile tile = tileProvider.getTileByKey(key);
            if (tile != null) {
                tile.updateRegrowth();
            }
        }
    }
}
