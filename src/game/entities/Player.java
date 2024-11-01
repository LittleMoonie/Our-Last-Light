package src.game.entities;

import src.game.constants.Config;
import src.game.world.Tile;
import src.game.world.World;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player {
    public int x, y; // Pixel coordinates
    public String name;
    public static final int SIZE = Config.PLAYER_SIZE ;
    public int normalSpeed = 4;
    public int sprintSpeed = 8;
    public boolean up, down, left, right, sprint;
    public World world;
    public BufferedImage image;
    public Inventory inventory;

    private static final int TILE_SIZE = Config.TILE_SIZE;

    public Player(World world, String name) {
        this.world = world;
        this.name = name;
        inventory = new Inventory();
        try {
            image = ImageIO.read(getClass().getResourceAsStream(Config.PLAYER_IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        findSpawnLocation();
    }

    private void findSpawnLocation() {
        int maxAttempts = Config.MAX_SPAWN_ATTEMPTS;
        int attempt = 0;
        int centerX = 0;
        int centerY = 0;
        int radius = 100;

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
        }
    }
    public void update() {
        int currentSpeed = sprint ? sprintSpeed : normalSpeed;

        // Calculate movement delta based on key states
        int dx = 0, dy = 0;
        if (up) dy -= currentSpeed;
        if (down) dy += currentSpeed;
        if (left) dx -= currentSpeed;
        if (right) dx += currentSpeed;

        // Normalize movement to prevent diagonal speed boost
        if (dx != 0 && dy != 0) {
            dx = dx / (int) Math.sqrt(2);
            dy = dy / (int) Math.sqrt(2);
        }

        int newX = x + dx;
        int newY = y + dy;

        // Only update position if the player can move to the new position
        if (canMoveTo(newX, newY)) {
            x = newX;
            y = newY;
        }

        // Trigger resource collection if 'collecting' flag is active
        if (collecting) {
            collectResource();
            collecting = false; // Reset collecting flag after collection
        }
    }

    private boolean canMoveTo(int x, int y) {
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;
        Tile tile = world.getTileAt(tileX * TILE_SIZE, tileY * TILE_SIZE, true);
        return tile != null && !tile.type.equals("water") && !tile.isObstacle;
    }

    public boolean collecting = false;

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_UP) up = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = true;
        if (code == KeyEvent.VK_Q || code == KeyEvent.VK_LEFT) left = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;
        if (code == KeyEvent.VK_SHIFT) sprint = true;
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_UP) up = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = false;
        if (code == KeyEvent.VK_Q || code == KeyEvent.VK_LEFT) left = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = false;
        if (code == KeyEvent.VK_SHIFT) sprint = false;
    }
    public BufferedImage getImage() {
        return image;
    }

    public void collectResource() {
        Tile currentTile = world.getTileAt(x, y, true);
        if (currentTile.type.equals("forest")) {
            inventory.addItem("Wood", 1);
            currentTile.setType("grass");
        }
    }
}
