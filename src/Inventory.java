package src;

import java.awt.Graphics2D;
import java.awt.Color;

public class Inventory {
    private int wood = 0;

    public void addWood(int amount) {
        wood += amount;
    }

    public void draw(Graphics2D g2, int screenWidth) {
        g2.setColor(Color.WHITE);
        g2.drawString("Wood: " + wood, screenWidth - 100, 20);
    }
}
