package src;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Player {
    int x, y; // Pixel coordinates
    int normalSpeed = 4;
    int sprintSpeed = 8;
    int size = 28;
    boolean up, down, left, right, sprint;
    World world;
    BufferedImage image;
    Inventory inventory;


    private static final int TILE_SIZE = GameConstants.TILE_SIZE;

    public Player(World world) {
        this.world = world;
        inventory = new Inventory();
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/resources/player.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Find a suitable spawn location
        findSpawnLocation();

        // Preload tiles around the player
        preloadTilesAroundPlayer(10); // Preload tiles within a 10-tile radius
    }

    private void preloadTilesAroundPlayer(int radius) {
        int centerTileX = x / GameConstants.TILE_SIZE;
        int centerTileY = y / GameConstants.TILE_SIZE;

        for (int i = centerTileX - radius; i <= centerTileX + radius; i++) {
            for (int j = centerTileY - radius; j <= centerTileY + radius; j++) {
                int worldX = i * GameConstants.TILE_SIZE;
                int worldY = j * GameConstants.TILE_SIZE;
                world.getTileAt(worldX, worldY); // Immediate loading
            }
        }
    }

    private void findSpawnLocation() {
        int centerX = 0;
        int centerY = 0;
        int radius = 100; // Search radius in tiles

        Set<Long> largestLandmass = world.findLargestLandmass(centerX, centerY, radius);

        if (!largestLandmass.isEmpty()) {
            // Choose a random tile within the largest landmass
            List<Long> landTiles = new ArrayList<>(largestLandmass);
            long key = landTiles.get(new Random().nextInt(landTiles.size()));
            int tileX = (int) (key >> 32);
            int tileY = (int) key;

            int worldX = tileX * GameConstants.TILE_SIZE;
            int worldY = tileY * GameConstants.TILE_SIZE;

            x = worldX + GameConstants.TILE_SIZE / 2;
            y = worldY + GameConstants.TILE_SIZE / 2;
        } else {
            System.err.println("Failed to find a valid spawn location.");
            // Handle this case appropriately
        }
    }



    public void update() {
        int currentSpeed = sprint ? sprintSpeed : normalSpeed;

        double dx = 0, dy = 0;

        if (up) dy -= 1;
        if (down) dy += 1;
        if (left) dx -= 1;
        if (right) dx += 1;

        // Normalize movement vector
        double length = Math.hypot(dx, dy);
        if (length != 0) {
            dx = dx / length * currentSpeed;
            dy = dy / length * currentSpeed;

            int newX = x + (int) dx;
            int newY = y + (int) dy;

            if (canMoveTo(newX, newY)) {
                x = newX;
                y = newY;
            }
        }
        if (collecting) {
            collectResource();
            collecting = false;
        }
    }

    private boolean canMoveTo(int x, int y) {
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;
        Tile tile = world.getTileAt(tileX * TILE_SIZE, tileY * TILE_SIZE);
        return tile != null && !tile.type.equals("water") && !tile.isObstacle;
    }

    boolean collecting = false;

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_UP) {
            up = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            down = true;
        }
        if (code == KeyEvent.VK_Q || code == KeyEvent.VK_LEFT) {
            left = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            right = true;
        }
        if (code == KeyEvent.VK_SHIFT) {
            sprint = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            collecting = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_UP) {
            up = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            down = false;
        }
        if (code == KeyEvent.VK_Q || code == KeyEvent.VK_LEFT) {
            left = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            right = false;
        }
        if (code == KeyEvent.VK_SHIFT) {
            sprint = false;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void collectResource() {
        Tile currentTile = world.getTileAt(x, y);
        if (currentTile.type.equals("forest")) {
            inventory.addWood(1);
            // Change the tile to grass after collecting
            currentTile.setType("grass");
        }
    }
}
