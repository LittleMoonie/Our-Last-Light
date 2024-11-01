package src.game.world;

import src.game.constants.Config;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tile implements Serializable {
    private static final long serialVersionUID = 1L;

    public String type;
    public String resource;
    private transient BufferedImage image; // Mark as transient
    private boolean resourceGenerated = false;
    private transient Random random = new Random(); // for regrowth
    public boolean isObstacle;

    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final int TILE_SIZE = Config.TILE_SIZE;

    public Tile(String type, String resource) {
        this.type = type;
        this.resource = resource;
        this.isObstacle = (resource != null && (resource.equals("tree") || resource.equals("stone")));
        this.image = loadImage(type, resource);
    }

    public BufferedImage getImage() {
        if (image == null) {
            image = loadImage(type, resource); // Reload if missing
        }
        return image;
    }

    public boolean hasResource() {
        return resource != null;
    }

    public void removeResource() {
        this.resource = null;
        this.isObstacle = false;
        this.image = loadImage(type, null); // Update image to reflect no resource
    }

    public void updateRegrowth() {
        if (resource == null && resourceGenerated) {
            double regrowthChance = type.equals("forest") ? Config.TREE_REGROWTH_CHANCE : Config.STONE_REGROWTH_CHANCE;
            if (random.nextDouble() < regrowthChance) {
                this.resource = type.equals("forest") ? "tree" : "stone";
                this.isObstacle = true;
                this.image = loadImage(type, resource); // Update image to reflect resource regrowth
            }
        }
    }

    private BufferedImage loadImage(String type, String resource) {
        String cacheKey = type + (resource != null ? "_" + resource : "");
        BufferedImage img = imageCache.get(cacheKey);

        if (img == null) {
            try {
                String imagePath = Config.TILE_IMAGE_PATH + (resource != null ? resource : type) + ".png";
                BufferedImage original = ImageIO.read(getClass().getResourceAsStream(imagePath));

                if (original == null) {
                    throw new IOException("Image not found at path: " + imagePath);
                }

                img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(original, 0, 0, TILE_SIZE, TILE_SIZE, null);
                g2d.dispose();

                imageCache.put(cacheKey, img);
            } catch (IOException e) {
                System.err.println("Error loading image for tile type '" + type + "' with resource '" + resource + "': " + e.getMessage());
                img = createFallbackImage();
                imageCache.put(cacheKey, img);
            }
        }
        return img;
    }

    private BufferedImage createFallbackImage() {
        BufferedImage fallback = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallback.createGraphics();
        g2d.setColor(java.awt.Color.GRAY);
        g2d.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
        g2d.dispose();
        return fallback;
    }

    // Deserialize and reload transient fields
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.random = new Random();
        this.image = loadImage(type, resource); // Reload image after deserialization
    }
}
