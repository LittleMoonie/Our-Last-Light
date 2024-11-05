package src.game.systems;

import src.game.components.PositionComponent;
import src.game.components.MinimapComponent;
import src.game.constants.Config;
import src.game.entities.Entity;
import src.game.world.Tile;
import src.game.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MinimapSystem extends System {
    private final World world;
    private BufferedImage minimapImage;
    private final int width = 150;
    private final int height = 150;
    private long lastUpdateTime = 0;
    private final long updateInterval;

    public MinimapSystem(World world, int minimapUpdateInterval) {
        this.world = world;
        this.updateInterval = minimapUpdateInterval;
        this.minimapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        PositionComponent position = entity.getComponent(PositionComponent.class);
        MinimapComponent minimap = entity.getComponent(MinimapComponent.class);

        if (position == null || minimap == null) return;

        long currentTime = java.lang.System.currentTimeMillis();
        if (currentTime - lastUpdateTime > updateInterval) {
            redrawMinimap(position, minimap.isExpanded);
            lastUpdateTime = currentTime;
        }
    }

    public void toggleExpanded(Entity entity) {
        MinimapComponent minimap = entity.getComponent(MinimapComponent.class);
        if (minimap != null) {
            minimap.isExpanded = !minimap.isExpanded;
        }
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Entity entity) {
        MinimapComponent minimap = entity.getComponent(MinimapComponent.class);
        if (minimap == null) return;

        int displayedWidth = minimap.isExpanded ? (int) (width * 1.5) : width;
        int displayedHeight = minimap.isExpanded ? (int) (height * 1.5) : height;

        int minimapX = minimap.isExpanded ? (screenWidth - displayedWidth) / 2 : screenWidth - width - 20;
        int minimapY = minimap.isExpanded ? (screenHeight - displayedHeight) / 2 : 20;

        g2.drawImage(minimapImage, minimapX, minimapY, displayedWidth, displayedHeight, null);
        g2.setColor(Color.BLACK);
        g2.drawRect(minimapX, minimapY, displayedWidth, displayedHeight);
    }

    private void redrawMinimap(PositionComponent position, boolean isExpanded) {
        Graphics2D g = minimapImage.createGraphics();
        float scale = isExpanded ? 0.75f : 1.0f;
        int tileSize = (int) (Config.TILE_WIDTH * scale);

        int centerX = position.x;
        int centerY = position.y;
        int startX = centerX - (width / 2) * tileSize;
        int startY = centerY - (height / 2) * tileSize;

        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, minimapImage.getWidth(), minimapImage.getHeight());
        g.setComposite(AlphaComposite.SrcOver);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int tileX = startX + x * tileSize;
                int tileY = startY + y * tileSize;
                Tile tile = world.getTileAt(tileX, tileY, true);

                g.setColor(tile != null ? getColorForTile(tile) : Color.BLACK);
                g.fillRect(x, y, 1, 1);
            }
        }

        // Draw player position marker
        g.setColor(Color.RED);
        int playerMarkerX = width / 2;
        int playerMarkerY = height / 2;
        g.fillRect(playerMarkerX - 2, playerMarkerY - 2, 5, 5);

        g.dispose();
    }

    private Color getColorForTile(Tile tile) {
        switch (tile.type) {
            case "grass": return new Color(34, 139, 34);
            case "sand": return new Color(194, 178, 128);
            case "water": return new Color(65, 105, 225);
            case "forest": return new Color(0, 100, 0);
            default: return Color.BLACK;
        }
    }
}
