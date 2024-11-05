package src.game.systems;

import src.game.components.InventoryComponent;
import src.game.entities.Entity;

import java.awt.*;
import java.util.Map;

public class InventorySystem extends System {

    @Override
    public void update(Entity entity, float deltaTime) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        if (inventory != null) {
            // Logic for managing inventory updates
            handleInventoryActions(entity, inventory);
        }
    }

    // Method to add an item to an entity's inventory
    public void addItem(Entity entity, String itemName, int quantity) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        if (inventory != null) {
            inventory.addItem(itemName, quantity);
            java.lang.System.out.println("Added " + quantity + " of " + itemName + " to " + entity);
        }
    }

    // Method to remove an item from an entity's inventory
    public void removeItem(Entity entity, String itemName, int quantity) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        if (inventory != null && inventory.hasItem(itemName)) {
            inventory.removeItem(itemName, quantity);
            java.lang.System.out.println("Removed " + quantity + " of " + itemName + " from " + entity);
        }
    }

    // Checks if the entity has a specific item
    public boolean hasItem(Entity entity, String itemName) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        return inventory != null && inventory.hasItem(itemName);
    }

    // Example method for handling specific inventory actions
    private void handleInventoryActions(Entity entity, InventoryComponent inventory) {
        // Example: Check conditions to consume or use items
        if (shouldConsumeItem(entity, "HealthPotion")) {
            consumeItem(entity, "HealthPotion", 1);
        }
    }

    // Method to consume an item and potentially trigger effects (e.g., heal the player)
    private void consumeItem(Entity entity, String itemName, int quantity) {
        if (hasItem(entity, itemName)) {
            removeItem(entity, itemName, quantity);
            applyItemEffect(entity, itemName);  // Apply effects specific to the item
        }
    }

    // Apply the effects of an item (e.g., healing, increasing stamina)
    private void applyItemEffect(Entity entity, String itemName) {
        // Placeholder for item effect logic; modify based on item types
        if (itemName.equals("HealthPotion")) {
            // Example: Increase health by a specific amount
            java.lang.System.out.println("Entity " + entity + " used a HealthPotion and gained health!");
            // Would need to interact with a HealthComponent and HealthSystem here
        }
    }

    // Example condition to decide if an item should be consumed
    private boolean shouldConsumeItem(Entity entity, String itemName) {
        // Placeholder for actual game logic that decides if item consumption is needed
        return false;  // Modify as needed
    }

    // Method to print out the current inventory contents for debugging
    public void printInventory(Entity entity) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        if (inventory != null) {
            java.lang.System.out.println("Inventory of " + entity + ": " + inventory.getItems());
        }
    }


    // Method to draw the inventory contents
    public void drawInventory(Graphics2D g2, Entity entity, int screenWidth, int screenHeight) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        if (inventory == null) return;

        // Define dimensions and styles for the inventory display
        int panelWidth = 200;
        int panelHeight = 300;
        int padding = 10;
        int startX = screenWidth - panelWidth - padding;
        int startY = padding;

        // Draw the inventory panel background
        g2.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black
        g2.fillRect(startX, startY, panelWidth, panelHeight);

        // Draw a border around the panel
        g2.setColor(Color.WHITE);
        g2.drawRect(startX, startY, panelWidth, panelHeight);

        // Set font and color for inventory text
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);

        // Draw inventory title
        g2.drawString("Inventory", startX + padding, startY + padding + 15);

        // Draw each item in the inventory with its quantity
        int itemY = startY + padding + 40; // Start position for item list
        for (Map.Entry<String, Integer> entry : inventory.getItems().entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            String itemDisplay = itemName + " x" + quantity;

            // Draw the item text
            g2.drawString(itemDisplay, startX + padding, itemY);

            // Move down for the next item
            itemY += 20;
        }

        // If there are no items in the inventory
        if (inventory.getItems().isEmpty()) {
            g2.drawString("Empty", startX + padding, itemY);
        }
    }
}
