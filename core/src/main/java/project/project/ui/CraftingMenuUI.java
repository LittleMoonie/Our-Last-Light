package project.project.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import project.project.components.CraftingComponent;
import project.project.components.InventoryComponent;
import project.project.systems.CraftingSystem;

import java.util.Map;

public class CraftingMenuUI {
    private Table craftingTable;
    private Stage stage;
    private CraftingComponent crafting;
    private InventoryComponent inventory;
    private CraftingSystem craftingSystem;

    public CraftingMenuUI(Stage stage, CraftingComponent crafting, InventoryComponent inventory) {
        this.stage = stage;
        this.crafting = crafting;
        this.inventory = inventory;
        this.craftingSystem = new CraftingSystem();

        craftingTable = new Table();
        populateCraftingMenu();

        craftingTable.setPosition(100, 100); // Position it properly
        stage.addActor(craftingTable);
    }

    private void populateCraftingMenu() {
        craftingTable.clear();

        for (Map.Entry<String, Map<String, Integer>> recipe : crafting.getAllRecipes().entrySet()) {
            String itemName = recipe.getKey();
            TextButton craftButton = new TextButton("Craft " + itemName, new Skin());
            craftButton.addListener(event -> {
                craftingSystem.craftItem(crafting, inventory, itemName, 1);
                return true;
            });

            craftingTable.add(craftButton).pad(10);
            craftingTable.row();
        }
    }
}
