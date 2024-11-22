package project.project.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TextureUtils {

    /**
     * Creates a solid texture with the given dimensions and color.
     *
     * @param width The width of the texture.
     * @param height The height of the texture.
     * @param color The color of the texture.
     * @return The created Texture object.
     */
    public static Texture createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    /**
     * Creates a texture with rounded corners.
     *
     * @param width        The width of the texture.
     * @param height       The height of the texture.
     * @param color        The color of the texture.
     * @param cornerRadius The radius of the corners.
     * @return The created Texture object with rounded corners.
     */

    public static Texture createRoundedTexture(int width, int height, Color color, int cornerRadius) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);

        // Clear the pixmap
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // Fill the rounded rectangle
        pixmap.setColor(color);

        // Draw the rounded body
        pixmap.fillRectangle(cornerRadius, 0, width - 2 * cornerRadius, height);
        pixmap.fillRectangle(0, cornerRadius, width, height - 2 * cornerRadius);

        // Draw the four rounded corners
        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, cornerRadius, cornerRadius);
        pixmap.fillCircle(cornerRadius, height - cornerRadius - 1, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius);

        // Convert Pixmap to Texture
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
