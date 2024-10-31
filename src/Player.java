package src;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.Serializable;

public class Player {
    int x, y; // Pixel coordinates
    String name;
    int normalSpeed = 4;
    int sprintSpeed = 8;
    int size = 28;
    boolean up, down, left, right, sprint;
    World world;
    BufferedImage image;
    Inventory inventory;

    private static final int TILE_SIZE = GameConstants.TILE_SIZE;

    public Player(World world, String name) {
        this.world = world;
        this.name = name;
        inventory = new Inventory();
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/resources/player.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Find a suitable spawn location
        findSpawnLocation();
    }

    private void findSpawnLocation() {
        int maxAttempts = 1000;
        int attempt = 0;
        int centerX = 0;
        int centerY = 0;
        int radius = 100; // Search within a 100-tile radius

        while (attempt < maxAttempts) {
            int offsetX = (int) (Math.random() * radius * 2) - radius;
            int offsetY = (int) (Math.random() * radius * 2) - radius;
            int tileX = centerX + offsetX;
            int tileY = centerY + offsetY;

            int worldX = tileX * TILE_SIZE;
            int worldY = tileY * TILE_SIZE;

            Tile tile = world.getTileAt(worldX, worldY, true);

            if (tile != null && !tile.type.equals("water") && !tile.isObstacle) {
                x = worldX + TILE_SIZE / 2;
                y = worldY + TILE_SIZE / 2;
                break;
            }
            attempt++;
        }

        if (attempt == maxAttempts) {
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
        Tile tile = world.getTileAt(tileX * TILE_SIZE, tileY * TILE_SIZE, true);
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
        if (code == KeyEvent.VK_E) {
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
        Tile currentTile = world.getTileAt(x, y, true);
        if (currentTile.type.equals("forest")) {
            inventory.addItem("Wood", 1);
            // Change the tile to grass after collecting
            currentTile.setType("grass");
        }
    }
}
