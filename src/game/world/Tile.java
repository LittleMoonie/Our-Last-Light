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

    public Tile(String type) {
        this.type = type;
        this.image = getImageForType(type);
        this.isObstacle = type.equals("forest") || type.equals("mountain");
    }

    private BufferedImage getImageForType(String type) {
        BufferedImage img = imageCache.get(type);
        if (img == null) {
            String path = null;
            try {
                // Adjusted path to match /resources/<tile_type>.png
                path = "/resources/" + type + ".png";
                System.out.println("Attempting to load tile image at path: " + path); // Log the path being loaded

                img = ImageIO.read(getClass().getResourceAsStream(path));

                if (img == null) {
                    throw new IOException("Image file not found at path: " + path);
                }

                imageCache.put(type, img);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading image for tile type: " + type + " at path: " + path);

                // Provide a fallback image in case the file is missing
                img = new BufferedImage(Config.TILE_WIDTH, Config.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.MAGENTA); // Visible color for missing images
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
