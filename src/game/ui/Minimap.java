package src.game.ui;

import src.game.constants.Config;
import src.game.entities.Player;
import src.game.world.Tile;
import src.game.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Minimap {
    private static final int TILE_SIZE = Config.TILE_SIZE;
    private World world;
    private Player player;
    public int width = 150;
    public int height = 150;
    private float scale = 1;
    private BufferedImage minimapImage;
    private long lastUpdateTime = 0;
    private long updateInterval = Config.MINIMAP_UPDATE_INTERVAL;

    private boolean isExpanded = false;

    public Minimap(World world, Player player) {
        this.world = world;
        this.player = player;
        minimapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        redrawMinimap();
    }

    public void toggleExpanded() {
        isExpanded = !isExpanded;
        redrawMinimap(); // Redraw immediately on expansion toggle
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > updateInterval) {
            redrawMinimap();
            lastUpdateTime = currentTime;
        }
    }

    public void draw(Graphics2D g2) {
        int minimapX, minimapY;

        if (isExpanded) {
            // Center the expanded minimap on the screen with slight enlargement
            int expandedWidth = (int) (width * 1.5);
            int expandedHeight = (int) (height * 1.5);
            minimapX = (g2.getClipBounds().width - expandedWidth) / 2;
            minimapY = (g2.getClipBounds().height - expandedHeight) / 2;

            // Draw expanded minimap at the center with slight enlargement
            g2.drawImage(minimapImage, minimapX, minimapY, expandedWidth, expandedHeight, null);

            // Center the player marker within the expanded minimap
            int playerCenterX = minimapX + expandedWidth / 2;
            int playerCenterY = minimapY + expandedHeight / 2;
            g2.setColor(Color.RED);
            g2.fillRect(playerCenterX - 2, playerCenterY - 2, 5, 5);
        } else {
            // Position the smaller minimap in the top-right corner
            minimapX = g2.getClipBounds().width - width - 20;
            minimapY = 20;

            // Draw smaller minimap in the corner
            g2.drawImage(minimapImage, minimapX, minimapY, width, height, null);

            // Position player marker at the center of the smaller minimap
            g2.setColor(Color.RED);
            g2.fillRect(minimapX + width / 2 - 2, minimapY + height / 2 - 2, 5, 5);
        }

        // Draw a border around the minimap
        g2.setColor(Color.BLACK);
        g2.drawRect(minimapX, minimapY, isExpanded ? (int) (width * 1.5) : width, isExpanded ? (int) (height * 1.5) : height);
    }

    private void redrawMinimap() {
        Graphics2D g = minimapImage.createGraphics();

        // Set the scaling based on whether the minimap is expanded
        float effectiveScale = isExpanded ? scale / 1.5f : scale;

        // Calculate the top-left corner of the visible area in the world based on the player's position
        int worldX = (int) (player.x - (width / 2) * effectiveScale * TILE_SIZE);
        int worldY = (int) (player.y - (height / 2) * effectiveScale * TILE_SIZE);

        // Clear the previous image
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, minimapImage.getWidth(), minimapImage.getHeight());
        g.setComposite(AlphaComposite.SrcOver);

        // Draw each tile within the minimap area
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int tileX = (int) (worldX + x * effectiveScale * TILE_SIZE);
                int tileY = (int) (worldY + y * effectiveScale * TILE_SIZE);

                Tile tile = world.getTileAt(tileX, tileY, true);
                g.setColor(tile != null && !tile.type.equals("loading") ? getColorForTile(tile) : Color.BLACK);
                g.fillRect(x, y, 1, 1);
            }
        }

        g.dispose();
    }


    private Color getColorForTile(Tile tile) {
        switch (tile.type) {
            case "grass":
                return new Color(34, 139, 34);
            case "sand":
                return new Color(194, 178, 128);
            case "water":
                return new Color(65, 105, 225);
            case "forest":
                return new Color(0, 100, 0);
            default:
                return Color.BLACK;
        }
    }

    public BufferedImage getMinimapImage() {
        return minimapImage;
    }
}
