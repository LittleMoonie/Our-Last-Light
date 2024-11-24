package project.project.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import project.project.Constants;
import project.project.components.*;
import project.project.entities.InventoryItem;
import project.project.entities.ItemType;
import project.project.entities.Player;
import project.project.entities.Character;
import project.project.systems.CraftingSystem;
import project.project.systems.ObjectPlacementSystem;

import java.util.HashMap;
import java.util.Map;

public class InventoryUI {
    private Table inventoryTable;
    private Table hotbarTable;
    private Table craftingTable; // Added crafting table
    private Group hotbarGroup; // For hotbar inside inventory UI
    private Player player;
    private Stage stage;
    private ObjectPlacementSystem placementSystem;

    private BitmapFont font;
    private Texture brownSquareTexture;
    private Texture graySquareTexture;

    private InventoryItem pickedUpItem = null;
    private Image draggingItemImage = null; // Image for the item currently being dragged

    private boolean isVisible = false;
    private Table craftingNavigationTable; // For crafting navigation
    private Texture backgroundTexture;
    private Texture slotTexture;
    private Texture buttonTexture;
    private Texture navigationTexture;

    private Image craftingNavigationBackground; // Background for crafting navigation
    private Image craftingMenuBackground; // Background for crafting menu

    public InventoryUI(Stage stage, Player player, ObjectPlacementSystem placementSystem) {
        this.stage = stage;
        this.player = player;
        this.placementSystem = placementSystem;

        // Initialize font and textures
        font = new BitmapFont();
        brownSquareTexture = createRoundedTexture(58, 58, new Color(0.6f, 0.3f, 0.0f, 0.5f), 8); // Brown texture for inventory slots with 8px corner radius
        graySquareTexture = createRoundedTexture(150, 50, new Color(0.3f, 0.3f, 0.3f, 0.5f), 10); // Gray texture for crafting buttons with 10px corner radius

        backgroundTexture = createTexture(600, 400, new Color(0.5f, 0.25f, 0.25f, 1)); // Soft red-brown
        slotTexture = createTexture(64, 64, new Color(0.3f, 0.3f, 0.3f, 1)); // Dark gray for slots
        navigationTexture = createRoundedTexture(200, 50, new Color(0.4f, 0.3f, 0.2f, 1), 10); // Rounded corners for navigation buttons
        buttonTexture = createRoundedTexture(150, 50, new Color(0.6f, 0.4f, 0.2f, 1), 10);     // Rounded corners for crafting buttons
        // Initialize UI components
        inventoryTable = new Table();
        craftingTable = new Table();
        craftingNavigationTable = new Table();
        hotbarTable = new Table(); // Ensure hotbarTable is initialized
        hotbarGroup = new Group();

        // Create and add background
        createBackground();

        // Populate and initialize UI components
        populateInventoryUI();
        initializeCraftingNavigationUI();
        populateHotbarUI(); // Ensure hotbarTable is populated before positioning

        // Initialize positions
        initializeUIPositions();

        // Add actors to the stage in the correct order
        stage.addActor(craftingMenuBackground);       // Background
        stage.addActor(craftingNavigationBackground); // Crafting navigation background
        stage.addActor(craftingNavigationTable); // Crafting navigation
        stage.addActor(inventoryTable);       // Inventory
        stage.addActor(craftingTable);        // Crafting table
        stage.addActor(hotbarGroup);          // Hotbar

        // Initialize dragging item image
        draggingItemImage = new Image();
        draggingItemImage.setVisible(false);
        stage.addActor(draggingItemImage); // Dragging image
    }

    private Texture createPlaceholderTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void createBackground() {

        // Initialize crafting menu background
        if (craftingMenuBackground == null) {
            craftingMenuBackground = new Image(new TextureRegionDrawable(new TextureRegion(
                    createRoundedTexture(600, 400, new Color(0.15f, 0.15f, 0.15f, 0.75f), 15) // 15px corner radius
            )));
            craftingMenuBackground.setSize(600, 400); // Placeholder size
            stage.addActor(craftingMenuBackground); // Add before crafting UI
        }

        // Initialize crafting navigation background
        if (craftingNavigationBackground == null) {
            craftingNavigationBackground = new Image(new TextureRegionDrawable(new TextureRegion(
                    createRoundedTexture(250, 600, new Color(0.15f, 0.15f, 0.15f, 0.75f), 15) // 15px corner radius
            )));
            craftingNavigationBackground.setSize(250, 600); // Placeholder size
            stage.addActor(craftingNavigationBackground); // Add before crafting navigation UI
        }
    }

    private void createButtonStyle() {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(
                createRoundedTexture(150, 50, new Color(0.6f, 0.4f, 0.2f, 1), 10) // 10px rounded corners
        ));
        // Apply the style to your buttons
    }

    private Texture createRoundedTexture(int width, int height, Color color, int cornerRadius) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);

        // Clear the pixmap
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // Fill the rounded rectangle
        pixmap.setColor(color);

        // Draw the rounded body
        pixmap.fillRectangle(cornerRadius, 0, width - 2 * cornerRadius, height);
        pixmap.fillRectangle(0, cornerRadius, width, height - 2 * cornerRadius);

        // Draw the four rounded corners
        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, cornerRadius, cornerRadius);
        pixmap.fillCircle(cornerRadius, height - cornerRadius - 1, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius);

        // Convert Pixmap to Texture
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }


    private void populateInventoryUI() {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        inventoryTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        inventoryTable.add(new Label("INVENTORY", labelStyle)).colspan(6).pad(10).row();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 6; col++) {
                InventoryItem item = inventory.getItem(row, col);

                Stack slotStack = new Stack();
                Image slotImage = new Image(new TextureRegionDrawable(new TextureRegion(
                    createRoundedTexture(64, 64, new Color(0.3f, 0.3f, 0.3f, 1), 8))));
                slotStack.add(slotImage);

                if (item != null) {
                    Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(item.getTexture().texture)));
                    itemImage.setSize(48, 48);
                    slotStack.add(itemImage);

                    if (item.getQuantity() > 1) {
                        Label countLabel = new Label(String.valueOf(item.getQuantity()), labelStyle);
                        countLabel.setFontScale(1.0f);
                        countLabel.setAlignment(Align.bottomRight);
                        slotStack.add(countLabel);
                    }
                }

                final int finalRow = row;
                final int finalCol = col;

                slotStack.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        handleInventoryClick(finalRow, finalCol, event);
                    }
                });

                inventoryTable.add(slotStack).size(64, 64).pad(5);
            }
            inventoryTable.row();
        }
    }

    private void populateCraftingUI(String category) {
        CraftingComponent crafting = player.getComponent(CraftingComponent.class);

        craftingTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        craftingTable.add(new Label("Crafting Menu - " + category, labelStyle)).colspan(2).pad(10).row();

        Map<String, Map<String, Integer>> filteredRecipes = getRecipesForCategory(category);

        for (Map.Entry<String, Map<String, Integer>> recipe : filteredRecipes.entrySet()) {
            String itemName = recipe.getKey(); // The crafted item's name
            Map<String, Integer> requirements = recipe.getValue();

            // Display crafting requirements
            StringBuilder requirementText = new StringBuilder("Requires: ");
            for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
                requirementText.append(entry.getKey()).append(" x").append(entry.getValue()).append(" ");
            }

            Label requirementLabel = new Label(requirementText.toString(), labelStyle);

            // Create button with transparency for insufficient resources
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = font;
            buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));

            TextButton craftButton = new TextButton("Craft " + itemName, buttonStyle);
            boolean canCraft = new CraftingSystem().canCraft(crafting, player.getComponent(InventoryComponent.class), itemName);
            craftButton.setDisabled(!canCraft);

            // Adjust button transparency based on craftability
            if (!canCraft) {
                craftButton.getStyle().up.setMinWidth(0.5f); // Make button semi-transparent
            }

            // Add listener to handle crafting logic
            craftButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (canCraft) {
                        boolean crafted = new CraftingSystem().craftItem(crafting, player.getComponent(InventoryComponent.class), itemName, 1);
                        if (crafted) {
                            // Check if the crafted item is a building item
                            if (isBuildingItem(itemName)) {
                                // Create a Character object for the crafted item to allow placement
                                project.project.entities.Character buildingEntity =
                                    new project.project.entities.Character(itemName);

                                buildingEntity.addComponent(new PlacementComponent(true, 1, 1));
                                buildingEntity.addComponent(new HitboxComponent(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
                                buildingEntity.addComponent(new TextureComponent(itemName.toLowerCase() + ".png", Constants.TILE_WIDTH, Constants.TILE_HEIGHT));

                                // Start placing the crafted building object
                                placementSystem.startPlacingObject(buildingEntity);

                                // Remove one instance of the item from inventory
                                player.getComponent(InventoryComponent.class).removeItemFromInventory(itemName, 1);

                                // Close the inventory and crafting menus
                                setVisible(false);
                            }
                            updateUI(); // Refresh inventory and crafting UI
                        }
                    }
                }
            });

            craftingTable.add(craftButton).size(150, 50).pad(5);
            craftingTable.row();
            craftingTable.add(requirementLabel).colspan(2).pad(5);
            craftingTable.row();
        }
    }

    /**
     * Determines if an item is a building item.
     *
     * @param itemName The name of the item to check.
     * @return True if the item is a building item, otherwise false.
     */
    private boolean isBuildingItem(String itemName) {
        // List of building items (expand as needed)
        return itemName.equalsIgnoreCase("campfire") || itemName.equalsIgnoreCase("chest");
    }


    private Map<String, Map<String, Integer>> getRecipesForCategory(String category) {
        CraftingComponent crafting = player.getComponent(CraftingComponent.class);
        Map<String, Map<String, Integer>> allRecipes = crafting.getAllRecipes();

        // Filter recipes by category
        Map<String, Map<String, Integer>> filteredRecipes = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> recipe : allRecipes.entrySet()) {
            if (getItemCategory(recipe.getKey()).equals(category)) {
                filteredRecipes.put(recipe.getKey(), recipe.getValue());
            }
        }
        return filteredRecipes;
    }

    private String getItemCategory(String itemName) {
        // Logic to determine category of the item (e.g., hardcoded mapping or item metadata)
        switch (itemName) {
            case "campfire":
            case "chest":
                return "Building";

            case "wooden_sword":
            case "stone_sword":
                return "Weapons";

            case "stone_pickaxe":
            case "stone_axe":
                return "Tools";

            case "apple":
            case "healing_potion":
                return "Consumables";

            default:
                return "Other";
        }
    }

    private void handleInventoryClick(int row, int col, InputEvent event) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        InventoryItem clickedItem = inventory.getItem(row, col);

        if (pickedUpItem == null) {
            // Picking up an item
            if (clickedItem != null) {
                // If a Building item is clicked, close the inventory and start placement
                if (clickedItem.getItemType() == ItemType.BUILDING) {
                    setVisible(false); // Close inventory UI

                    // Create the building entity to be placed
                    project.project.entities.Character buildingEntity =
                        new project.project.entities.Character(clickedItem.getName()); // Ensure the correct Character class is used

                    // Add components directly or adjust to your component system
                    buildingEntity.addComponent(new PlacementComponent(true, 1, 1));
                    buildingEntity.addComponent(new HitboxComponent(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));

                    // Start placement using ObjectPlacementSystem
                    placementSystem.startPlacingObject(buildingEntity);

                    // Remove the item from inventory
                    inventory.removeItemFromInventory(clickedItem.getName(), 1);
                    return; // Exit after starting placement
                }

                // Split stack logic (unchanged)
                if (event.getButton() == Input.Buttons.RIGHT) {
                    int halfQuantity = clickedItem.getQuantity() / 2;
                    pickedUpItem = new InventoryItem(clickedItem.getName(), halfQuantity, clickedItem.getTexturePath(), clickedItem.getItemType());
                    clickedItem.setQuantity(clickedItem.getQuantity() - halfQuantity);
                    if (clickedItem.getQuantity() == 0) {
                        inventory.clearSlot(row, col);
                    }
                } else {
                    // Pick up the entire stack
                    pickedUpItem = clickedItem;
                    inventory.clearSlot(row, col);
                }
            }
        } else {
            // Placing the picked-up item in inventory (unchanged)
            InventoryItem existingItem = inventory.getItem(row, col);

            if (existingItem == null) {
                inventory.setItem(row, col, pickedUpItem);
                pickedUpItem = null;
            } else if (existingItem.isSameType(pickedUpItem)) {
                int spaceLeft = 64 - existingItem.getQuantity();
                int mergeQuantity = Math.min(spaceLeft, pickedUpItem.getQuantity());

                existingItem.setQuantity(existingItem.getQuantity() + mergeQuantity);
                pickedUpItem.setQuantity(pickedUpItem.getQuantity() - mergeQuantity);

                if (pickedUpItem.getQuantity() <= 0) {
                    pickedUpItem = null;
                }
            } else {
                InventoryItem temp = existingItem;
                inventory.setItem(row, col, pickedUpItem);
                pickedUpItem = temp;
            }
        }

        updateUI(); // Refresh inventory and hotbar UI
        player.notifyInventoryUpdate(); // Notify inventory update
    }


    private void mergeOrSwapItems(int row, int col, InventoryItem clickedItem) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        if (clickedItem == null) {
            // If the target slot is empty, place the picked-up item there
            inventory.setItem(row, col, pickedUpItem);
            pickedUpItem = null; // Clear the picked-up item
            updateDraggingItemImage(); // Update the UI
        } else if (clickedItem.isSameType(pickedUpItem)) {
            // If the items are the same type, try to merge them
            int spaceLeft = 64 - clickedItem.getQuantity(); // Assuming a max stack size of 64
            int mergeQuantity = Math.min(spaceLeft, pickedUpItem.getQuantity());

            clickedItem.setQuantity(clickedItem.getQuantity() + mergeQuantity);
            pickedUpItem.setQuantity(pickedUpItem.getQuantity() - mergeQuantity);

            if (pickedUpItem.getQuantity() <= 0) {
                pickedUpItem = null; // Clear the picked-up item if fully merged
            }
            updateDraggingItemImage(); // Update the UI
        } else {
            // If the items are different, swap them
            InventoryItem temp = clickedItem;
            inventory.setItem(row, col, pickedUpItem);
            pickedUpItem = temp;
            updateDraggingItemImage(); // Update the UI
        }

        // Notify the UI of any changes to the inventory
        player.notifyInventoryUpdate();
    }

    private void populateHotbarUI() {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);
        hotbarTable.clear();

        float slotSize = 64; // Fixed size for hotbar slots
        float padding = 4;   // Padding between slots

        TextureRegionDrawable brownSquareDrawable = new TextureRegionDrawable(new TextureRegion(brownSquareTexture));

        for (int col = 0; col < 6; col++) { // Assuming hotbar has 6 slots
            InventoryItem item = inventory.getItem(0, col); // Hotbar uses row 0
            Stack slotStack = new Stack();

            // Add slot background
            Image slotFrame = new Image(brownSquareDrawable);
            slotStack.add(slotFrame);

            // Display only tools and consumables in the hotbar
            if (item != null && item.canPlaceInHotbar()) {
                // Add item image
                Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(item.getTexture().texture)));
                itemImage.setSize(48, 48);
                slotStack.add(itemImage);

                // Add item count label
                if (item.getQuantity() > 1) {
                    Label.LabelStyle labelStyle = new Label.LabelStyle();
                    labelStyle.font = font;

                    Label countLabel = new Label(String.valueOf(item.getQuantity()), labelStyle);
                    countLabel.setFontScale(1.0f);
                    countLabel.setAlignment(Align.bottomRight);
                    slotStack.add(countLabel);
                }
            }

            final int finalCol = col; // Needed for use inside the lambda

            // Add listener to handle hotbar slot clicks
            slotStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleInventoryClick(0, finalCol, event); // Handle clicks for hotbar slots
                }
            });

            hotbarTable.add(slotStack).size(slotSize, slotSize).pad(padding); // Add slot to table
        }

        hotbarTable.setSize((slotSize + padding) * 6, slotSize); // Assuming 6 columns
        hotbarTable.setPosition(
                (Gdx.graphics.getWidth() - hotbarTable.getWidth()) / 2f, // Center horizontally
                20 // Slightly above the bottom edge
        );

        hotbarGroup.clear(); // Remove previous elements
        hotbarGroup.addActor(hotbarTable); // Add updated hotbar to group
    }

    private void moveToFirstAvailable(InventoryItem item, int originalRow, int originalCol) {
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        // Check all rows and columns for the first available slot
        for (int row = 0; row < inventory.getRows(); row++) {
            for (int col = 0; col < inventory.getCols(); col++) {
                InventoryItem existingItem = inventory.getItem(row, col);

                if (existingItem == null) {
                    inventory.setItem(row, col, item);
                    inventory.clearSlot(originalRow, originalCol);
                    updateUI(); // Refresh inventory
                    return;
                } else if (existingItem.isSameType(item) && existingItem.getQuantity() < 64) {
                    int spaceLeft = 64 - existingItem.getQuantity();
                    int toTransfer = Math.min(spaceLeft, item.getQuantity());
                    existingItem.setQuantity(existingItem.getQuantity() + toTransfer);
                    item.setQuantity(item.getQuantity() - toTransfer);

                    if (item.getQuantity() == 0) {
                        inventory.clearSlot(originalRow, originalCol);
                        updateUI(); // Refresh inventory
                        return;
                    }
                }
            }
        }
    }

    private void updateDraggingItemImage() {
        if (pickedUpItem != null) {
            TextureComponent itemTexture = pickedUpItem.getTexture();
            draggingItemImage.setDrawable(new TextureRegionDrawable(new TextureRegion(itemTexture.texture)));
            draggingItemImage.setSize(48, 48); // Adjust size of the dragged item
            draggingItemImage.setVisible(true);

            // Dynamically follow the mouse cursor
            draggingItemImage.setPosition(Gdx.input.getX() - (draggingItemImage.getWidth() / 2f),
                    Gdx.graphics.getHeight() - Gdx.input.getY() - (draggingItemImage.getHeight() / 2f));
        } else {
            draggingItemImage.setVisible(false);
        }

        updateUI(); // Ensure the inventory reflects the changes after dragging
    }


    public void setVisible(boolean visible) {
        isVisible = visible;

        // Toggle visibility of all UI elements
        if (craftingMenuBackground != null) craftingMenuBackground.setVisible(visible);
        if (craftingNavigationBackground != null) craftingNavigationBackground.setVisible(visible);
        inventoryTable.setVisible(visible);
        craftingNavigationTable.setVisible(visible);
        craftingTable.setVisible(visible);

        // Refresh UI when inventory is shown
        if (visible) {
            updateUI();
        }

        // Also reset dragging item when hiding the UI
        if (!visible && pickedUpItem != null) {
            moveToFirstAvailable(pickedUpItem, -1, -1); // Return picked-up item to the inventory
            pickedUpItem = null;
            updateDraggingItemImage();
        }
    }

    public void updateUI() {
        if (!isVisible) return;

        // Update contents of the UI
        populateInventoryUI();
        populateHotbarUI();

        // Update positions (ensure correct alignment)
        initializeUIPositions();
    }

    private void initializeCraftingNavigationUI() {
        craftingNavigationTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        craftingNavigationTable.add(new Label("Categories", labelStyle)).pad(10).row();

        String[] categories = {"Consumables", "Tools", "Building", "Weapons", "Other"};

        for (String category : categories) {
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = font;
            buttonStyle.up = new TextureRegionDrawable(new TextureRegion(navigationTexture));

            TextButton categoryButton = new TextButton(category, buttonStyle);
            craftingNavigationTable.add(categoryButton).size(200, 50).pad(10).row();

            categoryButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    populateCraftingUI(category); // Populate recipes for the selected category
                    craftingTable.setVisible(true); // Ensure crafting table becomes visible
                }
            });
        }

        craftingNavigationTable.setSize(250, Gdx.graphics.getHeight() - 200);
        craftingNavigationTable.setPosition(50, (Gdx.graphics.getHeight() / 2f) - (craftingNavigationTable.getHeight() / 2f));
        craftingNavigationTable.setVisible(false); // Start hidden
        stage.addActor(craftingNavigationTable);
    }

    private void initializeUIPositions() {
        // Center inventory table
        inventoryTable.setPosition(
                (Gdx.graphics.getWidth() - inventoryTable.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - inventoryTable.getHeight()) / 2f + 20 // Centered, slightly higher
        );

        // Position crafting table to the right of inventory with padding
        craftingTable.setPosition(
                inventoryTable.getX() + inventoryTable.getWidth() - 500, // 30 pixels padding
                inventoryTable.getY() + 100
        );

        // Position crafting navigation to the left of inventory with padding
        craftingNavigationTable.setPosition(
                inventoryTable.getX() - craftingNavigationTable.getWidth() - 650, // 30 pixels padding
                inventoryTable.getY() - 80
        );

        // Center hotbar at the bottom
        hotbarTable.setPosition(
                (Gdx.graphics.getWidth() - hotbarTable.getWidth()) / 2f,
                20 // 20 pixels from the bottom edge
        );

        // Adjust background to fit the inventory and crafting UI
        float backgroundWidth = inventoryTable.getWidth() + craftingNavigationTable.getWidth() + craftingTable.getWidth() + 120;
        float backgroundHeight = inventoryTable.getHeight() + 80; // Extra padding for height
        craftingMenuBackground.setSize(backgroundWidth, backgroundHeight);

        // Center background behind inventory and crafting UI
        craftingMenuBackground.setSize(600, 500); // Adjust width/height as necessary to cover UI elements
        craftingMenuBackground.setPosition(
                (Gdx.graphics.getWidth() - craftingMenuBackground.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - craftingMenuBackground.getHeight()) / 2f
        );

        // Center crafting navigation background
        craftingNavigationBackground.setSize(500, 450); // Adjust width/height as necessary to cover UI elements
        craftingNavigationBackground.setPosition(
                70,
                350
        );
    }

    public void toggleCraftingNavigation(boolean visible) {
        craftingNavigationTable.setVisible(visible);
        craftingTable.setVisible(visible);
    }

    public void resize(int width, int height) {
        // Update inventory table position
        inventoryTable.setPosition(
                (Gdx.graphics.getWidth() - inventoryTable.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - inventoryTable.getHeight()) / 2f
        );

        // Update crafting navigation
        craftingNavigationTable.setPosition(50, (height / 2f) - (craftingNavigationTable.getHeight() / 2f));

        // Update crafting table
        craftingTable.setPosition((width / 2f) + 200, height / 2f);

        // Update hotbar
        hotbarTable.setPosition((width - hotbarTable.getWidth()) / 2f, 20);
        initializeUIPositions();
    }
}
