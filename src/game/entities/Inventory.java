package src.game.entities;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private static final int INVENTORY_SIZE = 64; // Total slots in main inventory
    private static final int STACK_LIMIT = 32;    // Max items per slot
    private static final int HAND_SIZE = 9;       // Active hand slots

    private final InventorySlot[] mainInventory; // Main inventory slots
    private final InventorySlot[] activeHand;    // Active hand slots

    public Inventory() {
        mainInventory = new InventorySlot[INVENTORY_SIZE];
        activeHand = new InventorySlot[HAND_SIZE];

        // Initialize slots
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            mainInventory[i] = new InventorySlot();
        }
        for (int i = 0; i < HAND_SIZE; i++) {
            activeHand[i] = new InventorySlot();
        }
    }

    public Map<String, Integer> getItems() {
        Map<String, Integer> allItems = new HashMap<>();

        // Aggregate items from main inventory
        for (InventorySlot slot : mainInventory) {
            if (!slot.isEmpty()) {
                allItems.merge(slot.getItem(), slot.getQuantity(), Integer::sum);
            }
        }

        // Aggregate items from active hand
        for (InventorySlot slot : activeHand) {
            if (!slot.isEmpty()) {
                allItems.merge(slot.getItem(), slot.getQuantity(), Integer::sum);
            }
        }

        return allItems;
    }

    public void setItems(Map<String, Integer> loadedItems) {
        for (Map.Entry<String, Integer> entry : loadedItems.entrySet()) {
            addItem(entry.getKey(), entry.getValue());
        }
    }

    public boolean addItem(String itemName, int quantity) {
        // Add to existing slots or create a new slot if space allows
        for (InventorySlot slot : mainInventory) {
            if (slot.isEmpty() || (slot.getItem().equals(itemName) && slot.getQuantity() < STACK_LIMIT)) {
                int spaceLeft = STACK_LIMIT - slot.getQuantity();
                int toAdd = Math.min(quantity, spaceLeft);
                slot.addItems(itemName, toAdd);
                quantity -= toAdd;
                if (quantity <= 0) return true; // Successfully added
            }
        }
        return false; // Inventory full
    }

    public boolean removeItem(String itemName, int quantity) {
        for (InventorySlot slot : mainInventory) {
            if (slot.getItem().equals(itemName)) {
                if (slot.getQuantity() >= quantity) {
                    slot.removeItems(quantity);
                    return true;
                } else {
                    quantity -= slot.getQuantity();
                    slot.clear();
                }
            }
        }
        return false; // Not enough items found
    }

    public InventorySlot[] getMainInventory() {
        return mainInventory;
    }

    public InventorySlot[] getActiveHand() {
        return activeHand;
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        int slotSize = 40;
        int x = 20;
        int y = 20;
        g2.setColor(Color.WHITE);
        g2.drawString("Inventory:", x, y);

        y += 30;
        int rowLength = 8;

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            int slotX = x + (i % rowLength) * slotSize;
            int slotY = y + (i / rowLength) * slotSize;
            drawSlot(g2, mainInventory[i], slotX, slotY, slotSize);
        }

        // Draw active hand at the bottom
        int handStartX = (screenWidth - HAND_SIZE * slotSize) / 2;
        int handStartY = screenHeight - slotSize - 20;

        for (int i = 0; i < HAND_SIZE; i++) {
            drawSlot(g2, activeHand[i], handStartX + i * slotSize, handStartY, slotSize);
        }
    }

    private void drawSlot(Graphics2D g2, InventorySlot slot, int x, int y, int slotSize) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(x, y, slotSize, slotSize);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, slotSize, slotSize);

        if (!slot.isEmpty()) {
            g2.drawString(slot.getItem(), x + 5, y + 15);
            g2.drawString("x" + slot.getQuantity(), x + 5, y + 30);
        }
    }

    public class InventorySlot {
        private String item;
        private int quantity;

        public boolean isEmpty() {
            return quantity == 0;
        }

        public String getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public void addItems(String item, int quantity) {
            if (isEmpty()) {
                this.item = item;
            }
            this.quantity += quantity;
        }

        public void removeItems(int quantity) {
            this.quantity -= quantity;
            if (this.quantity <= 0) clear();
        }

        public void clear() {
            this.item = null;
            this.quantity = 0;
        }
    }
}
