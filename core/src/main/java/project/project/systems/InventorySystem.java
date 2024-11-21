package project.project.systems;

import project.project.components.InventoryComponent;
import project.project.entities.InventoryItem;
import project.project.entities.ItemType;

public class InventorySystem {

    /**
     * Adds an item to the inventory. Attempts to stack if possible.
     *
     * @param inventory The inventory component to add the item to.
     * @param item      The item to add.
     * @return true if the item was successfully added, false if the inventory is full.
     */
    public boolean addItem(InventoryComponent inventory, InventoryItem item) {
        // Check if the item can be added to the hotbar (row 0)
        if (item.getItemType() == ItemType.WEAPON || item.getItemType() == ItemType.CONSUMABLE) {
            for (int col = 0; col < inventory.getCols(); col++) {
                InventoryItem existingItem = inventory.getItem(0, col); // Hotbar is row 0
                if (existingItem != null && existingItem.getName().equals(item.getName())
                    && existingItem.getQuantity() < 64) {
                    int spaceLeft = 64 - existingItem.getQuantity();
                    int quantityToAdd = Math.min(spaceLeft, item.getQuantity());
                    existingItem.setQuantity(existingItem.getQuantity() + quantityToAdd);
                    item.setQuantity(item.getQuantity() - quantityToAdd);
                    if (item.getQuantity() == 0) {
                        return true; // Fully added to hotbar
                    }
                }
            }

            for (int col = 0; col < inventory.getCols(); col++) {
                if (inventory.getItem(0, col) == null) { // Check for an empty slot in the hotbar
                    inventory.setItem(0, col, new InventoryItem(
                        item.getName(),
                        item.getQuantity(),
                        item.getTexture().filePath,
                        item.getItemType()
                    ));
                    return true; // Fully added to hotbar
                }
            }
        }

        // If the item cannot go in the hotbar or the hotbar is full, add it to the main inventory
        for (int row = 1; row < inventory.getRows(); row++) { // Start from row 1 to exclude the hotbar
            for (int col = 0; col < inventory.getCols(); col++) {
                InventoryItem existingItem = inventory.getItem(row, col);
                if (existingItem != null && existingItem.getName().equals(item.getName())
                    && existingItem.getQuantity() < 64) {
                    int spaceLeft = 64 - existingItem.getQuantity();
                    int quantityToAdd = Math.min(spaceLeft, item.getQuantity());
                    existingItem.setQuantity(existingItem.getQuantity() + quantityToAdd);
                    item.setQuantity(item.getQuantity() - quantityToAdd);
                    if (item.getQuantity() == 0) {
                        return true; // Fully added to inventory
                    }
                }
            }

            for (int col = 0; col < inventory.getCols(); col++) {
                if (inventory.getItem(row, col) == null) { // Check for an empty slot in the main inventory
                    inventory.setItem(row, col, new InventoryItem(
                        item.getName(),
                        item.getQuantity(),
                        item.getTexture().filePath,
                        item.getItemType()
                    ));
                    return true; // Fully added to inventory
                }
            }
        }

        return false; // Inventory full
    }

    /**
     * Removes a specified quantity of an item from the inventory.
     *
     * @param inventory The inventory component to remove the item from.
     * @param name      The name of the item to remove.
     * @param quantity  The quantity to remove.
     * @return true if the quantity was successfully removed, false if there wasn't enough.
     */
    public boolean removeItem(InventoryComponent inventory, String name, int quantity) {
        for (int row = 0; row < inventory.getRows(); row++) {
            for (int col = 0; col < inventory.getCols(); col++) {
                InventoryItem item = inventory.getItem(row, col);
                if (item != null && item.getName().equals(name)) {
                    if (item.getQuantity() >= quantity) {
                        item.setQuantity(item.getQuantity() - quantity);
                        if (item.getQuantity() == 0) {
                            inventory.clearSlot(row, col);
                        }
                        return true; // Fully removed
                    } else {
                        quantity -= item.getQuantity();
                        inventory.clearSlot(row, col);
                    }
                }
            }
        }
        return false; // Not enough items
    }

    /**
     * Transfers an item between two inventories.
     *
     * @param source      The source inventory.
     * @param target      The target inventory.
     * @param sourceRow   The row in the source inventory.
     * @param sourceCol   The column in the source inventory.
     * @param quantity    The quantity to transfer.
     * @return true if the transfer was successful, false otherwise.
     */
    public boolean transferItem(InventoryComponent source, InventoryComponent target, int sourceRow, int sourceCol,
                                int quantity) {
        InventoryItem sourceItem = source.getItem(sourceRow, sourceCol);
        if (sourceItem == null || sourceItem.getQuantity() < quantity) {
            return false; // Not enough items to transfer
        }

        // Pass the ItemType to the constructor
        InventoryItem transferItem = new InventoryItem(
            sourceItem.getName(),
            quantity,
            sourceItem.getTexture().filePath,
            sourceItem.getItemType()
        );

        if (addItem(target, transferItem)) {
            sourceItem.setQuantity(sourceItem.getQuantity() - quantity);
            if (sourceItem.getQuantity() == 0) {
                source.clearSlot(sourceRow, sourceCol);
            }
            return true;
        }

        return false; // Target inventory full
    }
}
