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

    public Player(Vector2 startingTilePos) {
        super("Player");
        this.position = new PositionComponent(startingTilePos);
        addComponent(this.position);
        addComponent(new TextureComponent("player1.png", 50, 70));
        addComponent(new HealthComponent(75));
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

        // Weapons
        inventorySystem.addItem(inventory, new InventoryItem("wooden_sword", 1, "wooden_sword.png", ItemType.TOOL));
        inventorySystem.addItem(inventory, new InventoryItem("stone_sword", 1, "stone_sword.png", ItemType.TOOL));

        // Tools
        inventorySystem.addItem(inventory, new InventoryItem("stone_pickaxe", 1, "stone_pickaxe.png", ItemType.TOOL));
        inventorySystem.addItem(inventory, new InventoryItem("stone_axe", 1, "stone_axe.png", ItemType.TOOL));

        // Consumables
        inventorySystem.addItem(inventory, new InventoryItem("healing potion", 5, "healing_potion.png", ItemType.CONSUMABLE));
        inventorySystem.addItem(inventory, new InventoryItem("apple", 10, "apple.png", ItemType.CONSUMABLE));

        // Building items
        inventorySystem.addItem(inventory, new InventoryItem("campfire", 1, "campfire.png", ItemType.BUILDING));
        inventorySystem.addItem(inventory, new InventoryItem("chest", 1, "chest.png", ItemType.BUILDING));

        CraftingComponent crafting = new CraftingComponent();

        // Add recipes for crafting
        Map<String, Integer> campfireRecipe = new HashMap<>();
        campfireRecipe.put("wood", 5);
        campfireRecipe.put("stone", 3);

        Map<String, Integer> chestRecipe = new HashMap<>();
        chestRecipe.put("wood", 8);

        Map<String, Integer> woodenSwordRecipe = new HashMap<>();
        woodenSwordRecipe.put("wood", 5);
        woodenSwordRecipe.put("stone", 2);

        Map<String, Integer> stonePickaxeRecipe = new HashMap<>();
        stonePickaxeRecipe.put("wood", 3);
        stonePickaxeRecipe.put("stone", 5);

        Map<String, Integer> stoneAxeRecipe = new HashMap<>();
        stoneAxeRecipe.put("wood", 3);
        stoneAxeRecipe.put("stone", 5);

        Map<String, Integer> stoneSwordRecipe = new HashMap<>();
        stoneSwordRecipe.put("wood", 3);
        stoneSwordRecipe.put("stone", 5);

        Map<String, Integer> healingPotionRecipe = new HashMap<>();
        healingPotionRecipe.put("apple", 1);
        healingPotionRecipe.put("stone", 1);


        crafting.addRecipe("campfire", campfireRecipe);
        crafting.addRecipe("chest", chestRecipe);

        crafting.addRecipe("wooden_sword", woodenSwordRecipe);
        crafting.addRecipe("stone_sword", stoneSwordRecipe);

        crafting.addRecipe("stone_pickaxe", stonePickaxeRecipe);
        crafting.addRecipe("stone_axe", stoneAxeRecipe);

        crafting.addRecipe("healing_potion", healingPotionRecipe);

        this.addComponent(crafting);

        Character campfire = new Character("Campfire");
        campfire.addComponent(new PlacementComponent(true, 1, 1));
        campfire.addComponent(new HitboxComponent(32, 32));

        Character chest = new Character("Chest");
        chest.addComponent(new PlacementComponent(true, 1, 1));
        chest.addComponent(new HitboxComponent(32, 32));
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
    public void setTexture(String texturePath) {
        TextureComponent textureComponent = this.getComponent(TextureComponent.class);
        if (textureComponent != null) {
            textureComponent.setTexture(texturePath); // Assuming TextureComponent has this method
        }
    }

}
