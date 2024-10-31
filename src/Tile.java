package src;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    String type;
    BufferedImage image;
    boolean isObstacle = false;

    // Static cache for images
    private static Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final int TILE_SIZE = GameConstants.TILE_SIZE;

    public Tile(String type) {
        this.type = type;
        this.image = getImageForType(type);
    }

    // Public method to change the tile type and update the image
    public void setType(String type) {
        this.type = type;
        this.image = getImageForType(type);
        // Update obstacle status if needed
        if (type.equals("forest") || type.equals("mountain")) {
            this.isObstacle = true;
        } else {
            this.isObstacle = false;
        }
    }

    // Keep this method private to maintain encapsulation
    private BufferedImage getImageForType(String type) {
        BufferedImage img = imageCache.get(type);
        if (img == null) {
            try {
                BufferedImage original = ImageIO.read(getClass().getResourceAsStream("/resources/" + type + ".png"));
                img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(original, 0, 0, TILE_SIZE, TILE_SIZE, null);
                g2d.dispose();
                imageCache.put(type, img);
            } catch (IOException e) {
                e.printStackTrace();
                // Create a placeholder image in case of error
                img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return img;
    }

    public BufferedImage getImage() {
        return image;
    }
}
