// file: core/src/main/java/project/project/ui/InventoryClickHandler.java

package project.project.ui;

import project.project.components.InventoryComponent;
import project.project.entities.InventoryItem;

public class InventoryClickHandler {
    private InventoryComponent inventory;
    private InventoryItem pickedUpItem;
    private boolean isLeftClick;

    public InventoryClickHandler(InventoryComponent inventory) {
        this.inventory = inventory;
        this.pickedUpItem = null; // Tracks the currently picked up item
    }

    public void handleLeftClick(int row, int col) {
        InventoryItem item = inventory.getItem(row, col);

        if (pickedUpItem == null) {
            // Pick up the entire item stack
            if (item != null) {
                pickedUpItem = item;
                inventory.setItem(row, col, null); // Remove item from inventory temporarily
            }
        } else {
            if (item == null) {
                // Drop the picked-up item stack
                inventory.setItem(row, col, pickedUpItem);
                pickedUpItem = null;
            } else if (item.isSameType(pickedUpItem)) {
                // Merge if they are of the same type
                int totalQuantity = item.getQuantity() + pickedUpItem.getQuantity();
                if (totalQuantity <= 64) {
                    item.setQuantity(totalQuantity);
                    pickedUpItem = null;
                } else {
                    pickedUpItem.setQuantity(totalQuantity - 64);
                    item.setQuantity(64);
                }
            } else {
                // Swap items if they are different types
                inventory.setItem(row, col, pickedUpItem);
                pickedUpItem = item;
            }
        }
    }

    public void handleRightClick(int row, int col) {
        InventoryItem item = inventory.getItem(row, col);

        if (pickedUpItem == null) {
            // Pick up half of the item stack
            if (item != null) {
                int halfQuantity = item.getQuantity() / 2;
                pickedUpItem = new InventoryItem(
                    item.getType(),
                    halfQuantity,
                    item.getTexturePath(),
                    item.getItemType() // Pass the item type
                );
                item.setQuantity(item.getQuantity() - halfQuantity);

                if (item.getQuantity() == 0) {
                    inventory.setItem(row, col, null);
                }
            }
        } else {
            if (item == null) {
                // Drop one item from the picked-up stack
                inventory.setItem(row, col, new InventoryItem(
                    pickedUpItem.getType(),
                    1,
                    pickedUpItem.getTexturePath(),
                    pickedUpItem.getItemType() // Pass the item type
                ));
                pickedUpItem.setQuantity(pickedUpItem.getQuantity() - 1);

                if (pickedUpItem.getQuantity() == 0) {
                    pickedUpItem = null;
                }
            } else if (item.isSameType(pickedUpItem) && item.getQuantity() < 64) {
                // Add one to the existing stack
                item.setQuantity(item.getQuantity() + 1);
                pickedUpItem.setQuantity(pickedUpItem.getQuantity() - 1);

                if (pickedUpItem.getQuantity() == 0) {
                    pickedUpItem = null;
                }
            }
        }
    }

    public InventoryItem getPickedUpItem() {
        return pickedUpItem;
    }
}
