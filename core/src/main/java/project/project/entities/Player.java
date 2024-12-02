package project.project.entities;

import com.badlogic.gdx.math.Vector2;
import project.project.components.*;
import project.project.entities.enemies.Mob;
//import project.project.systems.AttackSystem;
import project.project.systems.InventorySystem;
import project.project.systems.ObjectPlacementSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Character {
    private PositionComponent position;

    // Inventory system: 6 rows, 9 columns
    private InventoryItem[][] inventory;

    // Listener to notify UI of inventory changes
    private Runnable inventoryUpdateListener;

//    private AttackSystem attackSystem = new AttackSystem();

    public Player(Vector2 startingTilePos) {
        super("Player");
        this.position = new PositionComponent(startingTilePos);
        addComponent(this.position);
        addComponent(new TextureComponent("player1.png", 50, 70));
        addComponent(new HealthComponent(100));
        addComponent(new MovementComponent(2));

        addComponent(new AttackComponent(10, 100)); // Ajout de l'AttackComponent

        addComponent(new HitboxComponent(50, 70));

        // Inventory
        InventoryComponent inventory = new InventoryComponent(6, 9);
        addComponent(inventory);

        // Add predefined items
        InventorySystem inventorySystem = new InventorySystem();

        // Materials
        inventorySystem.addItem(inventory, new InventoryItem("wood", 64, "wood.png", ItemType.MATERIAL));
        inventorySystem.addItem(inventory, new InventoryItem("stone", 64, "stone.png", ItemType.MATERIAL));

        // Tools
        inventorySystem.addItem(inventory, new InventoryItem("sword", 1, "sword.png", ItemType.TOOL));
        inventorySystem.addItem(inventory, new InventoryItem("pickaxe", 1, "pickaxe.png", ItemType.TOOL));
        inventorySystem.addItem(inventory, new InventoryItem("axe", 1, "axe.png", ItemType.TOOL));

        // Consumables
        inventorySystem.addItem(inventory, new InventoryItem("healing potion", 5, "healing_potion.png", ItemType.CONSUMABLE));
        inventorySystem.addItem(inventory, new InventoryItem("steak", 10, "steak.png", ItemType.CONSUMABLE));

        // Building items
        inventorySystem.addItem(inventory, new InventoryItem("campfire", 1, "campfire.png", ItemType.BUILDING));

        CraftingComponent crafting = new CraftingComponent();

        // Add recipes for crafting
        Map<String, Integer> campfireRecipe = new HashMap<>();
        campfireRecipe.put("wood", 5);
        campfireRecipe.put("stone", 3);

        Map<String, Integer> chestRecipe = new HashMap<>();
        chestRecipe.put("wood", 8);

        crafting.addRecipe("campfire", campfireRecipe);
        crafting.addRecipe("chest", chestRecipe);

        this.addComponent(crafting);

        ObjectPlacementSystem placementSystem = new ObjectPlacementSystem();

        Character campfire = new Character("Campfire");
        campfire.addComponent(new PlacementComponent(true, 1, 1));
        campfire.addComponent(new HitboxComponent(32, 32));

        Character chest = new Character("Chest");
        chest.addComponent(new PlacementComponent(true, 1, 1));
        chest.addComponent(new HitboxComponent(32, 32));

        placementSystem.placeObject(this, campfire, new Vector2(5, 5), false);
        placementSystem.placeObject(this, chest, new Vector2(6, 5), false);
    }

    public Vector2 getWorldPosition() {
        return position.worldPos;
    }

    public void setWorldPosition(float x, float y) {
        position.setWorldPosition(x, y);
    }

    /**
     * Adds an item to the inventory.
     * If the item already exists in a stack, it increments the quantity.
     * If the item is new, it finds the first empty slot to add it.
     */
    public boolean addItemToInventory(String name, int quantity, String texturePath, ItemType itemType) {
        // Try to stack the item in an existing slot
        for (int row = 0; row < inventory.length; row++) {
            for (int col = 0; col < inventory[row].length; col++) {
                if (inventory[row][col] != null && inventory[row][col].getName().equals(name) &&
                    inventory[row][col].getQuantity() < 64) {
                    int newQuantity = Math.min(inventory[row][col].getQuantity() + quantity, 64);
                    inventory[row][col].setQuantity(newQuantity);
                    notifyInventoryUpdate();
                    return true;
                }
            }
        }

        // Add the item to the first empty slot
        for (int row = 0; row < inventory.length; row++) {
            for (int col = 0; col < inventory[row].length; col++) {
                if (inventory[row][col] == null) {
                    inventory[row][col] = new InventoryItem(name, quantity, texturePath, itemType);
                    notifyInventoryUpdate();
                    return true;
                }
            }
        }

        // Inventory is full
        return false;
    }

    /**
     * Removes a specified quantity of an item from the inventory.
     * If the quantity reaches zero, the slot is cleared.
     */
    public boolean removeItemFromInventory(String name, int quantity) {
        for (int row = 0; row < inventory.length; row++) {
            for (int col = 0; col < inventory[row].length; col++) {
                if (inventory[row][col] != null && inventory[row][col].getName().equals(name)) {
                    if (inventory[row][col].getQuantity() >= quantity) {
                        inventory[row][col].setQuantity(inventory[row][col].getQuantity() - quantity);
                        if (inventory[row][col].getQuantity() == 0) {
                            clearInventorySlot(row, col);
                        }
                        notifyInventoryUpdate();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Clears a specific inventory slot.
     */
    public void clearInventorySlot(int row, int col) {
        if (row >= 0 && row < inventory.length && col >= 0 && col < inventory[row].length) {
            if (inventory[row][col] != null) {
                inventory[row][col].dispose(); // Dispose of the texture
                inventory[row][col] = null; // Clear the slot
            }
            notifyInventoryUpdate();
        }
    }

    /**
     * Clears all inventory slots.
     */
    public void clearInventory() {
        for (int row = 0; row < inventory.length; row++) {
            for (int col = 0; col < inventory[row].length; col++) {
                clearInventorySlot(row, col);
            }
        }
    }

    /**
     * Sets a listener to notify when the inventory is updated.
     */
    public void setInventoryUpdateListener(Runnable listener) {
        this.inventoryUpdateListener = listener;
    }

    public void notifyInventoryUpdate() {
        if (inventoryUpdateListener != null) {
            inventoryUpdateListener.run();
        }
    }

    public InventoryItem[][] getInventory() {
        return inventory;
    }


    public Vector2 getPosition() {
        return position.worldPos;
    }
}
