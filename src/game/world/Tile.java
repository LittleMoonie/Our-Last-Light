package src.game.world;

import src.game.constants.Config;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    public String type;
    public BufferedImage image;
    public boolean isObstacle = false;

    private static Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final int TILE_SIZE = Config.TILE_SIZE;

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
                BufferedImage original = ImageIO.read(getClass().getResourceAsStream(Config.TILE_IMAGE_PATH + type + ".png"));
                img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(original, 0, 0, TILE_SIZE, TILE_SIZE, null);
                g2d.dispose();
                imageCache.put(type, img);
            } catch (IOException e) {
                e.printStackTrace();
                img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return img;
    }

    public BufferedImage getImage() {
        return image;
    }
}
