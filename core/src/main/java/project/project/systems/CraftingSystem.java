package project.project.systems;

import project.project.components.CraftingComponent;
import project.project.components.InventoryComponent;
import project.project.entities.InventoryItem;
import project.project.entities.ItemType;

import java.util.Map;

public class CraftingSystem {
    public static boolean craftItem(CraftingComponent crafting, InventoryComponent inventory, String outputItem, int quantity) {
        if (!crafting.hasRecipe(outputItem)) {
            return false; // No such recipe
        }

        Map<String, Integer> recipe = crafting.getRecipe(outputItem);

        // Check if inventory has all required items
        for (Map.Entry<String, Integer> entry : recipe.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue() * quantity;

            if (getTotalQuantity(inventory, itemName) < requiredQuantity) {
                return false; // Not enough resources
            }
        }

        // Remove required items
        for (Map.Entry<String, Integer> entry : recipe.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue() * quantity;

            removeItems(inventory, itemName, requiredQuantity);
        }

        // Add crafted item
        InventoryItem craftedItem = new InventoryItem(outputItem, quantity, outputItem + ".png", ItemType.BUILDING);
        InventorySystem inventorySystem = new InventorySystem();
        inventorySystem.addItem(inventory, craftedItem);

        return true; // Crafting successful
    }

    private static void removeItems(InventoryComponent inventory, String itemName, int quantity) {
        for (int row = 0; row < inventory.getRows(); row++) {
            for (int col = 0; col < inventory.getCols(); col++) {
                InventoryItem item = inventory.getItem(row, col);
                if (item != null && item.getName().equals(itemName)) {
                    if (item.getQuantity() >= quantity) {
                        item.setQuantity(item.getQuantity() - quantity);
                        if (item.getQuantity() == 0) {
                            inventory.clearSlot(row, col);
                        }
                        return;
                    } else {
                        quantity -= item.getQuantity();
                        inventory.clearSlot(row, col);
                    }
                }
            }
        }
    }

    public static boolean canCraft(CraftingComponent crafting, InventoryComponent inventory, String itemName) {
        Map<String, Integer> recipe = crafting.getRecipe(itemName);

        if (recipe == null) {
            return false; // No recipe found for the item
        }

        // Check if the inventory has all the required resources
        for (Map.Entry<String, Integer> ingredient : recipe.entrySet()) {
            String resourceName = ingredient.getKey();
            int requiredAmount = ingredient.getValue();
            int availableAmount = getTotalQuantity(inventory, resourceName);

            if (availableAmount < requiredAmount) {
                return false; // Not enough resources
            }
        }

        return true; // All resources are sufficient
    }

    private static int getTotalQuantity(InventoryComponent inventory, String resourceName) {
        int total = 0;

        for (int row = 0; row < inventory.getRows(); row++) {
            for (int col = 0; col < inventory.getCols(); col++) {
                InventoryItem item = inventory.getItem(row, col);
                if (item != null && item.getName().equals(resourceName)) {
                    total += item.getQuantity();
                }
            }
        }

        return total;
    }
}
