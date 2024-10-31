package src.game.entities;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String, Integer> items;

    public Inventory() {
        items = new HashMap<>();
    }

    public void addItem(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
    }

    public void removeItem(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) - quantity);
        if (items.get(itemName) <= 0) {
            items.remove(itemName);
        }
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public void draw(Graphics2D g2, int screenWidth) {
        // Display the inventory items on the screen
        int x = 20;
        int y = 20;
        g2.setColor(Color.WHITE);
        g2.drawString("Inventory:", x, y);

        y += 20;
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            g2.drawString(itemName + ": " + quantity, x, y);
            y += 20;
        }
    }
}
