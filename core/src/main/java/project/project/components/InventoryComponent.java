package project.project.components;

import project.project.entities.InventoryItem;

public class InventoryComponent implements Component {
    private InventoryItem[][] items; // Represents the player's inventory
    private final int rows;
    private final int cols;

    public InventoryComponent(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        items = new InventoryItem[rows][cols];
    }

    public InventoryItem getItem(int row, int col) {
        // Check if the requested slot is valid
        if (isValidSlot(row, col)) {
            return items[row][col];
        } else {
            return null; // Return null if the slot is invalid
        }
    }

    public void setItem(int row, int col, InventoryItem item) {
        // Set an item in a specific slot if the slot is valid
        if (isValidSlot(row, col)) {
            items[row][col] = item;
        }
    }

    public void clearSlot(int row, int col) {
        // Clear a specific slot if the slot is valid
        if (isValidSlot(row, col)) {
            items[row][col] = null;
        }
    }

    public boolean isValidSlot(int row, int col) {
        // Check if the specified row and column are within the bounds of the inventory
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
