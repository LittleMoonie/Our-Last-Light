package src.game.world;

import src.game.constants.Config;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    public String type;
    public BufferedImage image;
    public boolean isObstacle = false;

    private static Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final int TILE_WIDTH = Config.TILE_WIDTH;

    public Tile(String type) {
        this.type = type;
        this.image = getImageForType(type);
    }

    public void setType(String type) {
        this.type = type;
        this.image = getImageForType(type);
        this.isObstacle = type.equals("forest") || type.equals("mountain");
    }

    private BufferedImage getImageForType(String type) {
        BufferedImage img = imageCache.get(type);
        if (img == null) {
            try {
                String path = Config.TILE_IMAGE_PATH + type + ".png";
                img = ImageIO.read(getClass().getResourceAsStream(path));

                if (img == null) {
                    throw new IOException("Image file not found at path: " + path);
                }

                // Resize image to isometric dimensions
                BufferedImage resizedImg = new BufferedImage(Config.TILE_WIDTH, Config.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImg.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(img, 0, 0, Config.TILE_WIDTH, Config.TILE_HEIGHT, null);
                g2d.dispose();

                img = resizedImg;
                imageCache.put(type, img);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading image for tile type: " + type + " at path: " + Config.TILE_IMAGE_PATH + type + ".png");

                // Provide a fallback image
                img = new BufferedImage(Config.TILE_WIDTH, Config.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.MAGENTA); // Use a visible color for missing images
                g.fillRect(0, 0, Config.TILE_WIDTH, Config.TILE_HEIGHT);
                g.dispose();
            }
        }
        return img;
    }



    public BufferedImage getImage() {
        return image;
    }
}
