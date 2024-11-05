package src.game.components;

import java.util.HashMap;
import java.util.Map;

public class InventoryComponent implements Component {
    private Map<String, Integer> items;

    public InventoryComponent() {
        items = new HashMap<>();
    }

    // Adds a specified quantity of an item to the inventory
    public void addItem(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
    }

    // Removes a specified quantity of an item from the inventory
    public void removeItem(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) - quantity);
        if (items.get(itemName) <= 0) {
            items.remove(itemName);
        }
    }

    // Retrieves the quantity of a specific item, returns 0 if the item doesn't exist
    public int getItemQuantity(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    // Checks if the inventory contains a specific item
    public boolean hasItem(String itemName) {
        return items.containsKey(itemName) && items.get(itemName) > 0;
    }

    // Returns a copy of all items in the inventory
    public Map<String, Integer> getItems() {
        return new HashMap<>(items);
    }

    // Clears all items from the inventory
    public void clearInventory() {
        items.clear();
    }

    // Set the inventory items from an external source, such as saved game data
    public void setItems(Map<String, Integer> newItems) {
        items.clear();
        items.putAll(newItems);
    }

    // Returns the total number of unique item types in the inventory
    public int getUniqueItemCount() {
        return items.size();
    }

    @Override
    public String toString() {
        return "InventoryComponent{" + "items=" + items + '}';
    }
}
