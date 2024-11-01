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
    public static final int SIZE = Config.PLAYER_SIZE;
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
        int radius = 1000;  // You might want to change this radius for testing

        while (attempt < maxAttempts) {
            int offsetX = (int) (Math.random() * radius * 2) - radius;
            int offsetY = (int) (Math.random() * radius * 2) - radius;
            int tileX = centerX + offsetX;
            int tileY = centerY + offsetY;

            Tile tile = world.getTileAt(tileX * TILE_SIZE, tileY * TILE_SIZE, true);

            if (tile != null && !tile.type.equals("water") && !tile.isObstacle) {
                x = tileX * TILE_SIZE + TILE_SIZE / 2;  // Centering on tile
                y = tileY * TILE_SIZE + TILE_SIZE / 2;  // Centering on tile
                return;  // Successfully found a spawn location
            }
            attempt++;
        }
        System.err.println("Failed to find a valid spawn location.");
    }


    public void update() {
        int currentSpeed = sprint ? sprintSpeed : normalSpeed;

        // Calculate movement delta based on key states
        double dx = 0, dy = 0;
        if (up) dy -= currentSpeed;
        if (down) dy += currentSpeed;
        if (left) dx -= currentSpeed;
        if (right) dx += currentSpeed;

        // Normalize movement to prevent diagonal speed boost
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            dx = dx / length * currentSpeed;
            dy = dy / length * currentSpeed;
        }

        int newX = x + (int) dx;
        int newY = y + (int) dy;

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
        return tile != null && !tile.type.equals("water") && !tile.isObstacle;  // Check for obstacles properly
    }

    public boolean collecting = false;

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Z, KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_Q, KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
            case KeyEvent.VK_SHIFT -> sprint = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Z, KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_Q, KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
            case KeyEvent.VK_SHIFT -> sprint = false;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void collectResource() {
        // Determine the direction the player is facing and calculate the tile coordinates
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;

        // Adjust tile coordinates based on the direction the player is facing
        if (up) {
            tileY -= 1; // Collect from the tile above
        } else if (down) {
            tileY += 1; // Collect from the tile below
        } else if (left) {
            tileX -= 1; // Collect from the tile to the left
        } else if (right) {
            tileX += 1; // Collect from the tile to the right
        }

        Tile currentTile = world.getTileAt(tileX * TILE_SIZE, tileY * TILE_SIZE, true);

        if (currentTile != null && currentTile.hasResource()) {
            if ("tree".equals(currentTile.resource)) {
                inventory.addItem("Wood", 1);
            } else if ("stone".equals(currentTile.resource)) {
                inventory.addItem("Stone", 1);
            }
            currentTile.removeResource(); // Remove resource after collecting
        }
    }
}
